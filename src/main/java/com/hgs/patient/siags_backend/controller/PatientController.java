package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.service.PatientService;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.dto.PatientRequest;
import com.hgs.patient.siags_backend.dto.PatientResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors; // Pour la conversion des listes

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Méthode utilitaire pour convertir Patient en PatientResponse
    private PatientResponse convertToDto(Patient patient) {
        PatientResponse dto = new PatientResponse();
        dto.setId(patient.getId());
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setSexe(patient.getSexe());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setAdresse(patient.getAdresse());
        dto.setTelephone(patient.getTelephone());
        dto.setNumeroDossier(patient.getNumeroDossier());
        return dto;
    }

    // Méthode utilitaire pour convertir PatientRequest en Patient
    // Cette méthode est utilisée pour créer ou mettre à jour un patient
    private Patient convertToEntity(PatientRequest dto) {
        Patient patient = new Patient();
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setSexe(dto.getSexe());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setAdresse(dto.getAdresse());
        patient.setTelephone(dto.getTelephone());
        patient.setNumeroDossier(dto.getNumeroDossier());
        return patient;
    }

    // Endpoint pour créer un nouveau patient
    // @RequestBody prend un PatientRequest et @Valid le valide
    // La méthode renvoie un PatientResponse
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest) {
        Patient patientToSave = convertToEntity(patientRequest);
        Patient savedPatient = patientService.savePatient(patientToSave);
        return new ResponseEntity<>(convertToDto(savedPatient), HttpStatus.CREATED);
    }

    // Endpoint pour récupérer un patient par son ID
    // La méthode renvoie un PatientResponse
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));
        return new ResponseEntity<>(convertToDto(patient), HttpStatus.OK);
    }

    // Endpoint pour récupérer tous les patients
    // La méthode renvoie une liste de PatientResponse
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        List<PatientResponse> patientResponses = patients.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(patientResponses, HttpStatus.OK);
    }

    // Endpoint pour mettre à jour un patient existant
    // @RequestBody prend un PatientRequest et @Valid le valide
    // La méthode renvoie un PatientResponse
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest patientRequest) {
        // Créer une entité à partir du DTO et lui donner l'ID du chemin
        Patient patientToUpdate = convertToEntity(patientRequest);
        patientToUpdate.setId(id);

        // Le service gérera la logique de mise à jour et lèvera l'exception si non trouvé
        Patient updatedPatient = patientService.savePatient(patientToUpdate);
        return new ResponseEntity<>(convertToDto(updatedPatient), HttpStatus.OK);
    }

    // Endpoint pour supprimer un patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        // Le service gérera la suppression et lèvera l'exception si non trouvé
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}