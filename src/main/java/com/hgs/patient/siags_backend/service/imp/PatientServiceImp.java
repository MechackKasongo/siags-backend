package com.hgs.patient.siags_backend.service.impl;


import com.hgs.patient.siags_backend.exception.OdooIntegrationException;

import com.hgs.patient.siags_backend.model.Patient;

import com.hgs.patient.siags_backend.repository.PatientRepository;

import com.hgs.patient.siags_backend.service.OdooService;

import com.hgs.patient.siags_backend.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.List;

import java.util.Optional;


@Service

public class PatientServiceImp implements PatientService {


    private final PatientRepository patientRepository;

    private final OdooService odooService;


    @Autowired

    public PatientServiceImp(PatientRepository patientRepository, OdooService odooService) {

        this.patientRepository = patientRepository;

        this.odooService = odooService;

    }


    @Override

    public Patient createPatient(Patient patient) {

        Patient savedPatient = patientRepository.save(patient);

        System.out.println("Patient sauvegardé localement avec ID: " + savedPatient.getId());


        try {

            Long odooContactId = odooService.createOrUpdateOdooPatientContact(savedPatient);

            savedPatient.setOdooContactId(odooContactId);

            patientRepository.save(savedPatient);

            System.out.println("Patient " + savedPatient.getId() + " synchronisé avec Odoo, ID Odoo: " + odooContactId);

        } catch (OdooIntegrationException e) {

            System.err.println("Échec de la synchronisation Odoo pour le patient " + savedPatient.getId() + ": " + e.getMessage());

        } catch (Exception e) {

            System.err.println("Erreur inattendue lors de la synchronisation Odoo pour le patient " + savedPatient.getId() + ": " + e.getMessage());

        }


        return savedPatient;

    }


    @Override

    public Optional<Patient> getPatientById(Long id) {

        return patientRepository.findById(id);

    }


    @Override

    public Iterable<Patient> getAllPatients() {

        return patientRepository.findAll();

    }


    @Override

    public Patient updatePatient(Long id, Patient patientDetails) {

        return patientRepository.findById(id).map(patient -> {

// --- CORRECTION ICI : Changer getDateOfBirth() en getBirthDate() ---

            patient.setFirstName(patientDetails.getFirstName());

            patient.setLastName(patientDetails.getLastName());

            patient.setBirthDate(patientDetails.getBirthDate()); // <-- MODIFIÉ ICI

            patient.setGender(patientDetails.getGender());

            patient.setPhoneNumber(patientDetails.getPhoneNumber());

            patient.setEmail(patientDetails.getEmail());

// Ajoutez d'autres champs à mettre à jour si nécessaire

            patient.setAddress(patientDetails.getAddress());

            patient.setRecordNumber(patientDetails.getRecordNumber());

            patient.setBloodType(patientDetails.getBloodType());

            patient.setKnownIllnesses(patientDetails.getKnownIllnesses());

            patient.setAllergies(patientDetails.getAllergies());


            Patient updatedPatient = patientRepository.save(patient);


            try {

                odooService.createOrUpdateOdooPatientContact(updatedPatient);

                System.out.println("Patient " + updatedPatient.getId() + " mis à jour localement et synchronisé avec Odoo.");

            } catch (OdooIntegrationException e) {

                System.err.println("Échec de la synchronisation Odoo lors de la mise à jour du patient " + updatedPatient.getId() + ": " + e.getMessage());

            } catch (Exception e) {

                System.err.println("Erreur inattendue lors de la synchronisation Odoo (mise à jour) pour le patient " + updatedPatient.getId() + ": " + e.getMessage());

            }


            return updatedPatient;

        }).orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + id));

    }


    @Override

    public void deletePatient(Long id) {

        patientRepository.deleteById(id);

        System.out.println("Patient " + id + " supprimé localement.");

    }


    @Override

    public List<Patient> findPatientsByName(String name) {

        return patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(name, name);

    }

}