package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data // Lombok génère automatiquement les getters, setters, toString, equals et hashCode
@NoArgsConstructor // Ajouté pour un constructeur par défaut nécessaire à JPA
@AllArgsConstructor // Utile pour créer facilement des objets Patient
public class Patient {

    @Id // Indique que ce champ est la clé primaire de l'entité
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique du patient

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @Column(length = 10) // Longueur max 10 caractères pour le sexe (ex: "MASCULIN", "FEMININ")
    @Size(max = 10, message = "Le sexe ne doit pas dépasser 10 caractères")
    private String genre;

    @Column(name = "date_naissance")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateDeNaissance;

    @Column(length = 150)
    @Size(max = 150, message = "L'adresse ne doit pas dépasser 150 caractères")
    private String adresse;

    @Column(length = 20)
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;

    @Column(unique = true, length = 50) // Doit être unique dans la base de données
    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 50, message = "Le numéro de dossier ne doit pas dépasser 50 caractères")
    private String numeroDossier; // Numéro de dossier unique généré par l'Agent d'Admission

    // Champs manquants ajoutés :
    @Column(length = 100)
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @Column(name = "type_de_sang", length = 5)
    @Size(max = 5, message = "Le type de sang ne doit pas dépasser 5 caractères")
    private String typeDeSang;

    @Column(name = "maladies_connues", columnDefinition = "TEXT")
    private String maladiesConnues;

    @Column(columnDefinition = "TEXT")
    private String allergies;

}