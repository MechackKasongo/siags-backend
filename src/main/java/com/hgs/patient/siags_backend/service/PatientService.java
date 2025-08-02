package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Patient createPatient(Patient patient);

    Optional<Patient> getPatientById(Long id);

    Iterable<Patient> getAllPatients();

    Patient updatePatient(Long id, Patient patientDetails);

    void deletePatient(Long id);

    // This method must be in the interface
    List<Patient> findPatientsByName(String name);
}
