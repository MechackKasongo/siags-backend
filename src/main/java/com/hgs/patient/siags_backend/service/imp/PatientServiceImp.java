package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.PatientRequestDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.service.MedicalRecordService;
import com.hgs.patient.siags_backend.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordService medicalRecordService;
    private final ModelMapper modelMapper;

    @Autowired
    public PatientServiceImp(PatientRepository patientRepository, MedicalRecordService medicalRecordService, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.medicalRecordService = medicalRecordService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Patient createPatient(PatientRequestDTO patientDTO) {
        Patient patient = modelMapper.map(patientDTO, Patient.class);
        Patient savedPatient = patientRepository.save(patient);
        medicalRecordService.createMedicalRecordForPatient(savedPatient.getId());

        return savedPatient;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional
    public Patient updatePatient(Long id, PatientRequestDTO patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));

        modelMapper.map(patientDTO, existingPatient);

        return patientRepository.save(existingPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findPatientsByName(String name) {
        return patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(name, name);
    }
}