package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AdmissionResponseDTO {

    private Long id;
// Informations du patient via un DTO simplifié
    private PatientSummaryDTO patient;
// Informations du département via un DTO simplifié
    private DepartmentSummaryDTO department;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String reasonForAdmission;
    private String diagnosis;
    private String status;
    private Long durationInDays;
    private String roomNumber;
    private String bedNumber;
    private String dischargeSummary;
    private UserSummaryDTO admissionPersonnel;

}