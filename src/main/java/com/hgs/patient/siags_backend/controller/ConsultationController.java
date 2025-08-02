package com.hgs.patient.siags_backend.controller;


import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;

import com.hgs.patient.siags_backend.model.Consultation;

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


@CrossOrigin(origins = "*", maxAge = 3600) // Pour autoriser les requêtes depuis un frontend différent

@RestController

@RequestMapping("/api/consultations") // Préfixe pour tous les endpoints de ce contrôleur

public class ConsultationController {


    @Autowired

    private ConsultationService consultationService;


// Créer une nouvelle consultation

// Nécessite l'ID du patient et l'ID du médecin dans l'URL

    @PostMapping("/patient/{patientId}/doctor/{doctorId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'RECEPTIONNISTE')") // Qui peut créer une consultation

    public ResponseEntity<ConsultationResponseDTO> createConsultation(

            @PathVariable Long patientId,

            @PathVariable Long doctorId,

            @Valid @RequestBody Consultation consultation) {

        ConsultationResponseDTO createdConsultationDto = consultationService.createConsultation(patientId, doctorId, consultation);

        return new ResponseEntity<>(createdConsultationDto, HttpStatus.CREATED);

    }


// Récupérer une consultation par son ID

    @GetMapping("/{id}")

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE')")

// Qui peut consulter une consultation spécifique

    public ResponseEntity<ConsultationResponseDTO> getConsultationById(@PathVariable Long id) {

        ConsultationResponseDTO consultationDto = consultationService.getConsultationById(id);

        return ResponseEntity.ok(consultationDto);

    }


// Récupérer toutes les consultations

    @GetMapping

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE')")

// Qui peut consulter toutes les consultations

    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {

        List<ConsultationResponseDTO> consultationsDto = consultationService.getAllConsultations();

        return ResponseEntity.ok(consultationsDto);

    }


// Récupérer les consultations d'un patient spécifique

    @GetMapping("/patient/{patientId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE')")

    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatient(@PathVariable Long patientId) {

        List<ConsultationResponseDTO> consultationsDto = consultationService.getConsultationsByPatient(patientId);

        return ResponseEntity.ok(consultationsDto);

    }


// Récupérer les consultations d'un médecin spécifique

    @GetMapping("/doctor/{doctorId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'RECEPTIONNISTE')") // Les médecins peuvent voir leurs consultations

    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDoctor(@PathVariable Long doctorId) {

        List<ConsultationResponseDTO> consultationsDto = consultationService.getConsultationsByDoctor(doctorId);

        return ResponseEntity.ok(consultationsDto);

    }


// Mettre à jour une consultation existante

    @PutMapping("/{id}")

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')") // Seuls l'Admin et les Médecins peuvent modifier les consultations

    public ResponseEntity<ConsultationResponseDTO> updateConsultation(@PathVariable Long id, @Valid @RequestBody Consultation consultationDetails) {

        ConsultationResponseDTO updatedConsultationDto = consultationService.updateConsultation(id, consultationDetails);

        return ResponseEntity.ok(updatedConsultationDto);

    }


// Supprimer une consultation

    @DeleteMapping("/{id}")

    @PreAuthorize("hasRole('ADMIN')") // Seul l'Admin peut supprimer une consultation

    public ResponseEntity<HttpStatus> deleteConsultation(@PathVariable Long id) {

        consultationService.deleteConsultation(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content

    }


    @GetMapping("/filter-by-date")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDateRange(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByDateRange(startDate, endDate);

        return ResponseEntity.ok(consultations);

    }


// Récupérer les consultations d'un patient avec un spécifique par nom de famille

    @GetMapping("/filter-by-patient-lastname")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatientLastName(

            @RequestParam String lastName) {

        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByPatientLastName(lastName);

        return ResponseEntity.ok(consultations);

    }


// Récupérer les consultations par mot-clé dans le diagnostic

    @GetMapping("/filter-by-diagnosis")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDiagnosis(

            @RequestParam String keyword) {

        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByDiagnosisKeyword(keyword);

        return ResponseEntity.ok(consultations);

    }

}
