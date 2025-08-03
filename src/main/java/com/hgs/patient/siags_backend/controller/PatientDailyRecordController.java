package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.PatientDailyRecordRequestDTO;
import com.hgs.patient.siags_backend.dto.PatientDailyRecordResponseDTO;
import com.hgs.patient.siags_backend.service.PatientDailyRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/daily-records")
public class PatientDailyRecordController {

    private final PatientDailyRecordService dailyRecordService;

    @Autowired
    public PatientDailyRecordController(PatientDailyRecordService dailyRecordService) {
        this.dailyRecordService = dailyRecordService;
    }

    // --- Créer un nouvel enregistrement quotidien ---
    @PostMapping
    @PreAuthorize("hasAuthority('DAILY_RECORD_WRITE')") // Qui peut créer un enregistrement (ADMIN, MEDECIN, INFIRMIER)
    public ResponseEntity<PatientDailyRecordResponseDTO> createPatientDailyRecord(@Valid @RequestBody PatientDailyRecordRequestDTO requestDTO) {
        PatientDailyRecordResponseDTO newRecord = dailyRecordService.createPatientDailyRecord(requestDTO);
        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
    }

    // --- Récupérer un enregistrement par ID ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    // Tous les rôles pertinents peuvent consulter les enregistrements
    public ResponseEntity<PatientDailyRecordResponseDTO> getPatientDailyRecordById(@PathVariable Long id) {
        PatientDailyRecordResponseDTO record = dailyRecordService.getPatientDailyRecordById(id);
        return ResponseEntity.ok(record);
    }

    // --- Récupérer tous les enregistrements (avec ou sans pagination) ---
    @GetMapping
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    // Tous les rôles pertinents peuvent consulter les enregistrements
    public ResponseEntity<?> getAllPatientDailyRecords(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {

        if (page != null && size != null) {
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.isEmpty()) {
                sort = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PatientDailyRecordResponseDTO> recordsPage = dailyRecordService.getAllPatientDailyRecordsPaginated(pageable);
            return ResponseEntity.ok(recordsPage);
        } else {
            List<PatientDailyRecordResponseDTO> records = dailyRecordService.getAllPatientDailyRecords();
            return ResponseEntity.ok(records);
        }
    }

    // --- Récupérer les enregistrements par ID Patient ---
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByPatientId(@PathVariable Long patientId) {
        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByPatientId(patientId);
        return ResponseEntity.ok(records);
    }

    // --- Récupérer les enregistrements par Date d'enregistrement ---
    @GetMapping("/date/{recordDate}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByRecordDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByRecordDate(recordDate);
        return ResponseEntity.ok(records);
    }

    // --- Récupérer les enregistrements par ID Patient et Date ---
    @GetMapping("/patient/{patientId}/date/{recordDate}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByPatientIdAndRecordDate(
            @PathVariable Long patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByPatientIdAndRecordDate(patientId, recordDate);
        return ResponseEntity.ok(records);
    }

    // --- Récupérer les enregistrements par l'utilisateur qui les a créés ---
    @GetMapping("/recorded-by/{userId}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_READ')")
    // Les réceptionnistes ne consultent peut-être pas par utilisateur
    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByRecordedById(@PathVariable Long userId) {
        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByRecordedById(userId);
        return ResponseEntity.ok(records);
    }

    // --- Mettre à jour un enregistrement existant ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_WRITE')")
    // Seuls les ADMINS, MEDECINS et INFIRMIERS peuvent modifier un enregistrement
    public ResponseEntity<PatientDailyRecordResponseDTO> updatePatientDailyRecord(@PathVariable Long id, @Valid @RequestBody PatientDailyRecordRequestDTO requestDTO) {
        PatientDailyRecordResponseDTO updatedRecord = dailyRecordService.updatePatientDailyRecord(id, requestDTO);
        return ResponseEntity.ok(updatedRecord);
    }

    // --- Supprimer un enregistrement ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DAILY_RECORD_DELETE')")
    // Seuls les ADMINS peuvent supprimer un enregistrement (opération sensible)
    public ResponseEntity<HttpStatus> deletePatientDailyRecord(@PathVariable Long id) {
        dailyRecordService.deleteDailyRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}