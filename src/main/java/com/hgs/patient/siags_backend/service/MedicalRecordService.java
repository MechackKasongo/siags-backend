package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.MedicalRecordDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.MedicalRecord;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.MedicalRecordRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PatientRepository patientRepository, ModelMapper modelMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Crée un dossier médical initial pour un patient.
     * Cette méthode est appelée par le PatientService lors de la création d'un patient.
     * Elle ne prend pas de DTO en paramètre pour simplifier le processus d'initialisation.
     */
    @Transactional
    public MedicalRecord createMedicalRecordForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

        if (medicalRecordRepository.findByPatientId(patientId).isPresent()) {
            throw new IllegalStateException("Un dossier médical existe déjà pour ce patient.");
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setCreatedDate(LocalDateTime.now());

        return medicalRecordRepository.save(medicalRecord);
    }

    /**
     * Met à jour les informations du dossier médical avec les données d'un DTO.
     * Cette méthode est appelée par le contrôleur API du dossier médical.
     */
    @Transactional
    public MedicalRecordDTO updateMedicalRecordDetails(Long patientId, MedicalRecordDTO medicalRecordDTO) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé pour le patient avec l'ID : " + patientId));

        modelMapper.map(medicalRecordDTO, medicalRecord);
        medicalRecord.setUpdatedDate(LocalDateTime.now());

        MedicalRecord updatedMedicalRecord = medicalRecordRepository.save(medicalRecord);
        return modelMapper.map(updatedMedicalRecord, MedicalRecordDTO.class);
    }

    @Transactional(readOnly = true)
    public MedicalRecordDTO getMedicalRecordByPatientId(Long patientId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé pour le patient avec l'ID : " + patientId));
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    @Transactional(readOnly = true)
    public MedicalRecordDTO getMedicalRecordById(Long recordId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé avec l'ID : " + recordId));
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }
}