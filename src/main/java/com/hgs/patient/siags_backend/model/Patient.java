package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Maintenu car utile pour la création d'instances

public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String lastName;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String firstName;

    // Si 'gender' doit être une Enum directement dans Patient, changez le type de String à Gender
    // Si c'est une String qui sera mappée à une Enum Odoo, laissez String et ajustez le mapping
    @Column(length = 10)
    @Size(max = 10, message = "Le genre ne doit pas dépasser 10 caractères")
    private String gender; // Ou 'private Gender gender;' si vous voulez une enum ici

    @Column(name = "birth_date")
    @PastOrPresent(message = "La date de naissance ne peut pas être dans le futur")
    private LocalDate birthDate;

    @Column(length = 150)
    @Size(max = 150, message = "L'adresse ne doit pas dépasser 150 caractères")
    private String address;

    @Column(length = 100)
    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    private String city;

    @Column(name = "zip_code", length = 20) // Nom de colonne explicite pour zipCode
    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    private String zipCode;

    @Column(name = "phone_number", length = 20)
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;

    @Column(name = "record_number", unique = true, length = 50)
    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 50, message = "Le numéro de dossier ne doit pas dépasser 50 caractères")
    private String recordNumber;

    @Column(length = 100)
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @Column(name = "blood_type", length = 15) // Longueur augmentée pour les noms d'Enum longs
    private String bloodType; // Ou 'private BloodType bloodType;' si vous voulez une enum ici

    @Column(name = "known_illnesses", columnDefinition = "TEXT")
    private String knownIllnesses;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "odoo_contact_id")
    private Long odooContactId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
