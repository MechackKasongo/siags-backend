package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Indique à Spring que c'est un composant de dépôt
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // Méthode personnalisée pour trouver les consultations par patient
    List<Consultation> findByPatient(Patient patient);

    // Méthode personnalisée pour trouver les consultations par médecin (User)
    List<Consultation> findByDoctor(User doctor);

    // Méthode pour trouver les consultations d'un patient entre deux dates
    List<Consultation> findByPatientAndConsultationDateBetween(Patient patient, LocalDateTime startDate, LocalDateTime endDate);

    // Méthode pour trouver les consultations d'un médecin entre deux dates
    List<Consultation> findByDoctorAndConsultationDateBetween(User doctor, LocalDateTime startDate, LocalDateTime endDate);

    // Méthode pour trouver les consultations d'un patient avec un diagnostic spécifique
    List<Consultation> findByPatientAndDiagnosisContainingIgnoreCase(Patient patient, String diagnosis);


//    // Méthode pour trouver les consultations d'un médecin avec un diagnostic spécifique
//    List<Consultation> findByDoctorAndDiagnosisContainingIgnoreCase(User doctor, String diagnosis);
//    // Méthode pour trouver les consultations d'un patient avec un traitement spécifique
//    List<Consultation> findByPatientAndTreatmentContainingIgnoreCase(Patient patient, String treatment);
//    // Méthode pour trouver les consultations d'un médecin avec un traitement spécifique
//    List<Consultation> findByDoctorAndTreatmentContainingIgnoreCase(User doctor, String treatment);
//    // Méthode pour trouver les consultations d'un patient avec un motif spécifique
//    List<Consultation> findByPatientAndReasonContainingIgnoreCase(Patient patient, String reason);
//    // Méthode pour trouver les consultations d'un médecin avec un motif spécifique
//    List<Consultation> findByDoctorAndReasonContainingIgnoreCase(User doctor, String reason);
//    // Méthode pour trouver les consultations d'un patient avec un statut spécifique
//    List<Consultation> findByPatientAndStatus(Patient patient, Consultation.ConsultationStatus status);
//    // Méthode pour trouver les consultations d'un médecin avec un statut spécifique
//    List<Consultation> findByDoctorAndStatus(User doctor, Consultation.ConsultationStatus status);
//    // Méthode pour trouver les consultations d'un patient avec un statut spécifique et entre deux dates
//    List<Consultation> findByPatientAndStatusAndConsultationDateBetween(Patient patient, Consultation.ConsultationStatus status, LocalDateTime startDate, LocalDateTime endDate);
//    // Méthode pour trouver les consultations d'un médecin avec un statut spécifique et entre deux dates
//    List<Consultation> findByDoctorAndStatusAndConsultationDateBetween(User doctor, Consultation.ConsultationStatus status, LocalDateTime startDate, LocalDateTime endDate);
//    // Méthode pour trouver les consultations d'un patient avec un statut spécifique
//    List<Consultation> findByPatientAndStatusAndDiagnosisContainingIgnoreCase(Patient patient,Consultation.ConsultationStatus status, String diagnosis);
}