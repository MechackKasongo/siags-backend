package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.*;
import com.hgs.patient.siags_backend.repository.AdmissionRepository;
import com.hgs.patient.siags_backend.repository.ConsultationRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImp implements ReportService {

    private final PatientRepository patientRepository;
    private final AdmissionRepository admissionRepository;
    private final ConsultationRepository consultationRepository;

    @Autowired
    public ReportServiceImp(PatientRepository patientRepository,
                            AdmissionRepository admissionRepository,
                            ConsultationRepository consultationRepository) {
        this.patientRepository = patientRepository;
        this.admissionRepository = admissionRepository;
        this.consultationRepository = consultationRepository;
    }
    // --- Rapports sur les patients ---

    @Override
    public Long getTotalPatientsCount() {
        return patientRepository.count();
    }

    @Override
    public List<PatientGenderDistributionDTO> getPatientGenderDistribution() {
        return patientRepository.countPatientsByGender();
    }

    // --- Rapports sur les admissions ---

    @Override
    public Long getTotalAdmissionsCount() {
        return admissionRepository.count();
    }

    @Override
    public Long getAdmissionsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return admissionRepository.countAdmissionsBetweenDates(startDate, endDate);
    }

    @Override
    public List<AdmissionCountByDepartmentDTO> getAdmissionCountByDepartment() {
        return admissionRepository.countAdmissionsByDepartment();
    }

    @Override
    public List<MonthlyAdmissionCountDTO> getMonthlyAdmissionCountForYear(int year) {
        return admissionRepository.countAdmissionsByMonth(year);
    }

    // --- Rapports sur les consultations ---
    @Override
    public Long getTotalConsultationsCount() {
        return consultationRepository.count();
    }

    @Override
    public Long getConsultationsCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return consultationRepository.countConsultationsBetweenDates(startDate, endDate);
    }

    @Override
    public List<ConsultationCountByDoctorDTO> getConsultationCountByDoctor() {
        return consultationRepository.countConsultationsByDoctor();
    }

    @Override
    public List<DiagnosisFrequencyDTO> getDiagnosisFrequency() {
        return consultationRepository.countDiagnosisFrequency();
    }
}