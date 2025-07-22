package com.hgs.patient.siags_backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    // Les annotations de validation sont reportées ici car c'est le DTO qui sera validé
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @Size(max = 10, message = "Le sexe ne peut pas dépasser 10 caractères")
    @Pattern(regexp = "MASCULIN|FEMININ|AUTRE", message = "Le sexe doit être 'MASCULIN', 'FEMININ' ou 'AUTRE'")
    private String sexe;

    @PastOrPresent(message = "La date de naissance ne peut pas être dans le futur")
    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @Size(max = 150, message = "L'adresse ne peut pas dépasser 150 caractères")
    private String adresse;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Le format du téléphone est invalide")
    private String telephone;

    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 50, message = "Le numéro de dossier ne peut pas dépasser 50 caractères")
    private String numeroDossier;
}