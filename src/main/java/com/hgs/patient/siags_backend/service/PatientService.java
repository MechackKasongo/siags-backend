package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.PatientRequestDTO;
import com.hgs.patient.siags_backend.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Patient createPatient(PatientRequestDTO patientDTO);

    Optional<Patient> getPatientById(Long id);

    Iterable<Patient> getAllPatients();

    Patient updatePatient(Long id, PatientRequestDTO patientDTO);

    void deletePatient(Long id);

    List<Patient> findPatientsByName(String name);
}