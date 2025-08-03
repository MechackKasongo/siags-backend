package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.MedicalRecordDTO;
import com.hgs.patient.siags_backend.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des dossiers médicaux.
 * Il expose les points d'accès de l'API pour les opérations CRUD sur les dossiers médicaux.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // Créer un dossier médical pour un patient
    @PostMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_WRITE')")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        MedicalRecordDTO createdRecord = medicalRecordService.createMedicalRecord(patientId, medicalRecordDTO);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    // Récupérer le dossier médical d'un patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_READ')")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordByPatientId(@PathVariable Long patientId) {
        MedicalRecordDTO record = medicalRecordService.getMedicalRecordByPatientId(patientId);
        return ResponseEntity.ok(record);
    }

    // Mettre à jour le dossier médical d'un patient
    @PutMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_WRITE')")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecord(patientId, medicalRecordDTO);
        return ResponseEntity.ok(updatedRecord);
    }
}
