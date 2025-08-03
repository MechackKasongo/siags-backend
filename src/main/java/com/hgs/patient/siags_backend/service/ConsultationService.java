package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO;
import com.hgs.patient.siags_backend.dto.ConsultationRequest;
import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.*;
import com.hgs.patient.siags_backend.repository.ConsultationRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des opérations métier liées aux consultations.
 * Utilise le modèle Consultation, les DTOs et le repository pour interagir avec la base de données.
 */
@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuditService auditService; // Injecter le service d'audit

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository, PatientRepository patientRepository, UserRepository userRepository, AuditService auditService) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    // Méthode utilitaire pour mapper une entité Consultation à un ConsultationResponseDTO
    private ConsultationResponseDTO mapToDTO(Consultation consultation) {
        ConsultationResponseDTO dto = new ConsultationResponseDTO();
        dto.setId(consultation.getId());
        dto.setConsultationDate(consultation.getConsultationDate());
        dto.setReasonForConsultation(consultation.getReasonForConsultation());
        dto.setObservations(consultation.getObservations());
        dto.setDiagnosis(consultation.getDiagnosis());
        dto.setTreatmentPlan(consultation.getTreatmentPlan());
        dto.setNotes(consultation.getNotes());

        if (consultation.getPatient() != null) {
            dto.setPatientId(consultation.getPatient().getId());
            dto.setPatientNomComplet(consultation.getPatient().getLastName() + " " + consultation.getPatient().getFirstName());
        }

        if (consultation.getDoctor() != null) {
            dto.setDoctorId(consultation.getDoctor().getId());
            dto.setDoctorNomComplet(consultation.getDoctor().getNomComplet() + " " + consultation.getDoctor().getNomComplet());
        }

        if (consultation.getRecordedBy() != null) {
            dto.setRecordedById(consultation.getRecordedBy().getId());
            dto.setRecordedByUsername(consultation.getRecordedBy().getUsername());
        }

        if (consultation.getRecordedAt() != null) {
            dto.setRecordedAt(consultation.getRecordedAt());
        }

        return dto;
    }

    // Créer une nouvelle consultation à partir d'un DTO de requête
    @Transactional
    public ConsultationResponseDTO createConsultation(Long patientId, Long doctorId, ConsultationRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));

        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setDoctor(doctor);

        // Mappage des champs du DTO de requête vers l'entité
        consultation.setConsultationDate(request.getConsultationDate());
        consultation.setReasonForConsultation(request.getReasonForConsultation());
        consultation.setObservations(request.getObservations());
        consultation.setDiagnosis(request.getDiagnosis());
        consultation.setTreatmentPlan(request.getTreatmentPlan());
        consultation.setNotes(request.getNotes());

        // Récupération de l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            User recordingUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur enregistreur non trouvé : " + userDetails.getUsername()));
            consultation.setRecordedBy(recordingUser);
        }
        consultation.setRecordedAt(LocalDateTime.now());

        Consultation savedConsultation = consultationRepository.save(consultation);

        // Appel du service d'audit
        auditService.logAction(AuditAction.CREATE, AuditResource.CONSULTATION, savedConsultation.getId(), "Création d'une consultation pour le patient " + patientId);

        return mapToDTO(savedConsultation);
    }

    // Récupérer une consultation par son ID
    @Transactional(readOnly = true)
    public ConsultationResponseDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));

        // Appel du service d'audit pour les lectures de ressources
        auditService.logAction(AuditAction.READ, AuditResource.CONSULTATION, consultation.getId(), "Lecture de la consultation " + id);

        return mapToDTO(consultation);
    }

    // Récupérer toutes les consultations
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations d'un patient spécifique
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));
        return consultationRepository.findByPatient(patient).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations d'un médecin spécifique
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));
        return consultationRepository.findByDoctor(doctor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour une consultation existante à partir d'un DTO de requête
    @Transactional
    public ConsultationResponseDTO updateConsultation(Long id, ConsultationRequest request) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));

        consultation.setReasonForConsultation(request.getReasonForConsultation());
        consultation.setObservations(request.getObservations());
        consultation.setDiagnosis(request.getDiagnosis());
        consultation.setTreatmentPlan(request.getTreatmentPlan());
        consultation.setNotes(request.getNotes());

        Consultation updatedConsultation = consultationRepository.save(consultation);

        // Appel du service d'audit
        auditService.logAction(AuditAction.UPDATE, AuditResource.CONSULTATION, updatedConsultation.getId(), "Mise à jour de la consultation " + id);

        return mapToDTO(updatedConsultation);
    }

    // Supprimer une consultation
    @Transactional
    public void deleteConsultation(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));
        consultationRepository.delete(consultation);

        // Appel du service d'audit
        auditService.logAction(AuditAction.DELETE, AuditResource.CONSULTATION, id, "Suppression de la consultation " + id);
    }

    // Récupérer les consultations entre deux dates
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return consultationRepository.findByConsultationDateBetween(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations par nom de famille du patient
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByPatientLastName(String lastName) {
        return consultationRepository.findByPatientLastNameContainingIgnoreCase(lastName).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations par mot-clé dans le diagnostic
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByDiagnosisKeyword(String keyword) {
        return consultationRepository.findByDiagnosisContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer le nombre de consultations par médecin
    @Transactional(readOnly = true)
    public List<ConsultationCountByDoctorDTO> countConsultationsByDoctor() {
        return consultationRepository.countConsultationsByDoctor();
    }
}
