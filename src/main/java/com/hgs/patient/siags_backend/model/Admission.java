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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Le champ 'doctor' a été renommé 'medecin' comme demandé
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id") // Le nom de la colonne de la clé étrangère a également été mis à jour
    private User medecin;

    @Column(nullable = false)
    private LocalDateTime admissionDate;

    @Column(nullable = false)
    private String reasonForAdmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department assignedDepartment;
    private String roomNumber;
    private String bedNumber;

    @Enumerated(EnumType.STRING) // Stocke le nom de l'énumération (ex: "ACTIVE", "DISCHARGED")
    private AdmissionStatus status;
    private LocalDateTime dischargeDate;

    @Column(length = 1000)
    private String dischargeSummary;

    // Optionnel: Lien vers l'utilisateur qui a enregistré l'admission
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

    private String diagnosis;

    public Long getDurationInDays() {
        if (admissionDate != null && dischargeDate != null) {
            // Calcule la différence en jours entre la date d'admission et la date de sortie
            return ChronoUnit.DAYS.between(admissionDate, dischargeDate);
        }
        return null;
    }
}
