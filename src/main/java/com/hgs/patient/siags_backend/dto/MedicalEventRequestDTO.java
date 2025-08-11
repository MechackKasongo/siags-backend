package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalEventRequestDTO {

    @NotNull(message = "The medical record ID is required.")
    private Long medicalRecordId;

    @NotNull(message = "The doctor ID is required.")
    private Long medecinId;

    @NotBlank(message = "The event type is required.")
    private String eventType;

    @NotBlank(message = "The description is required.")
    private String description;

    private LocalDateTime eventDate;
}