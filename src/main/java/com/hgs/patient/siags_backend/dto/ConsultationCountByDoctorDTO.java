
package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class ConsultationCountByDoctorDTO {

    private Long doctorId;

    private String doctorUsername;

    private String doctorNomComplet;

    private Long consultationCount;

}