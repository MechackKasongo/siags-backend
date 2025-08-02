package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data // Génère les getters, setters, toString, equals, hashCode (Lombok)

@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)

@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)

public class ConsultationResponseDTO {


    private Long id;

    private Long patientId;

    private String patientNomComplet;


    private Long doctorId;

    private String doctorNomComplet;


    private LocalDateTime consultationDate;

    private String reasonForConsultation;

    private String observations;

    private String diagnosis;

    private String treatmentPlan;

    private String notes;


    private Long recordedById;

    private String recordedByUsername;

    private LocalDateTime recordedAt;


}