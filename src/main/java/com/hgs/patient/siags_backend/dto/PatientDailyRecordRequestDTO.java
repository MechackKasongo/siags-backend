package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data

@NoArgsConstructor

@AllArgsConstructor

public class PatientDailyRecordRequestDTO {


    @NotNull(message = "L'ID du patient est obligatoire")

    private Long patientId; // ID du patient lié à l'enregistrement


    @NotNull(message = "La date d'enregistrement est obligatoire")

    @PastOrPresent(message = "La date d'enregistrement ne peut pas être future")

    private LocalDate recordDate;


    @NotBlank(message = "Les observations ne peuvent pas être vides")

    @Size(max = 2000, message = "Les observations ne doivent pas dépasser 2000 caractères")

    private String observations;


    @Size(max = 1000, message = "Les médicaments administrés ne doivent pas dépasser 1000 caractères")

    private String medicationsAdministered;


    private Double temperature;

    private String bloodPressure;

    private Integer heartRate;
    private Integer oxygenSaturation;

}