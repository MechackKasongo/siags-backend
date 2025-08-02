package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

import java.util.List;

import java.util.Map; // Pour les détails de validation


@Data // Génère getters, setters, toString, equals, hashCode

@NoArgsConstructor // Génère un constructeur sans arguments

@AllArgsConstructor // Génère un constructeur avec tous les arguments

public class ErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> errors; // Pour les erreurs de validation spécifiques par champ

}