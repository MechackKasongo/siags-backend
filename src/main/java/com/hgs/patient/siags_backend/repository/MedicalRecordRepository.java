package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité MedicalRecord.
 * Permet les opérations CRUD sur les dossiers médicaux.
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Trouve un dossier médical par l'ID du patient associé.
     *
     * @param patientId L'ID du patient.
     * @return Un Optional contenant le MedicalRecord s'il existe.
     */
    Optional<MedicalRecord> findByPatientId(Long patientId);
}
