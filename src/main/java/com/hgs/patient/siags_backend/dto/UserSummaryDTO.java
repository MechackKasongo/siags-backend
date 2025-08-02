package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class UserSummaryDTO {

    private Long id;

    private String username;

    private String nomComplet; // Si disponible et pertinent

}