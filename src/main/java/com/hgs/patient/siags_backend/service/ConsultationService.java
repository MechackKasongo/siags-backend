package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.ConsultationRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // Indique à Spring que c'est un service
public class ConsultationService {

    @Autowired // Injectez le dépôt de consultations
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository; // Injectez UserRepository pour récupérer le médecin

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

        // Remplir les informations du patient
        if (consultation.getPatient() != null) {
            dto.setPatientId(consultation.getPatient().getId());
            dto.setPatientNomComplet(consultation.getPatient().getNom() + " " + consultation.getPatient().getPrenom());
        }

        // Remplir les informations du médecin
        if (consultation.getDoctor() != null) {
            dto.setDoctorId(consultation.getDoctor().getId());
            dto.setDoctorNomComplet(consultation.getDoctor().getUsername()); // Ou nom/prénom si disponibles sur User
            // Si votre entité User a des champs nom/prénom:
            // dto.setDoctorNomComplet(consultation.getDoctor().getFirstName() + " " + consultation.getDoctor().getLastName());
        }

        return dto;
    }

    // Créer une nouvelle consultation
    public ConsultationResponseDTO createConsultation(Long patientId, Long doctorId, Consultation consultation) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));

        consultation.setPatient(patient);
        consultation.setDoctor(doctor);
        consultation.setConsultationDate(LocalDateTime.now()); // Date de consultation à l'heure actuelle

        Consultation savedConsultation = consultationRepository.save(consultation);
        return mapToDTO(savedConsultation); // Mappez l'entité sauvegardée vers le DTO
    }

    // Récupérer une consultation par son ID
    public ConsultationResponseDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));
        return mapToDTO(consultation);
    }

    // Récupérer toutes les consultations
    public List<ConsultationResponseDTO> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations d'un patient spécifique
    public List<ConsultationResponseDTO> getConsultationsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));
        return consultationRepository.findByPatient(patient).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les consultations d'un médecin spécifique
    public List<ConsultationResponseDTO> getConsultationsByDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));
        return consultationRepository.findByDoctor(doctor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour une consultation
    public ConsultationResponseDTO updateConsultation(Long id, Consultation consultationDetails) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));

        // Mettre à jour les champs pertinents
        consultation.setReasonForConsultation(consultationDetails.getReasonForConsultation());
        consultation.setObservations(consultationDetails.getObservations());
        consultation.setDiagnosis(consultationDetails.getDiagnosis());
        consultation.setTreatmentPlan(consultationDetails.getTreatmentPlan());
        consultation.setNotes(consultationDetails.getNotes());

        // Note: Patient et Doctor ne sont pas censés être modifiés via un update
        //       classique d'une consultation existante.
        //       Si cela est nécessaire, il faudrait une logique spécifique.

        Consultation updatedConsultation = consultationRepository.save(consultation);
        return mapToDTO(updatedConsultation);
    }

    // Supprimer une consultation
    public void deleteConsultation(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));
        consultationRepository.delete(consultation);
    }
}