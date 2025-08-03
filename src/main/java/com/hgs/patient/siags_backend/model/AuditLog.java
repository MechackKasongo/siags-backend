package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité pour le journal d'audit, enregistrant les actions des utilisateurs sur les ressources.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'utilisateur qui a effectué l'action
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Type d'action (CREATE, UPDATE, DELETE, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    // Ressource affectée (PATIENT, CONSULTATION, MEDICAL_RECORD, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditResource resource;

    // ID de la ressource affectée
    private Long resourceId;

    // Description de l'action
    @Column(columnDefinition = "TEXT")
    private String details;

    // Date et heure de l'action
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AuditLog(User user, AuditAction action, AuditResource resource, Long resourceId, String details) {
        this.user = user;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
