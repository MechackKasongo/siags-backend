package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.PatientDailyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientDailyRecordRepository extends JpaRepository<PatientDailyRecord, Long> {

    // Méthode pour trouver les enregistrements d'un patient spécifique
    List<PatientDailyRecord> findByPatientId(Long patientId);

    // Méthode pour trouver les enregistrements d'un patient pour une date spécifique
    List<PatientDailyRecord> findByPatientIdAndRecordDate(Long patientId, LocalDate recordDate);

    // Méthode pour trouver les enregistrements effectués par un utilisateur spécifique
    List<PatientDailyRecord> findByRecordedById(Long recordedById);

    List<PatientDailyRecord> findByRecordDate(LocalDate recordDate);
}