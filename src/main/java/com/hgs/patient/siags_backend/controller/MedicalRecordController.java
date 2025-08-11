package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.MedicalRecordDTO;
import com.hgs.patient.siags_backend.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // Mise à jour des informations du dossier médical d'un patient existant
    // Notez l'utilisation de @PutMapping pour la mise à jour
    @PutMapping("/{patientId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_WRITE')")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {

        // Appel de la méthode correcte du service
        MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecordDetails(patientId, medicalRecordDTO);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    // Récupération du dossier médical par l'ID du patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_READ')")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordByPatientId(@PathVariable Long patientId) {
        MedicalRecordDTO recordDTO = medicalRecordService.getMedicalRecordByPatientId(patientId);
        return new ResponseEntity<>(recordDTO, HttpStatus.OK);
    }

    // Récupération du dossier médical par l'ID du dossier
    @GetMapping("/{recordId}")
    @PreAuthorize("hasAuthority('MEDICAL_RECORD_READ')")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordById(@PathVariable Long recordId) {
        MedicalRecordDTO recordDTO = medicalRecordService.getMedicalRecordById(recordId);
        return new ResponseEntity<>(recordDTO, HttpStatus.OK);
    }

}