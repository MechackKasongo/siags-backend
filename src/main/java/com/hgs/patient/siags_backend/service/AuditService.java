package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.AuditAction;
import com.hgs.patient.siags_backend.model.AuditLog;
import com.hgs.patient.siags_backend.model.AuditResource;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.AuditLogRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour l'enregistrement des actions d'audit.
 * Permet d'enregistrer qui a fait quoi, quand et sur quelle ressource.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Enregistre une action d'audit.
     *
     * @param action     Le type d'action.
     * @param resource   La ressource concernée.
     * @param resourceId L'ID de la ressource.
     * @param details    Une description détaillée de l'action.
     */
    @Transactional
    public void logAction(AuditAction action, AuditResource resource, Long resourceId, String details) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            // Dans ce cas, l'action n'est pas effectuée par un utilisateur authentifié (ex: tâche planifiée)
            // Nous pourrions loguer avec un utilisateur système ou simplement ne rien faire.
            System.err.println("Aucun utilisateur authentifié pour l'audit.");
            return;
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur d'audit non trouvé"));

        AuditLog auditLog = new AuditLog(currentUser, action, resource, resourceId, details);
        auditLogRepository.save(auditLog);
    }
}
