package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.payload.request.LoginRequest;
import com.hgs.patient.siags_backend.payload.request.SignupRequest;
import com.hgs.patient.siags_backend.payload.response.JwtResponse;
import com.hgs.patient.siags_backend.payload.response.MessageResponse;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.jwt.JwtUtils;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de l'authentification et de l'inscription.
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Authentifie un utilisateur et génère un token JWT.
     *
     * @param loginRequest Les informations de connexion.
     * @return Une réponse JWT avec le token et les informations de l'utilisateur.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * @param signUpRequest Les informations d'inscription.
     * @return Un message de confirmation de la création du compte.
     */
    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Erreur : Ce nom d'utilisateur est déjà pris !");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Erreur : Cette adresse e-mail est déjà utilisée !");
        }

        // Créer le nouvel utilisateur
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_RECEPTIONNISTE)
                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(adminRole);
                        break;
                    case "medecin":
                        Role medecinRole = roleRepository.findByName(ERole.ROLE_MEDECIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(medecinRole);
                        break;
                    case "infirmier":
                        Role infirmierRole = roleRepository.findByName(ERole.ROLE_INFIRMIER)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(infirmierRole);
                        break;
                    case "personnel_admin_sortie":
                        Role personnelRole = roleRepository.findByName(ERole.ROLE_PERSONNEL_ADMIN_SORTIE)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(personnelRole);
                        break;
                    default:
                        Role defaultRole = roleRepository.findByName(ERole.ROLE_RECEPTIONNISTE)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(defaultRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("Utilisateur enregistré avec succès !");
    }
}
