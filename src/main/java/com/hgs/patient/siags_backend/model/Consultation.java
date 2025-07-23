package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations") // Nom de la table dans la base de données
@Data
@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)
@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le Patient (relation Many-to-One : plusieurs consultations pour un patient)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Lien vers le Médecin (relation Many-to-One : plusieurs consultations par un médecin)
    // Assurez-vous que votre entité User peut représenter un médecin (via un rôle par exemple)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // L'utilisateur qui est le médecin

    @Column(nullable = false)
    private LocalDateTime consultationDate; // Date et heure de la consultation

    @Column(nullable = false)
    private String reasonForConsultation; // Motif de la consultation (ex: suivi, nouvelle douleur)

    @Column(length = 2000)
    private String observations;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 2000)
    private String treatmentPlan; // Plan de traitement proposé

    @Column(length = 1000) // Notes additionnelles
    private String notes;

    // Optionnel: Liens vers les prescriptions et examens (relations One-to-Many)
    // Ces collections seront gérées dans leurs propres entités si elles deviennent complexes.
    // Pour l'instant, on les met en commentaire ou on les inclut si vous voulez gérer des sous-objets directement.
    /*
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prescription> prescriptions = new HashSet<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MedicalExam> medicalExams = new HashSet<>();
    */
}