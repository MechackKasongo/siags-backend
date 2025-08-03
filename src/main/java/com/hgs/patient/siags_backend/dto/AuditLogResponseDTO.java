package com.hgs.patient.siags_backend.dto;

import com.hgs.patient.siags_backend.model.AuditAction;
import com.hgs.patient.siags_backend.model.AuditResource;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour les journaux d'audit.
 * Utilisé pour exposer les données d'audit de manière sécurisée et structurée.
 */
@Data
public class AuditLogResponseDTO {

    private Long id;
    private String username;
    private AuditAction action;
    private AuditResource resource;
    private Long resourceId;
    private String details;
    private LocalDateTime timestamp;
}
