package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.PatientDailyRecordRequestDTO;
import com.hgs.patient.siags_backend.dto.PatientDailyRecordResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PatientDailyRecordService {
    PatientDailyRecordResponseDTO createPatientDailyRecord(PatientDailyRecordRequestDTO requestDTO);

    PatientDailyRecordResponseDTO getPatientDailyRecordById(Long id);

    List<PatientDailyRecordResponseDTO> getAllPatientDailyRecords();

    Page<PatientDailyRecordResponseDTO> getAllPatientDailyRecordsPaginated(Pageable pageable);

    PatientDailyRecordResponseDTO updatePatientDailyRecord(Long id, PatientDailyRecordRequestDTO requestDTO);

    void deletePatientDailyRecord(Long id);

    // Méthodes de recherche spécifiques
    List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByPatientId(Long patientId);

    List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByRecordDate(LocalDate recordDate);

    List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByPatientIdAndRecordDate(Long patientId, LocalDate recordDate);

    List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByRecordedById(Long recordedById);
}
