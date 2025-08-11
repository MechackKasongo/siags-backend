package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.MedicalEventRequestDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.MedicalEvent;
import com.hgs.patient.siags_backend.model.MedicalRecord;
import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.MedicalEventRepository;
import com.hgs.patient.siags_backend.repository.MedicalRecordRepository;
import com.hgs.patient.siags_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MedicalEventService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalEventRepository medicalEventRepository;
    private final UserRepository userRepository; // Remplacement de MedecinRepository par UserRepository

    public MedicalEventService(MedicalRecordRepository medicalRecordRepository,
                               MedicalEventRepository medicalEventRepository,
                               UserRepository userRepository) { // Remplacement dans le constructeur
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalEventRepository = medicalEventRepository;
        this.userRepository = userRepository;
    }

    public MedicalEvent addMedicalEvent(Long recordId, MedicalEventRequestDTO eventDTO) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé avec l'ID : " + recordId));

        User medecin = userRepository.findById(eventDTO.getMedecinId()) // Utilisation de userRepository
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID : " + eventDTO.getMedecinId()));

        MedicalEvent medicalEvent = new MedicalEvent();
        medicalEvent.setMedicalRecord(medicalRecord);
        medicalEvent.setMedecin(medecin);
        medicalEvent.setEventType(eventDTO.getEventType());
        medicalEvent.setDescription(eventDTO.getDescription());
        medicalEvent.setEventDate(LocalDateTime.now());

        return medicalEventRepository.save(medicalEvent);
    }
}