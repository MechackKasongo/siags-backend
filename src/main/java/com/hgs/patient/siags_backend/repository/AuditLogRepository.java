package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité AuditLog.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByResourceId(Long resourceId);
}
