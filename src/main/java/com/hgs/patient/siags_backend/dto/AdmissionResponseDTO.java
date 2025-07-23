package com.hgs.patient.siags_backend.dto;

import com.hgs.patient.siags_backend.model.Admission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionResponseDTO {

    private Long id;
    private Long patientId; // Seulement l'ID du patient
    private String patientNomComplet; // Nom complet du patient pour faciliter la lecture

    private LocalDateTime admissionDate;
    private String reasonForAdmission;
    private String assignedDepartment;
    private String roomNumber;
    private String bedNumber;
    private Admission.AdmissionStatus status; // Utilisez l'enum de l'entité
    private LocalDateTime dischargeDate;
    private String dischargeSummary;
    private String admissionPersonnelUsername; // Nom d'utilisateur du personnel si applicable
}