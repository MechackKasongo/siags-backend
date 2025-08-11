package com.hgs.patient.siags_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalEventResponseDTO {
    private Long id;
    private Long medecinId;
    private String medecinName;
    private String eventType;
    private String description;
    private LocalDateTime eventDate;
}