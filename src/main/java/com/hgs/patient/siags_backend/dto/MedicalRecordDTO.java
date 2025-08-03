package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour les dossiers médicaux.
 * Permet de transférer les données du dossier médical sans exposer l'entité complète.
 */
@Data
public class MedicalRecordDTO {

    private Long id;

    @NotNull(message = "L'ID du patient est obligatoire.")
    private Long patientId;

    private String medicalHistory;
    private String familyHistory;
    private String allergies;
    private String currentMedications;
    private String notes;
}
