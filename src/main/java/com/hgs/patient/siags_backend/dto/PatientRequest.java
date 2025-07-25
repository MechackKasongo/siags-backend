package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Génère les méthodes getter, setter, equals, hashCode et toString
@NoArgsConstructor
@AllArgsConstructor // Génère un constructeur avec tous les champs
public class PatientRequest {

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String lastName; // Renommé de 'nom'

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String firstName; // Renommé de 'prenom'

    // Changement de 'sexe' à 'genre' pour la cohérence
    @Size(max = 10, message = "Le genre ne peut pas dépasser 10 caractères")
    @Pattern(regexp = "MASCULIN|FEMININ|AUTRE", message = "Le genre doit être 'MASCULIN', 'FEMININ' ou 'AUTRE'")
    private String gender; // Renommé de 'genre'

    // Changement de 'dateNaissance' à 'dateDeNaissance' pour la cohérence
    @PastOrPresent(message = "La date de naissance ne peut pas être dans le futur")
    // Gardé PastOrPresent pour cohérence avec Patient entity
    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate birthDate; // Renommé de 'dateDeNaissance'

    @Size(max = 150, message = "L'adresse ne peut pas dépasser 150 caractères")
    private String address; // Renommé de 'adresse'

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Le format du téléphone est invalide")
    private String phoneNumber; // Renommé de 'telephone'

    // Ajout du champ email
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 50, message = "Le numéro de dossier ne peut pas dépasser 50 caractères")
    private String recordNumber; // Renommé de 'numeroDossier'

    // Ajout des champs TypeDeSang, MaladiesConnues, Allergies
    @Size(max = 5, message = "Le type de sang ne doit pas dépasser 5 caractères")
    private String bloodType; // Renommé de 'typeDeSang'

    private String knownIllnesses; // Renommé de 'maladiesConnues'

    private String allergies; // Nom déjà en anglais
}