package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDate;

import java.time.LocalDateTime;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class PatientDailyRecordResponseDTO {

    private Long id;

    private PatientSummaryDTO patient; // DTO simplifié pour le patient

    private UserSummaryDTO recordedBy; // DTO simplifié pour l'utilisateur qui a enregistré

    private LocalDate recordDate;

    private String observations;

    private String medicationsAdministered;

    private Double temperature;

    private String bloodPressure;

    private Integer heartRate;

    private Integer oxygenSaturation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}