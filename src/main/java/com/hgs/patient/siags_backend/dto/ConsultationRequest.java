package com.hgs.patient.siags_backend.dto;


import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class ConsultationRequest {
// Ne pas inclure l'ID ici, il est géré par le chemin de l'URL pour les PUT


    @NotNull(message = "La date de consultation est obligatoire")

    private LocalDateTime consultationDate; // Peut être omis si toujours LocalDateTime.now() dans le service


    @NotBlank(message = "Le motif de consultation est obligatoire")

    @Size(max = 500, message = "Le motif de consultation ne peut pas dépasser 500 caractères")

    private String reasonForConsultation;


    @Size(max = 1000, message = "Les observations ne peuvent pas dépasser 1000 caractères")

    private String observations;


    @Size(max = 500, message = "Le diagnostic ne peut pas dépasser 500 caractères")

    private String diagnosis;


    @Size(max = 1000, message = "Le plan de traitement ne peut pas dépasser 1000 caractères")

    private String treatmentPlan;


    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")

    private String notes;


}
