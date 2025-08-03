package com.hgs.patient.siags_backend.payload.response;

import lombok.Data;

import java.util.List;

/**
 * DTO pour la réponse de connexion réussie.
 * Il contient le token d'authentification et les informations de l'utilisateur.
 */
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
