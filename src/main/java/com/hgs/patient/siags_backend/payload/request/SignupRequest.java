package com.hgs.patient.siags_backend.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * DTO pour les requêtes d'inscription d'utilisateur.
 * Contient les informations nécessaires pour créer un nouvel utilisateur.
 */
@Data
public class SignupRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Size(max = 50)
    @Email(message = "L'adresse e-mail doit être valide.")
    private String email;

    private Set<String> role;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, max = 40)
    private String password;
}
