package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.UserCreateRequest;
import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.dto.UserUpdateRequest;
import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImp(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByUsername(userCreateRequest.getUsername())) {
            throw new IllegalArgumentException("Erreur: Le nom d'utilisateur est déjà pris!");
        }

        if (userCreateRequest.getEmail() != null && userRepository.existsByEmail(userCreateRequest.getEmail())) {
            throw new IllegalArgumentException("Erreur: L'email est déjà utilisé!");
        }

        User user = new User(
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                passwordEncoder.encode(userCreateRequest.getPassword())
        );

        user.setNomComplet(userCreateRequest.getNomComplet());
        Set<String> strRoles = userCreateRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByName(ERole.ROLE_RECEPTIONNISTE)
                    .orElseThrow(() -> new RuntimeException("Erreur: Le rôle RECEPTIONNISTE n'est pas trouvé."));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(roleName -> {
                try {
                    // CORRECTION ICI : Enlève le préfixe "ROLE_" car le frontend l'envoie déjà
                    ERole eRole = ERole.valueOf(roleName.toUpperCase());
                    Role role = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Erreur: Le rôle " + roleName + " n'est pas trouvé."));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Erreur: Rôle '" + roleName + "' inconnu. Détail: " + e.getMessage()); // Ajout du détail de l'erreur
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return convertUserToUserResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return convertUserToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur: " + username));
        return convertUserToUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertUserToUserResponseDTO);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        // Mettre à jour l'email si fourni et si différent de l'existant, en vérifiant l'unicité
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() &&
                !userUpdateRequest.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new IllegalArgumentException("Erreur: L'email '" + userUpdateRequest.getEmail() + "' est déjà utilisé par un autre utilisateur!");
            }
            existingUser.setEmail(userUpdateRequest.getEmail());
        }

        // Mettre à jour le mot de passe si fourni
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        // Mettre à jour le nom complet (si tu l'as dans ton User)
        if (userUpdateRequest.getNomComplet() != null && !userUpdateRequest.getNomComplet().isEmpty()) {
            existingUser.setNomComplet(userUpdateRequest.getNomComplet());
        }

        // Mettre à jour les rôles si fournis
        if (userUpdateRequest.getRoles() != null) {
            Set<Role> newRoles = new HashSet<>();
            if (!userUpdateRequest.getRoles().isEmpty()) {
                userUpdateRequest.getRoles().forEach(roleName -> {
                    try {
                        // CORRECTION ICI : Enlève le préfixe "ROLE_" car le frontend l'envoie déjà
                        ERole eRole = ERole.valueOf(roleName.toUpperCase());
                        Role role = roleRepository.findByName(eRole)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle " + roleName + " n'est pas trouvé."));
                        newRoles.add(role);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Erreur: Rôle '" + roleName + "' inconnu. Détail: " + e.getMessage()); // Ajout du détail de l'erreur
                    }
                });
            }
            existingUser.setRoles(newRoles);
        }

        User updatedUser = userRepository.save(existingUser);
        return convertUserToUserResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // Méthode utilitaire pour convertir un User en UserResponseDTO
    private UserResponseDTO convertUserToUserResponseDTO(User user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);

        // Map les noms des rôles manuellement, car ModelMapper peut avoir du mal avec Set<Role> -> List<String>
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name()) // Convertit ERole en String (ex: "ROLE_ADMIN")
                .collect(Collectors.toList()));
        return dto;
    }
}