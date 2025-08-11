package com.hgs.patient.siags_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordResponseDTO {

    private Long id;
    private Long patientId;
    private LocalDateTime createdDate;
    private List<MedicalEventResponseDTO> medicalEvents;


}