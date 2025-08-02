package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.*; // Importez tous les DTOs de rapport

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    // Rapports sur les patients
    Long getTotalPatientsCount();

    List<PatientGenderDistributionDTO> getPatientGenderDistribution();

    // Rapports sur les admissions
    Long getTotalAdmissionsCount();

    Long getAdmissionsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<AdmissionCountByDepartmentDTO> getAdmissionCountByDepartment();

    List<MonthlyAdmissionCountDTO> getMonthlyAdmissionCountForYear(int year);

    // Rapports sur les consultations
    Long getTotalConsultationsCount();

    Long getConsultationsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<ConsultationCountByDoctorDTO> getConsultationCountByDoctor();

    List<DiagnosisFrequencyDTO> getDiagnosisFrequency();
}
