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
//
//package com.hgs.patient.siags_backend.controller;
//
//import com.hgs.patient.siags_backend.dto.AdmissionRequest;
//import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
//import com.hgs.patient.siags_backend.model.Admission;
//import com.hgs.patient.siags_backend.service.AdmissionService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//        import java.time.LocalDateTime;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admissions") // Chemin de base pour les requêtes d'admission
//public class AdmissionController {
//
//    private final AdmissionService admissionService;
//
//    @Autowired
//    public AdmissionController(AdmissionService admissionService) {
//        this.admissionService = admissionService;
//    }
//
//    // Méthode utilitaire pour convertir AdmissionRequest en Admission
//    // Utilisée pour la création et la mise à jour
//    private Admission convertToEntity(AdmissionRequest dto) {
//        Admission admission = new Admission();
//        // L'ID du patient et l'admissionPersonnel sont gérés séparément
//        admission.setAdmissionDate(dto.getAdmissionDate() != null ? dto.getAdmissionDate() : LocalDateTime.now());
//        admission.setReasonForAdmission(dto.getReasonForAdmission());
//        admission.setAssignedDepartment(dto.getAssignedDepartment());
//        admission.setRoomNumber(dto.getRoomNumber());
//        admission.setBedNumber(dto.getBedNumber());
//        admission.setStatus(dto.getStatus());
//        admission.setDischargeDate(dto.getDischargeDate());
//        admission.setDischargeSummary(dto.getDischargeSummary());
//        return admission;
//    }
//
//    /**
//     * Crée une nouvelle admission pour un patient spécifique.
//     * URL: POST /api/admissions/patient/{patientId}
//     */
//    @PostMapping("/patient/{patientId}")
//    public ResponseEntity<AdmissionResponseDTO> createAdmission(
//            @PathVariable Long patientId,
//            @Valid @RequestBody AdmissionRequest admissionRequest) {
//        Admission admissionToCreate = convertToEntity(admissionRequest);
//        AdmissionResponseDTO createdAdmission = admissionService.createAdmission(patientId, admissionToCreate);
//        return new ResponseEntity<>(createdAdmission, HttpStatus.CREATED);
//    }
//
//    /**
//     * Récupère une admission par son ID.
//     * URL: GET /api/admissions/{id}
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<AdmissionResponseDTO> getAdmissionById(@PathVariable Long id) {
//        AdmissionResponseDTO admission = admissionService.getAdmissionById(id);
//        return new ResponseEntity<>(admission, HttpStatus.OK);
//    }
//
//    /**
//     * Récupère toutes les admissions.
//     * URL: GET /api/admissions
//     */
//    @GetMapping
//    public ResponseEntity<List<AdmissionResponseDTO>> getAllAdmissions() {
//        List<AdmissionResponseDTO> admissions = admissionService.getAllAdmissions();
//        return new ResponseEntity<>(admissions, HttpStatus.OK);
//    }
//
//    /**
//     * Récupère toutes les admissions pour un patient donné.
//     * URL: GET /api/admissions/patient/{patientId}
//     */
//    @GetMapping("/patient/{patientId}")
//    public ResponseEntity<List<AdmissionResponseDTO>> getAdmissionsByPatient(@PathVariable Long patientId) {
//        List<AdmissionResponseDTO> admissions = admissionService.getAdmissionsByPatient(patientId);
//        return new ResponseEntity<>(admissions, HttpStatus.OK);
//    }
//
//    /**
//     * Met à jour une admission existante.
//     * URL: PUT /api/admissions/{id}
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<AdmissionResponseDTO> updateAdmission(
//            @PathVariable Long id,
//            @Valid @RequestBody AdmissionRequest admissionRequest) {
//        Admission admissionDetails = convertToEntity(admissionRequest); // Convertit le DTO en entité pour le service
//        AdmissionResponseDTO updatedAdmission = admissionService.updateAdmission(id, admissionDetails);
//        return new ResponseEntity<>(updatedAdmission, HttpStatus.OK);
//    }
//
//    /**
//     * Marque une admission comme "sortie" (DISCHARGED).
//     * URL: PUT /api/admissions/{id}/discharge
//     * Vous pouvez ajuster le corps de la requête si vous voulez plus d'informations pour le décharge.
//     */
//    @PutMapping("/{id}/discharge")
//    public ResponseEntity<AdmissionResponseDTO> dischargeAdmission(
//            @PathVariable Long id,
//            @RequestParam(required = false) String dischargeSummary) {
//        AdmissionResponseDTO dischargedAdmission = admissionService.dischargeAdmission(id, dischargeSummary);
//        return new ResponseEntity<>(dischargedAdmission, HttpStatus.OK);
//    }
//
//
//    /**
//     * Supprime une admission par son ID.
//     * URL: DELETE /api/admissions/{id}
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAdmission(@PathVariable Long id) {
//        admissionService.deleteAdmission(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}
