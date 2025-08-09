package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO;
import com.hgs.patient.siags_backend.dto.ConsultationRequest;
import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
import com.hgs.patient.siags_backend.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des consultations.
 * Expose des endpoints sécurisés pour créer, lire, mettre à jour et supprimer des consultations.
 * Les autorisations sont gérées via des annotations @PreAuthorize.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @Autowired
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    // Créer une nouvelle consultation
    // Utilise le DTO ConsultationRequest pour la réception des données
    @PostMapping("/patient/{patientId}/doctor/{doctorId}")
    @PreAuthorize("hasAuthority('CONSULTATION_WRITE')")
    public ResponseEntity<ConsultationResponseDTO> createConsultation(
            @PathVariable Long patientId,
            @PathVariable Long doctorId,
            @Valid @RequestBody ConsultationRequest consultationRequest) {
        ConsultationResponseDTO createdConsultationDto = consultationService.createConsultation(patientId, doctorId, consultationRequest);
        return new ResponseEntity<>(createdConsultationDto, HttpStatus.CREATED);
    }

    // Récupérer une consultation par son ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<ConsultationResponseDTO> getConsultationById(@PathVariable Long id) {
        ConsultationResponseDTO consultationDto = consultationService.getConsultationById(id);
        return ResponseEntity.ok(consultationDto);
    }

    // Récupérer toutes les consultations
    @GetMapping
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {
        List<ConsultationResponseDTO> consultationsDto = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultationsDto);
    }

    // Récupérer les consultations d'un patient spécifique
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatient(@PathVariable Long patientId) {
        List<ConsultationResponseDTO> consultationsDto = consultationService.getConsultationsByPatient(patientId);
        return ResponseEntity.ok(consultationsDto);
    }

    // Récupérer les consultations d'un médecin spécifique
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDoctor(@PathVariable Long doctorId) {
        List<ConsultationResponseDTO> consultationsDto = consultationService.getConsultationsByDoctor(doctorId);
        return ResponseEntity.ok(consultationsDto);
    }

    // Mettre à jour une consultation existante
    // Utilise le DTO ConsultationRequest pour la mise à jour
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONSULTATION_WRITE')")
    public ResponseEntity<ConsultationResponseDTO> updateConsultation(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationRequest consultationRequest) {
        ConsultationResponseDTO updatedConsultationDto = consultationService.updateConsultation(id, consultationRequest);
        return ResponseEntity.ok(updatedConsultationDto);
    }

    // Supprimer une consultation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONSULTATION_DELETE')")
    public ResponseEntity<HttpStatus> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Filtrer les consultations par plage de dates
    @GetMapping("/filter-by-date")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(consultations);
    }

    // Filtrer les consultations par nom de famille du patient
    @GetMapping("/filter-by-patient-lastname")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatientLastName(
            @RequestParam String lastName) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByPatientLastName(lastName);
        return ResponseEntity.ok(consultations);
    }

    // Filtrer les consultations par mot-clé dans le diagnostic
    @GetMapping("/filter-by-diagnosis")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDiagnosis(
            @RequestParam String keyword) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByDiagnosisKeyword(keyword);
        return ResponseEntity.ok(consultations);
    }

    // Compter les consultations par médecin
    @GetMapping("/count-by-doctor")
    @PreAuthorize("hasAuthority('CONSULTATION_READ')")
    public ResponseEntity<List<ConsultationCountByDoctorDTO>> countConsultationsByDoctor() {
        List<ConsultationCountByDoctorDTO> counts = consultationService.countConsultationsByDoctor();
        return ResponseEntity.ok(counts);
    }
}
