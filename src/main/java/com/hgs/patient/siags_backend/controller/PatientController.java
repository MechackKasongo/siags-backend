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
        // Mappage des champs avec les nouveaux noms en anglais
        patient.setLastName(patientRequest.getLastName());
        patient.setFirstName(patientRequest.getFirstName());
        patient.setBirthDate(patientRequest.getBirthDate());
        patient.setGender(patientRequest.getGender());
        patient.setAddress(patientRequest.getAddress());
        patient.setPhoneNumber(patientRequest.getPhoneNumber());
        patient.setEmail(patientRequest.getEmail());
        patient.setRecordNumber(patientRequest.getRecordNumber());
        patient.setBloodType(patientRequest.getBloodType());
        patient.setKnownIllnesses(patientRequest.getKnownIllnesses());
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
            // Le nom de la colonne pour le tri doit correspondre au nom du champ dans l'entité Patient
            if (sortBy != null && !sortBy.isEmpty()) {
                // IMPORTANT : Si 'sortBy' est "nom" ou "prenom" dans la requête, il faudra le mapper à "lastName" ou "firstName"
                // ou informer l'utilisateur de passer les noms de champs anglais dans la requête sortBy
                // Exemple : sortBy = "lastName"
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
        // La méthode searchPatients dans PatientServiceImp devra être mise à jour pour chercher sur 'lastName' et 'firstName'
        List<PatientResponseDTO> patients = patientServiceImp.searchPatients(searchTerm);
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Ou HttpStatus.OK avec une liste vide
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
}