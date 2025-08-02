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


//@CrossOrigin(origins = "*", maxAge = 3600) // Permet les requ\C3\AAtes Cross-Origin

@RestController // Indique que c'est un contr\C3\B4leur REST

@RequestMapping("/api/daily-records") // Toutes les routes de ce contr\C3\B4leur commenceront par /api/daily-records

public class PatientDailyRecordController {


    private final PatientDailyRecordService dailyRecordService;


    @Autowired

    public PatientDailyRecordController(PatientDailyRecordService dailyRecordService) {

        this.dailyRecordService = dailyRecordService;

    }


// --- Cr\C3\A9er un nouvel enregistrement quotidien ---

// Seuls les ADMINS, MEDECINS et INFIRMIERS peuvent cr\C3\A9er un enregistrement

    @PostMapping

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER')")

    public ResponseEntity<PatientDailyRecordResponseDTO> createPatientDailyRecord(@Valid @RequestBody PatientDailyRecordRequestDTO requestDTO) {

        PatientDailyRecordResponseDTO newRecord = dailyRecordService.createPatientDailyRecord(requestDTO);

        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);

    }


// --- R\C3\A9cup\C3\A9rer un enregistrement par ID ---

// Tous les r\C3\B4les pertinents peuvent consulter les enregistrements

    @GetMapping("/{id}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<PatientDailyRecordResponseDTO> getPatientDailyRecordById(@PathVariable Long id) {

        PatientDailyRecordResponseDTO record = dailyRecordService.getPatientDailyRecordById(id);

        return ResponseEntity.ok(record);

    }


// --- R\C3\A9cup\C3\A9rer tous les enregistrements (avec ou sans pagination) ---

// Tous les r\C3\B4les pertinents peuvent consulter les enregistrements

    @GetMapping

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

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


// --- R\C3\A9cup\C3\A9rer les enregistrements par ID Patient ---

    @GetMapping("/patient/{patientId}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByPatientId(@PathVariable Long patientId) {

        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByPatientId(patientId);

        return ResponseEntity.ok(records);

    }


// --- R\C3\A9cup\C3\A9rer les enregistrements par Date d'enregistrement ---

    @GetMapping("/date/{recordDate}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByRecordDate(

            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {

        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByRecordDate(recordDate);

        return ResponseEntity.ok(records);

    }


// --- R\C3\A9cup\C3\A9rer les enregistrements par ID Patient et Date ---

    @GetMapping("/patient/{patientId}/date/{recordDate}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER') or hasRole('RECEPTIONNISTE')")

    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByPatientIdAndRecordDate(

            @PathVariable Long patientId,

            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {

        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByPatientIdAndRecordDate(patientId, recordDate);

        return ResponseEntity.ok(records);

    }


// --- R\C3\A9cup\C3\A9rer les enregistrements par l'utilisateur qui les a cr\C3\A9\C3\A9s ---

    @GetMapping("/recorded-by/{userId}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER')")

// Les r\C3\A9ceptionnistes ne consultent peut-\C3\AAtre pas par utilisateur

    public ResponseEntity<List<PatientDailyRecordResponseDTO>> getPatientDailyRecordsByRecordedById(@PathVariable Long userId) {

        List<PatientDailyRecordResponseDTO> records = dailyRecordService.getPatientDailyRecordsByRecordedById(userId);

        return ResponseEntity.ok(records);

    }


// --- Mettre \C3\A0 jour un enregistrement existant ---

// Seuls les ADMINS, MEDECINS et INFIRMIERS peuvent modifier un enregistrement

    @PutMapping("/{id}")

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN') or hasRole('INFIRMIER')")

    public ResponseEntity<PatientDailyRecordResponseDTO> updatePatientDailyRecord(@PathVariable Long id, @Valid @RequestBody PatientDailyRecordRequestDTO requestDTO) {

        PatientDailyRecordResponseDTO updatedRecord = dailyRecordService.updatePatientDailyRecord(id, requestDTO);

        return ResponseEntity.ok(updatedRecord);

    }


// --- Supprimer un enregistrement ---

// Seuls les ADMINS peuvent supprimer un enregistrement (op\C3\A9ration sensible)

    @DeleteMapping("/{id}")

    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<HttpStatus> deletePatientDailyRecord(@PathVariable Long id) {

        dailyRecordService.deletePatientDailyRecord(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // R\C3\A9ponse 204 No Content

    }

}