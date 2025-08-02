package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class AdmissionCountByDepartmentDTO {

    private String departmentName;

    private Long admissionCount;

}

//{
//
//
/// / Ne pas inclure l'ID ici, il est géré par le chemin de l'URL pour les PUT
//
//// Ne pas inclure patientId directement ici car il sera souvent passé en paramètre de l'URL pour les créations/récupérations
//
//
//@NotNull(message = "La date d'admission est obligatoire")
//
//private LocalDateTime admissionDate; // Peut être omis si toujours LocalDateTime.now() dans le service
//
//
//@NotBlank(message = "Le motif d'admission est obligatoire")
//
//@Size(max = 500, message = "Le motif d'admission ne peut pas dépasser 500 caractères")
//
//private String reasonForAdmission;
//
//
//@Size(max = 100, message = "Le service assigné ne peut pas dépasser 100 caractères")
//
//private String assignedDepartment;
//
//
//@Size(max = 20, message = "Le numéro de chambre ne peut pas dépasser 20 caractères")
//
//private String roomNumber;
//
//
//@Size(max = 20, message = "Le numéro de lit ne peut pas dépasser 20 caractères")
//
//private String bedNumber;
//
//
//// Le statut peut être ACTIVE par défaut lors de la création, mais modifiable lors de la mise à jour
//
//@NotNull(message = "Le statut d'admission est obligatoire")
//
//private Admission.AdmissionStatus status;
//
//
//// Ces champs peuvent être null pour une nouvelle admission
//
//private LocalDateTime dischargeDate;
//
//
//@Size(max = 1000, message = "Le résumé de sortie ne peut pas dépasser 1000 caractères")
//
//private String dischargeSummary;
//
//
//// Pas besoin de admissionPersonnelUsername ici pour la requête, c'est pour la réponse
//
//}