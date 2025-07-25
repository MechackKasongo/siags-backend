package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // method to find a patient by their record number
    Optional<Patient> findByRecordNumber(String recordNumber); // Renamed from findByNumeroDossier

    // method for searching by last name or first name (case-insensitive)
    List<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName); // Renamed from findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase

    // method for searching by record number (case-insensitive)
    // List<Patient> findByRecordNumberContainingIgnoreCase(String recordNumber); // Renamed from findByNumeroDossierContainingIgnoreCase
}