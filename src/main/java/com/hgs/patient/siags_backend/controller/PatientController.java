package com.hgs.patient.siags_backend.controller;


import com.hgs.patient.siags_backend.model.Patient;

import com.hgs.patient.siags_backend.service.PatientService;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController

@RequestMapping("/api/patients")

public class PatientController {


    private final PatientService patientService;


    public PatientController(PatientService patientService) {

        this.patientService = patientService;

    }


    @PostMapping

    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {

        Patient createdPatient = patientService.createPatient(patient);

        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);

    }


    @GetMapping("/{id}")

    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {

        return patientService.getPatientById(id)

                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))

                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }


    @GetMapping

    public ResponseEntity<Iterable<Patient>> getAllPatients() {

        Iterable<Patient> patients = patientService.getAllPatients();

        return new ResponseEntity<>(patients, HttpStatus.OK);

    }


    @PutMapping("/{id}")

    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {

        Patient updatedPatient = patientService.updatePatient(id, patientDetails);

        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);

    }


    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {

        patientService.deletePatient(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
