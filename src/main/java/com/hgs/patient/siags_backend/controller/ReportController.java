package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.*;
import com.hgs.patient.siags_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // --- Rapports sur les patients ---

    /**
     * Récupère le nombre total de patients.
     * Accessible par les ADMINS et RECEPTIONNISTES (ou quiconque a la permission REPORT_READ_PATIENT).
     * GET /api/reports/patients/count
     */
    @GetMapping("/patients/count")
    @PreAuthorize("hasAuthority('REPORT_READ_PATIENT')")
    public ResponseEntity<Long> getTotalPatientsCount() {
        Long count = reportService.getTotalPatientsCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Récupère la répartition des patients par genre.
     * Accessible par les ADMINS et RECEPTIONNISTES.
     * GET /api/reports/patients/gender-distribution
     */
    @GetMapping("/patients/gender-distribution")
    @PreAuthorize("hasAuthority('REPORT_READ_PATIENT')")
    public ResponseEntity<List<PatientGenderDistributionDTO>> getPatientGenderDistribution() {
        List<PatientGenderDistributionDTO> distribution = reportService.getPatientGenderDistribution();
        return ResponseEntity.ok(distribution);
    }

    // --- Rapports sur les admissions ---

    /**
     * Récupère le nombre total d'admissions.
     * Accessible par les ADMINS et RECEPTIONNISTES.
     * GET /api/reports/admissions/count
     */
    @GetMapping("/admissions/count")
    @PreAuthorize("hasAuthority('REPORT_READ_ADMISSION')")
    public ResponseEntity<Long> getTotalAdmissionsCount() {
        Long count = reportService.getTotalAdmissionsCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Récupère le nombre d'admissions entre deux dates.
     * Accessible par les ADMINS et RECEPTIONNISTES.
     * GET /api/reports/admissions/count-by-date-range?startDate=YYYY-MM-DDTHH:MM:SS&endDate=YYYY-MM-DDTHH:MM:SS
     */
    @GetMapping("/admissions/count-by-date-range")
    @PreAuthorize("hasAuthority('REPORT_READ_ADMISSION')")
    public ResponseEntity<Long> getAdmissionsCountBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = reportService.getAdmissionsCountBetweenDates(startDate, endDate);
        return ResponseEntity.ok(count);
    }

    /**
     * Récupère le nombre d'admissions par département.
     * Accessible par les ADMINS et RECEPTIONNISTES.
     * GET /api/reports/admissions/count-by-department
     */
    @GetMapping("/admissions/count-by-department")
    @PreAuthorize("hasAuthority('REPORT_READ_ADMISSION')")
    public ResponseEntity<List<AdmissionCountByDepartmentDTO>> getAdmissionCountByDepartment() {
        List<AdmissionCountByDepartmentDTO> counts = reportService.getAdmissionCountByDepartment();
        return ResponseEntity.ok(counts);
    }

    /**
     * Récupère le nombre d'admissions par mois pour une année donnée.
     * Accessible par les ADMINS et RECEPTIONNISTES.
     * GET /api/reports/admissions/monthly-count?year=YYYY
     */
    @GetMapping("/admissions/monthly-count")
    @PreAuthorize("hasAuthority('REPORT_READ_ADMISSION')")
    public ResponseEntity<List<MonthlyAdmissionCountDTO>> getMonthlyAdmissionCountForYear(
            @RequestParam int year) {
        List<MonthlyAdmissionCountDTO> counts = reportService.getMonthlyAdmissionCountForYear(year);
        return ResponseEntity.ok(counts);
    }

    // --- Rapports sur les consultations ---

    /**
     * Récupère le nombre total de consultations.
     * Accessible par les ADMINS, MEDECINS et RECEPTIONNISTES.
     * GET /api/reports/consultations/count
     */
    @GetMapping("/consultations/count")
    @PreAuthorize("hasAuthority('REPORT_READ_CONSULTATION')")
    public ResponseEntity<Long> getTotalConsultationsCount() {
        Long count = reportService.getTotalConsultationsCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Récupère le nombre de consultations entre deux dates.
     * Accessible par les ADMINS, MEDECINS et RECEPTIONNISTES.
     * GET /api/reports/consultations/count-by-date-range?startDate=YYYY-MM-DDTHH:MM:SS&endDate=YYYY-MM-DDTHH:MM:SS
     */
    @GetMapping("/consultations/count-by-date-range")
    @PreAuthorize("hasAuthority('REPORT_READ_CONSULTATION')")
    public ResponseEntity<Long> getConsultationsCountBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = reportService.getConsultationsCountBetweenDates(startDate, endDate);
        return ResponseEntity.ok(count);
    }

    /**
     * Récupère le nombre de consultations par médecin.
     * Accessible par les ADMINS et MEDECINS.
     * GET /api/reports/consultations/count-by-doctor
     */
    @GetMapping("/consultations/count-by-doctor")
    @PreAuthorize("hasAuthority('REPORT_READ_CONSULTATION')")
    public ResponseEntity<List<ConsultationCountByDoctorDTO>> getConsultationCountByDoctor() {
        List<ConsultationCountByDoctorDTO> counts = reportService.getConsultationCountByDoctor();
        return ResponseEntity.ok(counts);
    }

    /**
     * Récupère la fréquence des diagnostics.
     * Accessible par les ADMINS et MEDECINS.
     * GET /api/reports/consultations/diagnosis-frequency
     */
    @GetMapping("/consultations/diagnosis-frequency")
    @PreAuthorize("hasAuthority('REPORT_READ_CONSULTATION')")
    public ResponseEntity<List<DiagnosisFrequencyDTO>> getDiagnosisFrequency() {
        List<DiagnosisFrequencyDTO> frequency = reportService.getDiagnosisFrequency();
        return ResponseEntity.ok(frequency);
    }
}