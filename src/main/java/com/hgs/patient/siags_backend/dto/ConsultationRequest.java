package com.hgs.patient.siags_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO pour la création ou la mise à jour d'une consultation.
 */
@Setter
@Getter
public class ConsultationRequest {

    @NotNull(message = "L'ID de l'admission ne peut pas être nul")
    private Long admissionId;
    private Long patientId;
    private Long doctorId;

    // CORRECTION : Le pattern est maintenant 'yyyy-MM-dd'T'HH:mm'
    @NotNull(message = "La date de consultation ne peut pas être nulle")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime consultationDate;

    @NotBlank(message = "La raison de la consultation est requise")
    private String reasonForConsultation;
    private String observations;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
}