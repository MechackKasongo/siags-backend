package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.JwtResponse;
import com.hgs.patient.siags_backend.dto.LoginRequest;
import com.hgs.patient.siags_backend.dto.SignupRequest;
import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.jwt.JwtUtils;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//@CrossOrigin(origins = "*", maxAge = 3600) // Permet les requêtes Cross-Origin (CORS)
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin") // Endpoint pour la connexion
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

// 1. Authentifier l'utilisateur via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


// 2. Mettre à jour le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);


// 3. Générer le jeton JWT
        String jwt = jwtUtils.generateJwtToken(authentication);


// 4. Récupérer les détails de l'utilisateur et ses rôles
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // Ligne 107
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());


// 5. Renvoyer la réponse JWT
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));

    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent voir la liste des rôles
    public ResponseEntity<?> getAvailableRoles() {
        // Cette liste devrait venir de ta base de données ou d'une constante
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR", "ROLE_PATIENT"); // Exemple
        // Ou si tu as un service de rôle qui récupère tous les noms de rôles :
        // List<String> roles = roleService.getAllRoleNames();
        return ResponseEntity.ok(roles);
    }


    @PostMapping("/signup") // Endpoint pour l'inscription
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

// 1. Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur : Le nom d'utilisateur est déjà pris !");
        }


// 1. Vérifier sil'email existe déjà (si vous gérez l'email dans User)
// 2. Vérifier si l'email existe déjà (si vous gérez l'email dans User)
// if (userRepository.existsByEmail(signUpRequest.getEmail())) {
// return ResponseEntity
// .badRequest()
// .body("Erreur : L'email est déjà utilisé !");
// }


// 3. Créer un nouvel utilisateur
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(), // Ou null si l'email n'est pas toujours envoyé
                encoder.encode(signUpRequest.getPassword())
        );


// 4. Attribuer les rôles à l'utilisateur
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
// Si aucun rôle n'est spécifié, attribuer un rôle par défaut (ex: ROLE_RECEPTIONNISTE)
            Role userRole = roleRepository.findByName(ERole.ROLE_RECEPTIONNISTE)
                    .orElseThrow(() -> new RuntimeException("Erreur: Le rôle RECEPTIONNISTE n'est pas trouvé."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle ADMIN n'est pas trouvé."));
                        roles.add(adminRole);
                        break;
                    case "medecin":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MEDECIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle MEDECIN n'est pas trouvé."));
                        roles.add(modRole);
                        break;
                    case "infirmier":
                        Role userRole = roleRepository.findByName(ERole.ROLE_INFIRMIER)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle INFIRMIER n'est pas trouvé."));
                        roles.add(userRole);
                        break;
                    case "personnel_admin_sortie":
                        Role personnelRole = roleRepository.findByName(ERole.ROLE_PERSONNEL_ADMIN_SORTIE)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle PERSONNEL_ADMIN_SORTIE n'est pas trouvé."));
                        roles.add(personnelRole);
                        break;
                    default:
// Si un rôle inconnu est spécifié, vous pouvez soit le rejeter, soit attribuer un rôle par défaut
                        Role defaultRole = roleRepository.findByName(ERole.ROLE_RECEPTIONNISTE)
                                .orElseThrow(() -> new RuntimeException("Erreur: Le rôle RECEPTIONNISTE n'est pas trouvé."));
                        roles.add(defaultRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);


        return ResponseEntity.ok("Utilisateur enregistré avec succès !");

    }

}