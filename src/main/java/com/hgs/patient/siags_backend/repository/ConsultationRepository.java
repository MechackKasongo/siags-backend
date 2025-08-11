package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO;
import com.hgs.patient.siags_backend.dto.DiagnosisFrequencyDTO;
import com.hgs.patient.siags_backend.model.Consultation;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // Utilisation de la méthode de nommage.
    List<Consultation> findByPatient(Patient patient);
    List<Consultation> findByDoctor(User doctor);
    List<Consultation> findByPatientAndConsultationDateBetween(Patient patient, LocalDateTime startDate, LocalDateTime endDate);
    List<Consultation> findByDoctorAndConsultationDateBetween(User doctor, LocalDateTime startDate, LocalDateTime endDate);
    List<Consultation> findByPatientLastNameContainingIgnoreCase(String lastName);
    List<Consultation> findByDiagnosisContainingIgnoreCase(String diagnosisKeyword);

    List<Consultation> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * @description Récupère le nombre de consultations par médecin.
     * Cette requête JPQL est déjà bien optimisée et utilise le DTO pour une projection efficace.
     * Les champs GROUP BY sont corrects.
     */
    @Query("SELECT new com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO(c.doctor.id, c.doctor.username, c.doctor.nomComplet, COUNT(c.id)) " +
            "FROM Consultation c GROUP BY c.doctor.id, c.doctor.username, c.doctor.nomComplet")
    List<ConsultationCountByDoctorDTO> countConsultationsByDoctor();

    /**
     * @description Récupère la fréquence des diagnostics (les plus courants).
     * La requête est correcte, elle filtre les diagnostics nuls ou vides avant de compter.
     */
    @Query("SELECT new com.hgs.patient.siags_backend.dto.DiagnosisFrequencyDTO(c.diagnosis, COUNT(c.id)) " +
            "FROM Consultation c WHERE c.diagnosis IS NOT NULL AND c.diagnosis != '' GROUP BY c.diagnosis ORDER BY COUNT(c.id) DESC")
    List<DiagnosisFrequencyDTO> countDiagnosisFrequency();

    /**
     * @description Récupère le nombre total de consultations entre deux dates.
     * La requête est correcte et efficace pour un simple comptage.
     */
    @Query("SELECT COUNT(c.id) FROM Consultation c WHERE c.consultationDate BETWEEN :startDate AND :endDate")
    Long countConsultationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}