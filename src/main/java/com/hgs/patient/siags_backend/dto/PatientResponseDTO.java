package com.hgs.patient.siags_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private Long id;
    private String last_name;
    private String first_name;
    private String post_name;
    private String profession;
    private String employer;
    private String patient_number;
    private String spouse_name;
    private String spouse_profession;
    private LocalDate birth_date;
    private String gender;
    private String address;
    private String city;
    private String phone_number;
    private String email;
    private String record_number;
    private String blood_type;
    private String known_illnesses;
    private String allergies;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}