package com.hgs.patient.siags_backend.dto;


import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.util.Set;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class SignupRequest {


    @NotBlank(message = "Le nom d'utilisateur est obligatoire")

    @Size(min = 3, max = 20, message = "Le nom d'utilisateur doit contenir entre 3 et 20 caractères")

    private String username;


    @NotBlank(message = "L'adresse email est obligatoire")

    @Size(max = 50, message = "L'adresse email ne doit pas dépasser 50 caractères")

    @Email(message = "Le format de l'adresse email est invalide")

    private String email;


    @NotBlank(message = "Le mot de passe est obligatoire")

    @Size(min = 6, max = 40, message = "Le mot de passe doit contenir entre 6 et 40 caractères")

    private String password;


    private Set<String> role;

}