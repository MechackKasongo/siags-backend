package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(max = 50, message = "L'email ne doit pas dépasser 50 caractères")
    @Email(message = "Le format de l'email est invalide")
    private String email;

    @Size(min = 6, max = 40, message = "Le mot de passe doit contenir entre 6 et 40 caractères (si modifié)")
    private String password; // Le mot de passe peut être modifié
    private String nomComplet;
    private Set<String> roles; // Les rôles peuvent être modifiés

}