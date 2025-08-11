package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.dto.PatientGenderDistributionDTO;
import com.hgs.patient.siags_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByRecordNumber(String recordNumber);

    List<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName);

    @Query("SELECT new com.hgs.patient.siags_backend.dto.PatientGenderDistributionDTO(p.gender, COUNT(p.id)) " +
            "FROM Patient p GROUP BY p.gender")
    List<PatientGenderDistributionDTO> countPatientsByGender();


    /**
     * Récupère un patient par son ID en chargeant de manière eager (JOIN FETCH)
     *
     * @param id L'ID du patient.
     * @return Un Optional contenant le patient et son dossier médical, ou vide si non trouvé.
     */
    @Query("SELECT p FROM Patient p JOIN FETCH p.medicalRecord WHERE p.id = :id")
    Optional<Patient> findByIdWithMedicalRecord(Long id);

    /**
     * Récupère tous les patients en chargeant de manière eager (JOIN FETCH)
     * leurs dossiers médicaux.
     *
     * @return Une liste de tous les patients avec leurs dossiers médicaux.
     */
    @Query("SELECT p FROM Patient p JOIN FETCH p.medicalRecord")
    List<Patient> findAllWithMedicalRecords();
}