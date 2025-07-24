package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.PatientRequest;
import com.hgs.patient.siags_backend.dto.PatientResponseDTO;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.service.impl.PatientServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientServiceImp patientServiceImp;

    @Autowired
    public PatientController(PatientServiceImp patientServiceImp) {
        this.patientServiceImp = patientServiceImp;
    }

    // Convertit PatientRequest en Patient (entité)
    private Patient convertToEntity(PatientRequest patientRequest) {
        Patient patient = new Patient();
        patient.setNom(patientRequest.getNom());
        patient.setPrenom(patientRequest.getPrenom());
        patient.setDateDeNaissance(patientRequest.getDateDeNaissance());
        patient.setGenre(patientRequest.getGenre());
        patient.setAdresse(patientRequest.getAdresse());
        patient.setTelephone(patientRequest.getTelephone());
        patient.setEmail(patientRequest.getEmail());
        patient.setNumeroDossier(patientRequest.getNumeroDossier());
        patient.setTypeDeSang(patientRequest.getTypeDeSang());
        patient.setMaladiesConnues(patientRequest.getMaladiesConnues());
        patient.setAllergies(patientRequest.getAllergies());
        return patient;
    }

    // Créer un nouveau patient
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequest patientRequest) {
        Patient patientToSave = convertToEntity(patientRequest);
        Patient savedPatient = patientServiceImp.savePatient(patientToSave);
        PatientResponseDTO responseDto = patientServiceImp.getPatientDtoById(savedPatient.getId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Récupérer un patient par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        PatientResponseDTO patientDto = patientServiceImp.getPatientDtoById(id);
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Récupère tous les patients ou une page de patients avec pagination et tri.
     * URL: GET /api/patients?page=0&size=10&sortBy=nom&sortDir=ASC
     * Si aucun paramètre de pagination n'est fourni, retourne tous les patients.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<?> getAllPatients(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {

        if (page != null && size != null) {
            // Pagination est demandée
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.isEmpty()) {
                sort = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PatientResponseDTO> patientsPage = patientServiceImp.getAllPatientsPaginated(pageable);
            return ResponseEntity.ok(patientsPage);
        } else {
            // Pas de pagination, retourner tous les patients
            List<PatientResponseDTO> patients = patientServiceImp.getAllPatientsDto();
            return ResponseEntity.ok(patients);
        }
    }


    // Mettre à jour un patient existant
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest patientRequest) {
        Patient patientDetails = convertToEntity(patientRequest);
        patientDetails.setId(id);
        PatientResponseDTO updatedPatient = patientServiceImp.updatePatient(id, patientDetails);
        return ResponseEntity.ok(updatedPatient);
    }

    // Supprimer un patient
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientServiceImp.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Recherche des patients par terme de recherche (nom ou prénom).
     * URL: GET /api/patients/search?searchTerm=valeur
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(@RequestParam String searchTerm) {
        List<PatientResponseDTO> patients = patientServiceImp.searchPatients(searchTerm);
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Ou HttpStatus.OK avec une liste vide
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
}


//package com.hgs.patient.siags_backend.controller;
//
//import com.hgs.patient.siags_backend.model.Patient;
//import com.hgs.patient.siags_backend.service.PatientService;
//import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
//import com.hgs.patient.siags_backend.dto.PatientRequest;
//import com.hgs.patient.siags_backend.dto.PatientResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//        import jakarta.validation.Valid;
//
//import java.util.List;
//import java.util.stream.Collectors; // Pour la conversion des listes
//
//@RestController
//@RequestMapping("/api/patients")
//public class PatientController {
//
//    private final PatientService patientService;
//
//    @Autowired
//    public PatientController(PatientService patientService) {
//        this.patientService = patientService;
//    }
//
//    // Méthode utilitaire pour convertir Patient en PatientResponse
//    private PatientResponse convertToDto(Patient patient) {
//        PatientResponse dto = new PatientResponse();
//        dto.setId(patient.getId());
//        dto.setNom(patient.getNom());
//        dto.setPrenom(patient.getPrenom());
//        dto.setSexe(patient.getSexe());
//        dto.setDateNaissance(patient.getDateNaissance());
//        dto.setAdresse(patient.getAdresse());
//        dto.setTelephone(patient.getTelephone());
//        dto.setNumeroDossier(patient.getNumeroDossier());
//        return dto;
//    }
//
//    // Méthode utilitaire pour convertir PatientRequest en Patient
//    // Cette méthode est utilisée pour créer ou mettre à jour un patient
//    private Patient convertToEntity(PatientRequest dto) {
//        Patient patient = new Patient();
//        patient.setNom(dto.getNom());
//        patient.setPrenom(dto.getPrenom());
//        patient.setSexe(dto.getSexe());
//        patient.setDateNaissance(dto.getDateNaissance());
//        patient.setAdresse(dto.getAdresse());
//        patient.setTelephone(dto.getTelephone());
//        patient.setNumeroDossier(dto.getNumeroDossier());
//        return patient;
//    }
//
//    // Endpoint pour créer un nouveau patient
//    // @RequestBody prend un PatientRequest et @Valid le valide
//    // La méthode renvoie un PatientResponse
//    @PostMapping
//    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest) {
//        Patient patientToSave = convertToEntity(patientRequest);
//        Patient savedPatient = patientService.savePatient(patientToSave);
//        return new ResponseEntity<>(convertToDto(savedPatient), HttpStatus.CREATED);
//    }
//
//    // Endpoint pour récupérer un patient par son ID
//    // La méthode renvoie un PatientResponse
//    @GetMapping("/{id}")
//    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
//        Patient patient = patientService.getPatientById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));
//        return new ResponseEntity<>(convertToDto(patient), HttpStatus.OK);
//    }
//
//    // Endpoint pour récupérer tous les patients
//    // La méthode renvoie une liste de PatientResponse
//    @GetMapping
//    public ResponseEntity<List<PatientResponse>> getAllPatients() {
//        List<Patient> patients = patientService.getAllPatients();
//        List<PatientResponse> patientResponses = patients.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//        return new ResponseEntity<>(patientResponses, HttpStatus.OK);
//    }
//
//    // Endpoint pour mettre à jour un patient existant
//    // @RequestBody prend un PatientRequest et @Valid le valide
//    // La méthode renvoie un PatientResponse
//    @PutMapping("/{id}")
//    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest patientRequest) {
//        // Créer une entité à partir du DTO et lui donner l'ID du chemin
//        Patient patientToUpdate = convertToEntity(patientRequest);
//        patientToUpdate.setId(id);
//
//        // Le service gérera la logique de mise à jour et lèvera l'exception si non trouvé
//        Patient updatedPatient = patientService.savePatient(patientToUpdate);
//        return new ResponseEntity<>(convertToDto(updatedPatient), HttpStatus.OK);
//    }
//
//    // Endpoint pour supprimer un patient
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
//        // Le service gérera la suppression et lèvera l'exception si non trouvé
//        patientService.deletePatient(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}