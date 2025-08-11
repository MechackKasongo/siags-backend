package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.UserCreateRequest;
import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.dto.UserUpdateRequest;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
                    ERole eRole = ERole.valueOf(roleName.toUpperCase());
                    Role role = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new ResourceNotFoundException("Erreur: Le rôle " + roleName + " n'est pas trouvé."));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Erreur: Rôle '" + roleName + "' inconnu. Détail: " + e.getMessage());
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
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return convertUserToUserResponseDTO(user);
    }

    @Override
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertUserToUserResponseDTO);
    }

    @Override
    public Optional<Long> getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId);
    }

    @Override
    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable, String searchTerm) {
        if (StringUtils.hasText(searchTerm)) {
            return userRepository.findByUsernameContainingIgnoreCase(searchTerm, pageable)
                    .map(this::convertUserToUserResponseDTO);
        } else {
            return userRepository.findAll(pageable)
                    .map(this::convertUserToUserResponseDTO);
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers(String searchTerm) {
        List<User> users;
        if (StringUtils.hasText(searchTerm)) {
            users = userRepository.findByUsernameContainingIgnoreCase(searchTerm);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(this::convertUserToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() &&
                !userUpdateRequest.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new IllegalArgumentException("Erreur: L'email '" + userUpdateRequest.getEmail() + "' est déjà utilisé par un autre utilisateur!");
            }
            existingUser.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        if (userUpdateRequest.getNomComplet() != null && !userUpdateRequest.getNomComplet().isEmpty()) {
            existingUser.setNomComplet(userUpdateRequest.getNomComplet());
        }

        if (userUpdateRequest.getRoles() != null) {
            Set<Role> newRoles = new HashSet<>();
            if (!userUpdateRequest.getRoles().isEmpty()) {
                userUpdateRequest.getRoles().forEach(roleName -> {
                    try {
                        ERole eRole = ERole.valueOf(roleName.toUpperCase());
                        Role role = roleRepository.findByName(eRole)
                                .orElseThrow(() -> new ResourceNotFoundException("Erreur: Le rôle " + roleName + " n'est pas trouvé."));
                        newRoles.add(role);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Erreur: Rôle '" + roleName + "' inconnu. Détail: " + e.getMessage());
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
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertUserToUserResponseDTO(User user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);

        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());
        dto.setPermissions(permissions);

        return dto;
    }
}