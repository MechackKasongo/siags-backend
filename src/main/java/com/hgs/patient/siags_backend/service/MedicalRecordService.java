package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.MedicalRecordDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.MedicalRecord;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.MedicalRecordRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service pour la gestion des opérations métier liées aux dossiers médicaux.
 * Il gère la création, la mise à jour et la récupération des dossiers médicaux.
 */
@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PatientRepository patientRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Crée un nouveau dossier médical pour un patient donné.
     *
     * @param patientId        L'ID du patient.
     * @param medicalRecordDTO Les données du dossier médical à créer.
     * @return Le DTO du dossier médical créé.
     */
    @Transactional
    public MedicalRecordDTO createMedicalRecord(Long patientId, MedicalRecordDTO medicalRecordDTO) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

        if (medicalRecordRepository.findByPatientId(patientId).isPresent()) {
            throw new IllegalStateException("Un dossier médical existe déjà pour ce patient.");
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setMedicalHistory(medicalRecordDTO.getMedicalHistory());
        medicalRecord.setFamilyHistory(medicalRecordDTO.getFamilyHistory());
        medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecord.setCurrentMedications(medicalRecordDTO.getCurrentMedications());
        medicalRecord.setNotes(medicalRecordDTO.getNotes());

        MedicalRecord savedMedicalRecord = medicalRecordRepository.save(medicalRecord);
        return mapToDTO(savedMedicalRecord);
    }

    /**
     * Récupère le dossier médical d'un patient par son ID.
     *
     * @param patientId L'ID du patient.
     * @return Le DTO du dossier médical.
     */
    @Transactional(readOnly = true)
    public MedicalRecordDTO getMedicalRecordByPatientId(Long patientId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé pour le patient avec l'ID : " + patientId));
        return mapToDTO(medicalRecord);
    }

    /**
     * Met à jour un dossier médical existant pour un patient.
     *
     * @param patientId        L'ID du patient.
     * @param medicalRecordDTO Les données de mise à jour.
     * @return Le DTO du dossier médical mis à jour.
     */
    @Transactional
    public MedicalRecordDTO updateMedicalRecord(Long patientId, MedicalRecordDTO medicalRecordDTO) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé pour le patient avec l'ID : " + patientId));

        // Mettez à jour les champs si les valeurs ne sont pas nulles
        Optional.ofNullable(medicalRecordDTO.getMedicalHistory()).ifPresent(medicalRecord::setMedicalHistory);
        Optional.ofNullable(medicalRecordDTO.getFamilyHistory()).ifPresent(medicalRecord::setFamilyHistory);
        Optional.ofNullable(medicalRecordDTO.getAllergies()).ifPresent(medicalRecord::setAllergies);
        Optional.ofNullable(medicalRecordDTO.getCurrentMedications()).ifPresent(medicalRecord::setCurrentMedications);
        Optional.ofNullable(medicalRecordDTO.getNotes()).ifPresent(medicalRecord::setNotes);

        MedicalRecord updatedMedicalRecord = medicalRecordRepository.save(medicalRecord);
        return mapToDTO(updatedMedicalRecord);
    }

    // Méthode utilitaire pour mapper l'entité MedicalRecord au DTO
    private MedicalRecordDTO mapToDTO(MedicalRecord medicalRecord) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(medicalRecord.getId());
        dto.setPatientId(medicalRecord.getPatient().getId());
        dto.setMedicalHistory(medicalRecord.getMedicalHistory());
        dto.setFamilyHistory(medicalRecord.getFamilyHistory());
        dto.setAllergies(medicalRecord.getAllergies());
        dto.setCurrentMedications(medicalRecord.getCurrentMedications());
        dto.setNotes(medicalRecord.getNotes());
        return dto;
    }
}
