package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Lien One-to-One avec le patient
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", unique = true, nullable = false)
    private Patient patient;

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
}
