package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.util.List;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class UserResponseDTO {

    private Long id;

    private String username;

    private String email;

    private String nomComplet;

    private List<String> roles; // Liste des noms de rôles

}