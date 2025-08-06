package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String recordNumber;
    private String prenom;
}
