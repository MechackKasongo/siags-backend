package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateDeNaissance; // Renommé pour correspondre à l'entité Patient
    private String genre; // Renommé pour correspondre à l'entité Patient
    private String adresse;
    private String telephone;
    private String email; // Ajouté si manquant, car il est dans Patient
    private String numeroDossier;
    private String typeDeSang; // Ajouté si manquant
    private String maladiesConnues; // Ajouté si manquant
    private String allergies; // Ajouté si manquant
}