package com.hgs.patient.siags_backend.model;
import jakarta.persistence.*; // Pour les annotations JPA
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*; // Ajout pour les annotations de validation

@Entity
@Table(name = "patients")
@Data // Lombok génère automatiquement les getters, setters, toString, equals et hashCode

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
    private String sexe;

    @Column(name = "date_naissance")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

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

    /*
    public Patient() {
        // Constructeur par défaut
    }

    // Getters et Setters pour chaque champ
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... et ainsi de suite pour nom, prenom, etc.
    */

}
