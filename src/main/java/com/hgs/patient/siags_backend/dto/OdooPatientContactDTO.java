package com.hgs.patient.siags_backend.dto;


import com.hgs.patient.siags_backend.model.BloodType;

import com.hgs.patient.siags_backend.model.Gender;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDate;


// Ce DTO représente les données d'un contact/partenaire Odoo pour un patient

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class OdooPatientContactDTO {


// ID interne du patient dans SIAGS, utile pour le mapping dans Odoo si Odoo peut stocker un ID externe

    private Long siagsPatientId;


    private String name; // Nom complet du patient (souvent Nom + Prénom dans Odoo)

    private String email;

    private String phone;

    private String mobile;

    private String street;

    private String city;

    private String zip; // Code postal

    private String country; // Nom du pays (ex: "Democratic Republic of the Congo")


// Champs spécifiques à Odoo pour un contact

    private Boolean isCompany = false;

    private String type = "contact";


// Optionnel: ID Odoo si le contact existe déjà dans Odoo (pour les mises à jour)

    private Long odooContactId;


// Optionnel: Statut du contact si Odoo gère un statut lié à l'admission/sortie

    private String status;


    private LocalDate birthDate;

    private Gender gender;

    private String recordNumber;

    private BloodType bloodType;

    private String knownIllnesses;

    private String allergies;

}