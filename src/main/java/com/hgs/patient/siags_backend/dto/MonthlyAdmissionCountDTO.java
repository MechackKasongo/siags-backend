package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Cette annotation génère le constructeur (Integer month, Long count)
public class MonthlyAdmissionCountDTO {
    private Integer month;
    private Long count;

    // NE PAS AJOUTER manuellement le constructeur si @AllArgsConstructor est présent,
    // sauf si vous avez une raison très spécifique et gérez la génération de Lombok.
    // public MonthlyAdmissionCountDTO(Integer month, Long count) {
    //     this.month = month;
    //     this.count = count;
    // }
}