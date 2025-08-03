package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.AdmissionRequestDTO;
import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
import com.hgs.patient.siags_backend.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    @Autowired
    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    // --- Créer une nouvelle admission ---
    // Seuls les utilisateurs avec la permission ADMISSION_WRITE peuvent créer une admission
    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_WRITE')")
    public ResponseEntity<AdmissionResponseDTO> createAdmission(@Valid @RequestBody AdmissionRequestDTO admissionRequestDTO) {
        AdmissionResponseDTO newAdmission = admissionService.createAdmission(admissionRequestDTO);
        return new ResponseEntity<>(newAdmission, HttpStatus.CREATED);
    }

    // --- Récupérer une admission par ID ---
    // Tous les rôles pertinents qui ont ADMISSION_READ peuvent consulter les admissions
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_READ')")
    public ResponseEntity<AdmissionResponseDTO> getAdmissionById(@PathVariable Long id) {
        AdmissionResponseDTO admission = admissionService.getAdmissionById(id);
        return ResponseEntity.ok(admission);
    }

    // --- Récupérer toutes les admissions (avec ou sans pagination) ---
    // Tous les rôles pertinents qui ont ADMISSION_READ peuvent consulter les admissions
    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_READ')")
    public ResponseEntity<?> getAllAdmissions(
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
            Page<AdmissionResponseDTO> admissionsPage = admissionService.getAllAdmissionsPaginated(pageable);
            return ResponseEntity.ok(admissionsPage);
        } else {
            List<AdmissionResponseDTO> admissions = admissionService.getAllAdmissions();
            return ResponseEntity.ok(admissions);
        }
    }

    // --- Récupérer les admissions par ID Patient ---
    // Tous les rôles pertinents qui ont ADMISSION_READ peuvent consulter les admissions d'un patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('ADMISSION_READ')")
    public ResponseEntity<List<AdmissionResponseDTO>> getAdmissionsByPatientId(@PathVariable Long patientId) {
        List<AdmissionResponseDTO> admissions = admissionService.getAdmissionsByPatientId(patientId);
        return ResponseEntity.ok(admissions);
    }

    // --- Mettre à jour une admission existante ---
    // Les utilisateurs ayant ADMISSION_WRITE peuvent modifier
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_WRITE')")
    public ResponseEntity<AdmissionResponseDTO> updateAdmission(@PathVariable Long id, @Valid @RequestBody AdmissionRequestDTO admissionRequestDTO) {
        AdmissionResponseDTO updatedAdmission = admissionService.updateAdmission(id, admissionRequestDTO);
        return ResponseEntity.ok(updatedAdmission);
    }

    // --- Supprimer une admission ---
    // Seuls les utilisateurs ayant ADMISSION_DELETE (ou ADMISSION_WRITE si c'est combiné) peuvent supprimer
    // J'assume ici une permission spécifique ADMISSION_DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<HttpStatus> deleteAdmission(@PathVariable Long id) {
        admissionService.deleteAdmission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}