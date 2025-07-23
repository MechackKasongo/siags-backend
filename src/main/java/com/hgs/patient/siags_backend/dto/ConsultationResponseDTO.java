package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Génère les getters, setters, toString, equals, hashCode (Lombok)
@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)
@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)
public class ConsultationResponseDTO {

    private Long id;
    private Long patientId; // ID du patient
    private String patientNomComplet; // Nom complet du patient pour l'affichage

    private Long doctorId; // ID du médecin (User)
    private String doctorNomComplet; // Nom complet du médecin (Username ou Nom Prenom si vous avez ces champs sur User)

    private LocalDateTime consultationDate;
    private String reasonForConsultation;
    private String observations;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;

//
//    // Vous pouvez ajouter d'autres champs si nécessaire, par exemple :
//    // private String prescriptions; // Si vous souhaitez inclure des prescriptions sous forme de texte
//    // private String medicalExams; // Si vous souhaitez inclure des examens médicaux sous forme de texte
//    private String status; // Statut de la consultation (par exemple "EN_COURS", "TERMINEE", etc.)
//    // Vous pouvez utiliser un enum pour le statut si vous avez défini un dans l'entité Consultation.
//    private LocalDateTime createdAt; // Date de création de la consultation
//    private LocalDateTime updatedAt; // Date de dernière mise à jour de la consultation
//    // Notez que les dates de création et de mise à jour sont optionnelles
//    // et peuvent être gérées par le système ou laissées null si non utilisées.
//    private String createdBy; // Nom d'utilisateur de la personne qui a créé la consultation

}