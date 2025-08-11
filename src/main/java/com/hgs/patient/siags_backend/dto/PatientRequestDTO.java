package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Post-name cannot exceed 100 characters")
    private String postName;

    @Size(max = 100, message = "Profession cannot exceed 100 characters")
    private String profession;

    @Size(max = 100, message = "Employer cannot exceed 100 characters")
    private String employer;

    @Size(max = 50, message = "Patient number cannot exceed 50 characters")
    private String patientNumber;

    @Size(max = 100, message = "Spouse's name cannot exceed 100 characters")
    private String spouseName;

    @Size(max = 100, message = "Spouse's profession cannot exceed 100 characters")
    private String spouseProfession;

    @Size(max = 10, message = "Gender cannot exceed 10 characters")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be 'MALE', 'FEMALE' or 'OTHER'")
    private String gender;

    @PastOrPresent(message = "Birth date cannot be in the future")
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number format is invalid")
    private String phoneNumber;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 50, message = "Record number cannot exceed 50 characters")
    private String recordNumber;

    private String bloodType;
    private String knownIllnesses;
    private String allergies;
}