package com.hgs.patient.siags_backend.service;


import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;

import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;

import com.hgs.patient.siags_backend.model.Consultation;

import com.hgs.patient.siags_backend.model.Patient;

import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;

import com.hgs.patient.siags_backend.model.User;

import com.hgs.patient.siags_backend.repository.ConsultationRepository;

import com.hgs.patient.siags_backend.repository.PatientRepository;

import com.hgs.patient.siags_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


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


// Remplir les informations du patient en utilisant les noms de champs anglais

        if (consultation.getPatient() != null) {

            dto.setPatientId(consultation.getPatient().getId());

            dto.setPatientNomComplet(consultation.getPatient().getLastName() + " " + consultation.getPatient().getFirstName());

        }


// Remplir les informations du médecin

        if (consultation.getDoctor() != null) {

            dto.setDoctorId(consultation.getDoctor().getId());

            dto.setDoctorNomComplet(consultation.getDoctor().getUsername());

        }


// Ajoutez les informations de l'enregistreur si elles sont disponibles

        if (consultation.getRecordedBy() != null) {

            dto.setRecordedById(consultation.getRecordedBy().getId());

            dto.setRecordedByUsername(consultation.getRecordedBy().getUsername());

        }

        if (consultation.getRecordedAt() != null) {

            dto.setRecordedAt(consultation.getRecordedAt());

        }


        return dto;

    }


// Créer une nouvelle consultation

    @Transactional // Assurez-vous que cette méthode est transactionnelle

    public ConsultationResponseDTO createConsultation(Long patientId, Long doctorId, Consultation consultation) {

        Patient patient = patientRepository.findById(patientId)

                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));


        User doctor = userRepository.findById(doctorId)

                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));


        consultation.setPatient(patient);

        consultation.setDoctor(doctor);

        consultation.setConsultationDate(LocalDateTime.now()); // Date de consultation à l'heure actuelle

        consultation.setRecordedAt(LocalDateTime.now()); // Date d'enregistrement de la consultation


// --- DÉPLACÉ ICI : Récupération de l'utilisateur authentifié pour recordedBy ---

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {

            User recordingUser = userRepository.findByUsername(userDetails.getUsername())

                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur enregistreur non trouvé : " + userDetails.getUsername()));

            consultation.setRecordedBy(recordingUser);

        }

// ---------------------------------------------------------------------------------


        Consultation savedConsultation = consultationRepository.save(consultation);

        return mapToDTO(savedConsultation); // Mappez l'entité sauvegardée vers le DTO

    }


// Récupérer une consultation par son ID

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

    public ConsultationResponseDTO getConsultationById(Long id) {

        Consultation consultation = consultationRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));

        return mapToDTO(consultation);

    }


// Récupérer toutes les consultations

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

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

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

    public List<ConsultationResponseDTO> getConsultationsByDoctor(Long doctorId) {

        User doctor = userRepository.findById(doctorId)

                .orElseThrow(() -> new ResourceNotFoundException("Médecin (Utilisateur) non trouvé avec l'ID : " + doctorId));

        return consultationRepository.findByDoctor(doctor).stream()

                .map(this::mapToDTO)

                .collect(Collectors.toList());

    }


// Mettre à jour une consultation

    @Transactional // Assurez-vous que cette méthode est transactionnelle

    public ConsultationResponseDTO updateConsultation(Long id, Consultation consultationDetails) {

        Consultation consultation = consultationRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));


// Mettre à jour les champs pertinents

        consultation.setReasonForConsultation(consultationDetails.getReasonForConsultation());

        consultation.setObservations(consultationDetails.getObservations());

        consultation.setDiagnosis(consultationDetails.getDiagnosis());

        consultation.setTreatmentPlan(consultationDetails.getTreatmentPlan());

        consultation.setNotes(consultationDetails.getNotes());


        Consultation updatedConsultation = consultationRepository.save(consultation);

        return mapToDTO(updatedConsultation);

    }


// Supprimer une consultation

    @Transactional // Assurez-vous que cette méthode est transactionnelle

    public void deleteConsultation(Long id) {

        Consultation consultation = consultationRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'ID : " + id));

        consultationRepository.delete(consultation);

    }


// Récupérer les consultations entre deux dates

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

    public List<ConsultationResponseDTO> getConsultationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        return consultationRepository.findByConsultationDateBetween(startDate, endDate).stream()

                .map(this::mapToDTO)

                .collect(Collectors.toList());

    }


// Récupérer les consultations par nom de famille du patient

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

    public List<ConsultationResponseDTO> getConsultationsByPatientLastName(String lastName) {

        return consultationRepository.findByPatientLastNameContainingIgnoreCase(lastName).stream()

                .map(this::mapToDTO)

                .collect(Collectors.toList());

    }


// Récupérer les consultations par mot-clé dans le diagnostic

    @Transactional(readOnly = true) // Assurez-vous que cette méthode est transactionnelle

    public List<ConsultationResponseDTO> getConsultationsByDiagnosisKeyword(String keyword) {

        return consultationRepository.findByDiagnosisContainingIgnoreCase(keyword).stream()

                .map(this::mapToDTO)

                .collect(Collectors.toList());

    }

}
