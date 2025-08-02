package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class OdooAdmissionDTO {


// L'ID interne de l'admission dans SIAGS, utile pour le mapping dans Odoo

    private Long siagsAdmissionId;

    private Long odooPatientContactId;

    private String patientName;

    private String reasonForAdmission;

    private String departmentName;

    private String status;

    private Long odooAdmissionRecordId; // Cet ID sera l'ID Odoo de l'enregistrement d'admission


    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;


// NOUVEAUX CHAMPS AJOUTÉS

    private String diagnosis;

    private String roomNumber;

    private String bedNumber;

}