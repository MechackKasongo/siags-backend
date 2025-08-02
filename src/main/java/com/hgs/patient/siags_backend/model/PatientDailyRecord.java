
package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_daily_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDailyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient; // Lien vers le patient concerné

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id", nullable = false)
    private User recordedBy; // Lien vers l'utilisateur qui a enregistré l'entrée (Médecin, Infirmier)

    @Column(nullable = false)
    private LocalDate recordDate; // Date de l'enregistrement quotidien

    @Column(columnDefinition = "TEXT")
    private String observations; // Observations générales sur l'état du patient

    @Column(columnDefinition = "TEXT")
    private String medicationsAdministered; // Médicaments administrés ce jour-là

    // Champs pour les signes vitaux (options, peuvent être détaillés ou modifiés selon besoin)
    private Double temperature; // Température en Celsius
    private String bloodPressure; // Pression artérielle (ex: "120/80")
    private Integer heartRate; // Fréquence cardiaque
    private Integer oxygenSaturation; // Saturation en oxygène (%)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Date et heure de création de l'enregistrement
    private LocalDateTime updatedAt; // Date et heure de la dernière mise à jour

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
