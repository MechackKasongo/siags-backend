package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.PatientRequestDTO;
import com.hgs.patient.siags_backend.dto.PatientResponseDTO;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.service.PatientService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;
    private final ModelMapper modelMapper;

    // Le MedicalRecordService n'est plus injecté dans le contrôleur
    public PatientController(PatientService patientService, ModelMapper modelMapper) {
        this.patientService = patientService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDECIN', 'ROLE_INFIRMIERE')")
    public ResponseEntity<Iterable<PatientResponseDTO>> getAllPatients() {
        Iterable<Patient> patients = patientService.getAllPatients();
        Iterable<PatientResponseDTO> responseDTOs = StreamSupport.stream(patients.spliterator(), false)
                .map(patient -> modelMapper.map(patient, PatientResponseDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    // Le contrôleur appelle une seule méthode du service pour tout le processus
    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_WRITE')")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO patientDTO) {
        Patient newPatient = patientService.createPatient(patientDTO);
        PatientResponseDTO responseDTO = modelMapper.map(newPatient, PatientResponseDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // ... Le reste des méthodes du contrôleur reste inchangé
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(patient -> new ResponseEntity<>(modelMapper.map(patient, PatientResponseDTO.class), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_WRITE')")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequestDTO patientDTO) {
        Patient updatedPatient = patientService.updatePatient(id, patientDTO);
        PatientResponseDTO responseDTO = modelMapper.map(updatedPatient, PatientResponseDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_DELETE')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}