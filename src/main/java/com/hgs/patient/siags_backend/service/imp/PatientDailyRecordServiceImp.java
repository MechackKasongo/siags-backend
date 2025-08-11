package com.hgs.patient.siags_backend.service.imp;

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
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import com.hgs.patient.siags_backend.service.PatientDailyRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class PatientDailyRecordServiceImp implements PatientDailyRecordService {

    private final PatientDailyRecordRepository dailyRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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
    @Transactional
    public PatientDailyRecordResponseDTO createPatientDailyRecord(PatientDailyRecordRequestDTO requestDTO) {
        Patient patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + requestDTO.getPatientId()));

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User recordedBy = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur enregistreur non trouvé avec l'ID: " + userDetails.getId()));

        PatientDailyRecord dailyRecord = modelMapper.map(requestDTO, PatientDailyRecord.class);

        dailyRecord.setPatient(patient);
        dailyRecord.setRecordedBy(recordedBy);

        PatientDailyRecord savedRecord = dailyRecordRepository.save(dailyRecord);

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
    @Transactional
    public PatientDailyRecordResponseDTO updatePatientDailyRecord(Long id, PatientDailyRecordRequestDTO requestDTO) {
        PatientDailyRecord existingRecord = dailyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement quotidien non trouvé avec l'ID: " + id));

        if (!existingRecord.getPatient().getId().equals(requestDTO.getPatientId())) {
            Patient newPatient = patientRepository.findById(requestDTO.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nouveau patient non trouvé avec l'ID: " + requestDTO.getPatientId()));
            existingRecord.setPatient(newPatient);
        }

        // Utilisation de ModelMapper pour la mise à jour des champs (si configuré correctement)
        modelMapper.map(requestDTO, existingRecord);

        PatientDailyRecord updatedRecord = dailyRecordRepository.save(existingRecord);
        return convertToResponseDTO(updatedRecord);
    }

    @Override
    @Transactional // Nécessaire car cette méthode modifie la base de données (delete)
    public void deleteDailyRecord(Long id) {
        if (!dailyRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enregistrement quotidien non trouvé avec l'ID: " + id);
        }
        dailyRecordRepository.deleteById(id);
    }

    @Override
    public List<PatientDailyRecordResponseDTO> getPatientDailyRecordsByPatientId(Long patientId) {
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

    private PatientDailyRecordResponseDTO convertToResponseDTO(PatientDailyRecord record) {
        PatientDailyRecordResponseDTO dto = modelMapper.map(record, PatientDailyRecordResponseDTO.class);

        if (record.getPatient() != null) {
            dto.setPatient(modelMapper.map(record.getPatient(), PatientSummaryDTO.class));
        }
        if (record.getRecordedBy() != null) {
            dto.setRecordedBy(modelMapper.map(record.getRecordedBy(), UserSummaryDTO.class));
        }
        return dto;
    }
}