package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.hgs.patient.siags_backend.dto.PatientGenderDistributionDTO;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByRecordNumber(String recordNumber);

    List<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName);

    @Query("SELECT new com.hgs.patient.siags_backend.dto.PatientGenderDistributionDTO(p.gender, COUNT(p.id)) " +
            "FROM Patient p GROUP BY p.gender")
    List<PatientGenderDistributionDTO> countPatientsByGender();
}