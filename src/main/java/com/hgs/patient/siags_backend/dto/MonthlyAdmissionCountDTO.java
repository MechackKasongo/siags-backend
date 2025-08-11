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

}