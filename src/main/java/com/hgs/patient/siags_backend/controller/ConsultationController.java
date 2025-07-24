package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}

//package com.hgs.patient.siags_backend.controller;
//
//import com.hgs.patient.siags_backend.dto.ConsultationRequest;
//import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
//import com.hgs.patient.siags_backend.model.Consultation;
//import com.hgs.patient.siags_backend.service.ConsultationService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//        import java.time.LocalDateTime;
//import java.util.List;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/consultations") // Chemin de base pour les requêtes de consultation
//public class ConsultationController {
//
//    private final ConsultationService consultationService;
//
//    @Autowired
//    public ConsultationController(ConsultationService consultationService) {
//        this.consultationService = consultationService;
//    }
//
//    // Méthode utilitaire pour convertir ConsultationRequest en Consultation (entité)
//    private Consultation convertToEntity(ConsultationRequest dto) {
//        Consultation consultation = new Consultation();
//        // L'ID, patient et doctor sont gérés séparément lors de la création
//        consultation.setConsultationDate(dto.getConsultationDate() != null ? dto.getConsultationDate() : LocalDateTime.now());
//        consultation.setReasonForConsultation(dto.getReasonForConsultation());
//        consultation.setObservations(dto.getObservations());
//        consultation.setDiagnosis(dto.getDiagnosis());
//        consultation.setTreatmentPlan(dto.getTreatmentPlan());
//        consultation.setNotes(dto.getNotes());
//        return consultation;
//    }
//
//    /**
//     * Crée une nouvelle consultation pour un patient et un médecin spécifiques.
//     * URL: POST /api/consultations/patient/{patientId}/doctor/{doctorId}
//     */
//    @PostMapping("/patient/{patientId}/doctor/{doctorId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')") // Seuls l'Admin et le Médecin peuvent créer une consultation
//    public ResponseEntity<ConsultationResponseDTO> createConsultation(
//            @PathVariable Long patientId,
//            @PathVariable Long doctorId,
//            @Valid @RequestBody ConsultationRequest consultationRequest) {
//        Consultation consultationToCreate = convertToEntity(consultationRequest);
//        ConsultationResponseDTO createdConsultation = consultationService.createConsultation(patientId, doctorId, consultationToCreate);
//        return new ResponseEntity<>(createdConsultation, HttpStatus.CREATED);
//    }
//
//    /**
//     * Récupère une consultation par son ID.
//     * URL: GET /api/consultations/{id}
//     */
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
//    public ResponseEntity<ConsultationResponseDTO> getConsultationById(@PathVariable Long id) {
//        ConsultationResponseDTO consultation = consultationService.getConsultationById(id);
//        return ResponseEntity.ok(consultation);
//    }
//
//    /**
//     * Récupère toutes les consultations.
//     * URL: GET /api/consultations
//     */
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
//    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {
//        List<ConsultationResponseDTO> consultations = consultationService.getAllConsultations();
//        return ResponseEntity.ok(consultations);
//    }
//
//    /**
//     * Récupère les consultations d'un patient spécifique.
//     * URL: GET /api/consultations/patient/{patientId}
//     */
//    @GetMapping("/patient/{patientId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
//    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatient(@PathVariable Long patientId) {
//        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByPatient(patientId);
//        return ResponseEntity.ok(consultations);
//    }
//
//    /**
//     * Récupère les consultations d'un médecin spécifique.
//     * URL: GET /api/consultations/doctor/{doctorId}
//     */
//    @GetMapping("/doctor/{doctorId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'INFIRMIER', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
//    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDoctor(@PathVariable Long doctorId) {
//        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByDoctor(doctorId);
//        return ResponseEntity.ok(consultations);
//    }
//
//    /**
//     * Met à jour une consultation existante.
//     * URL: PUT /api/consultations/{id}
//     */
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')") // Seuls l'Admin et le Médecin peuvent mettre à jour une consultation
//    public ResponseEntity<ConsultationResponseDTO> updateConsultation(
//            @PathVariable Long id,
//            @Valid @RequestBody ConsultationRequest consultationRequest) {
//        Consultation consultationDetails = convertToEntity(consultationRequest);
//        ConsultationResponseDTO updatedConsultation = consultationService.updateConsultation(id, consultationDetails);
//        return ResponseEntity.ok(updatedConsultation);
//    }
//
//    /**
//     * Supprime une consultation par son ID.
//     * URL: DELETE /api/consultations/{id}
//     */
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')") // Seul l'Admin peut supprimer une consultation
//    public ResponseEntity<HttpStatus> deleteConsultation(@PathVariable Long id) {
//        consultationService.deleteConsultation(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}