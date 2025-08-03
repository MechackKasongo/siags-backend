package com.hgs.patient.siags_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour les requêtes de connexion.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String password;

}