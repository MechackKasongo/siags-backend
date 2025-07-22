package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    // Méthode pour créer un nouveau patient ou mettre à jour un patient existant
    Patient savePatient(Patient patient);

    Optional<Patient> getPatientById(Long id);

    List<Patient> getAllPatients();

    void deletePatient(Long id);

    // Optional<Patient> findByNumeroDossier(String numeroDossier);
}