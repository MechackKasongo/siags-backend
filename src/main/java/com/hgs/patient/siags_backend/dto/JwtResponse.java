package com.hgs.patient.siags_backend.dto;


import lombok.Data;

import lombok.NoArgsConstructor;


import java.util.List;


@Data

@NoArgsConstructor // Un constructeur sans arguments est utile pour la désérialisation

public class JwtResponse {

    private String token;

    private String type = "Bearer"; // Type de token

    private Long id;

    private String username;

    private String email;

    private List<String> roles; // Les rôles de l'utilisateur


// Constructeur personnalisé pour faciliter la création de la réponse

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {

        this.token = accessToken;

        this.id = id;

        this.username = username;

        this.email = email;

        this.roles = roles;

    }

}