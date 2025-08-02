package com.hgs.patient.siags_backend.dto;

import com.hgs.patient.siags_backend.model.Admission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Génère les getters, setters, toString, equals et hashCode
@NoArgsConstructor // Génère un constructeur sans arguments
@AllArgsConstructor // Génère un constructeur avec tous les arguments
public class AdmissionRequestDTO {

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId; // Ajouté car utilisé dans AdmissionServiceImp

    @NotNull(message = "La date d'admission est obligatoire")
    private LocalDateTime admissionDate;

    @NotBlank(message = "Le motif d'admission est obligatoire")
    @Size(max = 500, message = "Le motif d'admission ne peut pas dépasser 500 caractères")
    private String reasonForAdmission;

    @NotNull(message = "L'ID du département assigné est obligatoire")
    private Long departmentId;

    @Size(max = 20, message = "Le numéro de chambre ne peut pas dépasser 20 caractères")
    private String roomNumber;

    @Size(max = 20, message = "Le numéro de lit ne peut pas dépasser 20 caractères")
    private String bedNumber;

    @NotNull(message = "Le statut d'admission est obligatoire")
    private Admission.AdmissionStatus status;

    // Ces champs peuvent être null pour une nouvelle admission ou mis à jour plus tard
    private LocalDateTime dischargeDate;

    @Size(max = 500, message = "Le diagnostic ne peut pas dépasser 500 caractères")
    private String diagnosis;

    @Size(max = 1000, message = "Le résumé de sortie ne peut pas dépasser 1000 caractères")
    private String dischargeSummary;
}