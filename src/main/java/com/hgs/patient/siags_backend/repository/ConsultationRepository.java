package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO; // Importez le DTO
import com.hgs.patient.siags_backend.dto.DiagnosisFrequencyDTO; // Importez le DTO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // Récupérer toutes les consultations entre deux dates, quelle que soit le patient/médecin
    List<Consultation> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Récupérer les consultations par nom de famille du patient (recherche insensible à la casse)
    List<Consultation> findByPatientLastNameContainingIgnoreCase(String lastName);

    // Méthode pour trouver les consultations avec un mot-clé spécifique dans le diagnostic (insensible à la casse)
    List<Consultation> findByDiagnosisContainingIgnoreCase(String diagnosisKeyword);

    /**
     * Récupère le nombre de consultations par médecin.
     * Joint la table des utilisateurs pour obtenir le nom d'utilisateur et le nom complet du médecin.
     */

    @Query("SELECT new com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO(c.doctor.id, c.doctor.username, c.doctor.nomComplet, COUNT(c.id)) " +
            "FROM Consultation c GROUP BY c.doctor.id, c.doctor.username, c.doctor.nomComplet")
    List<ConsultationCountByDoctorDTO> countConsultationsByDoctor();

    /**
     * Récupère la fréquence des diagnostics (les diagnostics les plus courants).
     * Regroupe les consultations par diagnostic et compte les occurrences.
     */

    @Query("SELECT new com.hgs.patient.siags_backend.dto.DiagnosisFrequencyDTO(c.diagnosis, COUNT(c.id)) " +
            "FROM Consultation c WHERE c.diagnosis IS NOT NULL AND c.diagnosis != '' GROUP BY c.diagnosis ORDER BY COUNT(c.id) DESC")
    List<DiagnosisFrequencyDTO> countDiagnosisFrequency();

    /**
     * Récupère le nombre total de consultations entre deux dates.
     */

    @Query("SELECT COUNT(c.id) FROM Consultation c WHERE c.consultationDate BETWEEN :startDate AND :endDate")
    Long countConsultationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

}