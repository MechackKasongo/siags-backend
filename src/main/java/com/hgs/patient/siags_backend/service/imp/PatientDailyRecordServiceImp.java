package com.hgs.patient.siags_backend.service.impl;


import com.hgs.patient.siags_backend.dto.PatientDailyRecordRequestDTO;

import com.hgs.patient.siags_backend.dto.PatientDailyRecordResponseDTO;

import com.hgs.patient.siags_backend.dto.PatientSummaryDTO;

import com.hgs.patient.siags_backend.dto.UserSummaryDTO;

import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;

import com.hgs.patient.siags_backend.model.Patient;

import com.hgs.patient.siags_backend.model.PatientDailyRecord;

import com.hgs.patient.siags_backend.model.User;

import com.hgs.patient.siags_backend.repository.PatientDailyRecordRepository;

import com.hgs.patient.siags_backend.repository.PatientRepository;

import com.hgs.patient.siags_backend.repository.UserRepository;

import com.hgs.patient.siags_backend.service.PatientDailyRecordService;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;

import java.time.LocalDate;

import java.util.List;

import java.util.stream.Collectors;


@Service

public class PatientDailyRecordServiceImp implements PatientDailyRecordService {


    private final PatientDailyRecordRepository dailyRecordRepository;

    private final PatientRepository patientRepository;

    private final UserRepository userRepository; // Pour trouver l'utilisateur qui enregistre

    private final ModelMapper modelMapper;


    @Autowired

    public PatientDailyRecordServiceImp(PatientDailyRecordRepository dailyRecordRepository,

                                        PatientRepository patientRepository,

                                        UserRepository userRepository,

                                        ModelMapper modelMapper) {

        this.dailyRecordRepository = dailyRecordRepository;

        this.patientRepository = patientRepository;

        this.userRepository = userRepository;

        this.modelMapper = modelMapper;

    }


    @Override

    public PatientDailyRecordResponseDTO createPatientDailyRecord(PatientDailyRecordRequestDTO requestDTO) {

// 1. Vérifier l'existence du patient

        Patient patient = patientRepository.findById(requestDTO.getPatientId())

                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + requestDTO.getPatientId()));


// 2. Récupérer l'utilisateur actuellement authentifié pour 'recordedBy'

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User recordedBy = userRepository.findById(userDetails.getId())

                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur enregistreur non trouvé avec l'ID: " + userDetails.getId()));


// 3. Mapper la DTO de requête vers l'entité

        PatientDailyRecord dailyRecord = modelMapper.map(requestDTO, PatientDailyRecord.class);


// 4. Assigner les entités Patient et User

        dailyRecord.setPatient(patient);

        dailyRecord.setRecordedBy(recordedBy);

// Les champs createdAt et updatedAt sont gérés par les @PrePersist/@PreUpdate de l'entité


// 5. Sauvegarder l'enregistrement

        PatientDailyRecord savedRecord = dailyRecordRepository.save(dailyRecord);


// 6. Convertir l'entité sauvegardée en DTO de réponse

        return convertToResponseDTO(savedRecord);

    }


    @Override

    public PatientDailyRecordResponseDTO getPatientDailyRecordById(Long id) {

        PatientDailyRecord record = dailyRecordRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement quotidien non trouvé avec l'ID: " + id));

        return convertToResponseDTO(record);

    }


    @Override

    public List<PatientDailyRecordResponseDTO> getAllPatientDailyRecords() {

        return dailyRecordRepository.findAll().stream()

                .map(this::convertToResponseDTO)

                .collect(Collectors.toList());

    }


    @Override

    public Page<PatientDailyRecordResponseDTO> getAllPatientDailyRecordsPaginated(Pageable pageable) {

        return dailyRecordRepository.findAll(pageable)

                .map(this::convertToResponseDTO);

    }


    @Override

    public PatientDailyRecordResponseDTO updatePatientDailyRecord(Long id, PatientDailyRecordRequestDTO requestDTO) {

        PatientDailyRecord existingRecord = dailyRecordRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement quotidien non trouvé avec l'ID: " + id));


// Vérifier et mettre à jour le patient si l'ID diffère (rarement le cas pour une mise à jour)

        if (!existingRecord.getPatient().getId().equals(requestDTO.getPatientId())) {

            Patient newPatient = patientRepository.findById(requestDTO.getPatientId())

                    .orElseThrow(() -> new ResourceNotFoundException("Nouveau patient non trouvé avec l'ID: " + requestDTO.getPatientId()));

            existingRecord.setPatient(newPatient);

        }


// Mettre à jour les champs modifiables

        existingRecord.setRecordDate(requestDTO.getRecordDate());

        existingRecord.setObservations(requestDTO.getObservations());

        existingRecord.setMedicationsAdministered(requestDTO.getMedicationsAdministered());

        existingRecord.setTemperature(requestDTO.getTemperature());

        existingRecord.setBloodPressure(requestDTO.getBloodPressure());

        existingRecord.setHeartRate(requestDTO.getHeartRate());

        existingRecord.setOxygenSaturation(requestDTO.getOxygenSaturation());

// L'utilisateur 'recordedBy' n'est pas modifié lors d'un update, car il représente qui l'a créé.

// Si besoin de changer qui a mis à jour, il faudrait un champ 'updatedBy'.


        PatientDailyRecord updatedRecord = dailyRecordRepository.save(existingRecord);

        return convertToResponseDTO(updatedRecord);

    }


    @Override

    public void deletePatientDailyRecord(Long id) {

        if (!dailyRecordRepository.existsById(id)) {

            throw new ResourceNotFoundException("Enregistrement quotidien non trouvé avec l'ID: " + id);

        }

        dailyRecordRepository.deleteById(id);

    }


    @Override

    public List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByPatientId(Long patientId) {

// Vérifier que le patient existe, ou laisser le repository lever l'exception si PatientDailyRecord est null

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patientId);

        }

        return dailyRecordRepository.findByPatientId(patientId).stream()

                .map(this::convertToResponseDTO)

                .collect(Collectors.toList());

    }


    @Override

    public List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByRecordDate(LocalDate recordDate) {

        return dailyRecordRepository.findByRecordDate(recordDate).stream()

                .map(this::convertToResponseDTO)

                .collect(Collectors.toList());

    }


    @Override

    public List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByPatientIdAndRecordDate(Long patientId, LocalDate recordDate) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patientId);

        }

        return dailyRecordRepository.findByPatientIdAndRecordDate(patientId, recordDate).stream()

                .map(this::convertToResponseDTO)

                .collect(Collectors.toList());

    }


    @Override

    public List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByRecordedById(Long recordedById) {

        if (!userRepository.existsById(recordedById)) {

            throw new ResourceNotFoundException("Utilisateur enregistreur non trouvé avec l'ID: " + recordedById);

        }

        return dailyRecordRepository.findByRecordedById(recordedById).stream()

                .map(this::convertToResponseDTO)

                .collect(Collectors.toList());

    }


// Méthode utilitaire pour convertir une entité en DTO de réponse

    private PatientDailyRecordResponseDTO convertToResponseDTO(PatientDailyRecord record) {

        PatientDailyRecordResponseDTO dto = modelMapper.map(record, PatientDailyRecordResponseDTO.class);


// Mapping des DTOs sommaires pour Patient et User

        if (record.getPatient() != null) {

            dto.setPatient(modelMapper.map(record.getPatient(), PatientSummaryDTO.class));

        }

        if (record.getRecordedBy() != null) {

            dto.setRecordedBy(modelMapper.map(record.getRecordedBy(), UserSummaryDTO.class));

        }

        return dto;

    }

}
