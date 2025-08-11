package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant le dossier médical d'un patient.
 * Elle centralise l'ensemble des informations médicales importantes.
 * Un patient a un et un seul dossier médical.
 */
@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Patient patient;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MedicalEvent> medicalEvents;

    // Champs de date gérés automatiquement
    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // Antécédents médicaux
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    // Antécédents familiaux
    @Column(columnDefinition = "TEXT")
    private String familyHistory;

    // Allergies
    @Column(columnDefinition = "TEXT")
    private String allergies;

    // Médicaments actuels
    @Column(columnDefinition = "TEXT")
    private String currentMedications;

    // Notes générales
    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}