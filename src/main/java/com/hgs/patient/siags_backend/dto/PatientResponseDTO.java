package com.hgs.patient.siags_backend.dto;


import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;


import java.time.LocalDate;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class PatientResponseDTO {

    private Long id;

    private String lastName;

    private String firstName;

    private LocalDate birthDate;

    private String gender;

    private String address;

    private String phoneNumber;

    private String email;

    private String recordNumber;

    private String bloodType;

    private String knownIllnesses;

    private String allergies;

}