package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.AuditLogResponseDTO;
import com.hgs.patient.siags_backend.model.AuditLog;
import com.hgs.patient.siags_backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des journaux d'audit.
 * Il permet aux utilisateurs autorisés (administrateurs) de consulter l'historique des actions.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    private AuditLogResponseDTO mapToDTO(AuditLog log) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(log.getId());
        dto.setUsername(log.getUser().getUsername());
        dto.setAction(log.getAction());
        dto.setResource(log.getResource());
        dto.setResourceId(log.getResourceId());
        dto.setDetails(log.getDetails());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }

    /**
     * Récupère tous les journaux d'audit.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<AuditLogResponseDTO>> getAllAuditLogs() {
        List<AuditLogResponseDTO> logs = auditLogRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    /**
     * Récupère les journaux d'audit pour un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogsByUser(@PathVariable Long userId) {
        List<AuditLogResponseDTO> logs = auditLogRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
