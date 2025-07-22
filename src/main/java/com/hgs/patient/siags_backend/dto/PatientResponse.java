package com.hgs.patient.siags_backend.dto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String sexe;
    private LocalDate dateNaissance;
    private String adresse;
    private String telephone;
    private String numeroDossier;

}