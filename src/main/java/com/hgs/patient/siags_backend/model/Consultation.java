package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {
    /**
     * Représente une consultation médicale.
     * Cette entité est utilisée pour stocker les informations relatives à une consultation entre un patient et un médecin.
     * Elle inclut des détails tels que la date de la consultation, le motif, les observations, le diagnostic, le plan de traitement et d'autres notes.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le Patient (relation Many-to-One : plusieurs consultations pour un patient)
    @ManyToOne(fetch = FetchType.EAGER) // *** MODIFIEZ ICI : DE LAZY À EAGER ***
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Lien vers le Médecin (relation Many-to-One : plusieurs consultations par un médecin)
    // Assurez-vous que votre entité User peut représenter un médecin (via un rôle par exemple)
    @ManyToOne(fetch = FetchType.EAGER) // *** MODIFIEZ ICI : DE LAZY À EAGER ***
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // L'utilisateur qui est le médecin

    @Column(nullable = false)
    private LocalDateTime consultationDate;

    @Column(nullable = false)
    private String reasonForConsultation;

    @Column(length = 2000)
    private String observations;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 2000)
    private String treatmentPlan;

    @Column(length = 1000)
    private String notes;

    // Optionnel : Lien vers l'utilisateur qui a enregistré la consultation (ex: Secrétaire médicale)}
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy; // L'utilisateur qui a enregistré la consultation

    // Optionnel : Date et heure de l'enregistrement de la consultation
    @Column(nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now(); // Par défaut, l'heure actuelle
}
