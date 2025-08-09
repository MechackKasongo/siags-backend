package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "admissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // CORRECTION ICI : Changer le type de String à Department et ajouter les annotations de relation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // Nouvelle colonne de clé étrangère pour le département
    private Department assignedDepartment; // Service assigné (maintenant un objet Department)
    private String roomNumber;
    private String bedNumber;

    @Enumerated(EnumType.STRING) // Stocke le nom de l'énumération (ex: "ACTIVE", "DISCHARGED")
    private AdmissionStatus status;
    private LocalDateTime dischargeDate;

    @Column(length = 1000)
    private String dischargeSummary;

    // Optionnel: Lien vers l'utilisateur qui a enregistré l'admission (ex: Réceptionniste)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_personnel_id")
    private User admissionPersonnel;

    // >>> AJOUTEZ CE CHAMP POUR L'ID ODOO DE L'ADMISSION
    private Long odooAdmissionId;


    // Enum pour le statut de l'admission
    public enum AdmissionStatus {
        ACTIVE,
        DISCHARGED,
        TRANSFERRED,
        CANCELLED
    }

    private String diagnosis;

    public Long getDurationInDays() {
        if (admissionDate != null && dischargeDate != null) {
            // Calcule la différence en jours entre la date d'admission et la date de sortie
            return ChronoUnit.DAYS.between(admissionDate, dischargeDate);
        }
        return null;
    }
}