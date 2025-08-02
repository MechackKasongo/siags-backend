package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class DiagnosisFrequencyDTO {

    private String diagnosis; // Le diagnostic

    private Long count;

}