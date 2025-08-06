package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    Long getTotalPatientsCount();
    List<PatientGenderDistributionDTO> getPatientGenderDistribution();
    Long getTotalAdmissionsCount();
    Long getAdmissionsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    List<AdmissionCountByDepartmentDTO> getAdmissionCountByDepartment();
    List<MonthlyAdmissionCountDTO> getMonthlyAdmissionCountForYear(int year);
    Long getTotalConsultationsCount();
    Long getConsultationsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    List<ConsultationCountByDoctorDTO> getConsultationCountByDoctor();
    List<DiagnosisFrequencyDTO> getDiagnosisFrequency();
}