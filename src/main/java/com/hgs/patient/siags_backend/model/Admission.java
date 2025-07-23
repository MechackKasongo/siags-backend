package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admissions") // Nom de la table dans la base de données
@Data // Génère les getters, setters, toString, equals, hashCode (Lombok)
@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)
@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le Patient (relation Many-to-One : plusieurs admissions pour un patient)
    @ManyToOne(fetch = FetchType.LAZY) // Charge le patient uniquement quand il est accédé
    @JoinColumn(name = "patient_id", nullable = false) // Colonne de la clé étrangère dans la table "admissions"
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime admissionDate; // Date et heure de l'admission

    @Column(nullable = false)
    private String reasonForAdmission; // Motif de l'admission

    private String assignedDepartment; // Service assigné (ex: Urgences, Pédiatrie)
    private String roomNumber;         // Numéro de chambre
    private String bedNumber;          // Numéro de lit

    @Enumerated(EnumType.STRING) // Stocke le nom de l'énumération (ex: "ACTIVE", "DISCHARGED")
    private AdmissionStatus status;

    private LocalDateTime dischargeDate;
    @Column(length = 1000)
    private String dischargeSummary;

    // Optionnel: Lien vers l'utilisateur qui a enregistré l'admission (ex: Réceptionniste)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_personnel_id")
    private User admissionPersonnel;

    // Enum pour le statut de l'admission
    public enum AdmissionStatus {
        ACTIVE,
        DISCHARGED,
        TRANSFERRED,
        CANCELLED
    }
}