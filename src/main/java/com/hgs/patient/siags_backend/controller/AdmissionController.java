package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // Créer une nouvelle admission pour un patient spécifique
    // Le corps de la requête utilise toujours l'entité Admission pour la réception des données
    // Mais la réponse utilise le DTO
    @PostMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<AdmissionResponseDTO> createAdmission(@PathVariable Long patientId, @Valid @RequestBody Admission admission) {
        AdmissionResponseDTO createdAdmissionDto = admissionService.createAdmission(patientId, admission); // Le service retourne déjà le DTO
        return new ResponseEntity<>(createdAdmissionDto, HttpStatus.CREATED);
    }

    // Récupérer une admission par son ID (retourne le DTO)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<AdmissionResponseDTO> getAdmissionById(@PathVariable Long id) {
        AdmissionResponseDTO admissionDto = admissionService.getAdmissionById(id); // Le service retourne le DTO
        return ResponseEntity.ok(admissionDto);
    }

    // Récupérer toutes les admissions (retourne une liste de DTOs)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<List<AdmissionResponseDTO>> getAllAdmissions() {
        List<AdmissionResponseDTO> admissionsDto = admissionService.getAllAdmissions(); // Le service retourne une liste de DTOs
        return ResponseEntity.ok(admissionsDto);
    }

    // Récupérer les admissions d'un patient spécifique (retourne une liste de DTOs)
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<List<AdmissionResponseDTO>> getAdmissionsByPatient(@PathVariable Long patientId) {
        List<AdmissionResponseDTO> admissionsDto = admissionService.getAdmissionsByPatient(patientId); // Le service retourne une liste de DTOs
        return ResponseEntity.ok(admissionsDto);
    }

    // Mettre à jour une admission existante (retourne le DTO de l'admission mise à jour)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<AdmissionResponseDTO> updateAdmission(@PathVariable Long id, @Valid @RequestBody Admission admissionDetails) {
        AdmissionResponseDTO updatedAdmissionDto = admissionService.updateAdmission(id, admissionDetails); // Le service retourne le DTO
        return ResponseEntity.ok(updatedAdmissionDto);
    }

    // Marquer une admission comme "sortie" (retourne le DTO)
    @PutMapping("/{id}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<AdmissionResponseDTO> dischargeAdmission(@PathVariable Long id, @RequestBody String dischargeSummary) {
        AdmissionResponseDTO dischargedAdmissionDto = admissionService.dischargeAdmission(id, dischargeSummary); // Le service retourne le DTO
        return ResponseEntity.ok(dischargedAdmissionDto);
    }

    // Supprimer une admission (pas de changement ici, car pas de corps de réponse)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteAdmission(@PathVariable Long id) {
        admissionService.deleteAdmission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}