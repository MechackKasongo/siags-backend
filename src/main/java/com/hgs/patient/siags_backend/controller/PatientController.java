package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    // Qui peut créer un patient (ex: Receptionniste, Admin)
    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_WRITE')")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    //Qui peut lire les informations d'un patient (ex: Receptionniste, Medecin, Infirmier, Admin)
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    // Qui peut lister tous les patients (ex: Receptionniste, Medecin, Infirmier, Admin)
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public ResponseEntity<Iterable<Patient>> getAllPatients() {
        Iterable<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    // Qui peut modifier les informations d'un patient (ex: Receptionniste, Admin)
    @PreAuthorize("hasAuthority('PATIENT_WRITE')")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Qui peut supprimer un patient (ex: Admin)
    @PreAuthorize("hasAuthority('PATIENT_DELETE')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}