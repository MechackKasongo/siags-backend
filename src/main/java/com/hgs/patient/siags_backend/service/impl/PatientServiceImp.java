package com.hgs.patient.siags_backend.service.impl;

import com.hgs.patient.siags_backend.dto.PatientResponseDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.service.OdooIntegrationService;
import com.hgs.patient.siags_backend.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final OdooIntegrationService odooIntegrationService;

    @Autowired
    public PatientServiceImp(PatientRepository patientRepository, ModelMapper modelMapper,
                             OdooIntegrationService odooIntegrationService) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.odooIntegrationService = odooIntegrationService;
    }

    private PatientResponseDTO convertToDto(Patient patient) {
        return modelMapper.map(patient, PatientResponseDTO.class);
    }

    @Override
    public Patient savePatient(Patient patient) {
        Patient savedPatient = patientRepository.save(patient);

        try {
            Map<String, Object> odooPartnerData = new HashMap<>();
            String fullName = savedPatient.getLastName() + " " + savedPatient.getFirstName();
            odooPartnerData.put("name", fullName);

            if (savedPatient.getEmail() != null) {
                odooPartnerData.put("email", savedPatient.getEmail());
            }
            if (savedPatient.getPhoneNumber() != null) {
                odooPartnerData.put("phone", savedPatient.getPhoneNumber());
            }
            if (savedPatient.getAddress() != null) {
                odooPartnerData.put("street", savedPatient.getAddress());
            }

            // Champs personnalisés Odoo : décommentez si vous les avez créés dans Odoo
            // if (savedPatient.getRecordNumber() != null) {
            //     odooPartnerData.put("x_record_number", savedPatient.getRecordNumber());
            // }
            // if (savedPatient.getBirthDate() != null) {
            //     odooPartnerData.put("x_birth_date", savedPatient.getBirthDate().toString());
            // }
            // if (savedPatient.getGender() != null) {
            //     odooPartnerData.put("x_gender", savedPatient.getGender());
            // }
            // if (savedPatient.getBloodType() != null) {
            //     odooPartnerData.put("x_blood_type", savedPatient.getBloodType());
            // }
            // if (savedPatient.getKnownIllnesses() != null) {
            //     odooPartnerData.put("x_known_illnesses", savedPatient.getKnownIllnesses());
            // }
            // if (savedPatient.getAllergies() != null) {
            //     odooPartnerData.put("x_allergies", savedPatient.getAllergies());
            // }

            Integer odooId = odooIntegrationService.createOdooPartner(odooPartnerData);

            if (odooId != null) {
                System.out.println("Patient créé avec succès dans Odoo. ID Odoo: " + odooId);
            } else {
                System.err.println("Échec de la création du patient dans Odoo. L'ID n'a pas été retourné.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'intégration Odoo pour le patient '" + savedPatient.getLastName() + " " + savedPatient.getFirstName() + "': " + e.getMessage());
            e.printStackTrace();
        }

        return savedPatient; // Retourne le patient enregistré localement
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public PatientResponseDTO getPatientDtoById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id));
        return convertToDto(patient);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<PatientResponseDTO> getAllPatientsDto() {
        return patientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id);
        }
        patientRepository.deleteById(id);
    }

    public PatientResponseDTO updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id));

        // Mettre à jour les champs du patient local avec les noms en anglais
        patient.setLastName(patientDetails.getLastName());
        patient.setFirstName(patientDetails.getFirstName());
        patient.setBirthDate(patientDetails.getBirthDate());
        patient.setGender(patientDetails.getGender());
        patient.setAddress(patientDetails.getAddress());
        patient.setPhoneNumber(patientDetails.getPhoneNumber());
        patient.setEmail(patientDetails.getEmail());
        patient.setRecordNumber(patientDetails.getRecordNumber());
        patient.setBloodType(patientDetails.getBloodType());
        patient.setKnownIllnesses(patientDetails.getKnownIllnesses());
        patient.setAllergies(patientDetails.getAllergies());

        Patient updatedLocalPatient = patientRepository.save(patient);

        // Optionnel: Ajouter ici la logique pour mettre à jour le patient correspondant dans Odoo.
        // Assurez-vous d'avoir un champ pour stocker l'ID Odoo dans votre entité Patient si vous faites cela.
        // try {
        //     if (updatedLocalPatient.getOdooId() != null) {
        //         Map<String, Object> odooUpdateData = new HashMap<>();
        //         odooUpdateData.put("name", updatedLocalPatient.getLastName() + " " + updatedLocalPatient.getFirstName());
        //         odooUpdateData.put("email", updatedLocalPatient.getEmail());
        //         // ... autres champs à mettre à jour et leurs mappings Odoo (standard ou x_custom)
        //         odooIntegrationService.updateOdooPartner(updatedLocalPatient.getOdooId(), odooUpdateData);
        //         System.out.println("Patient mis à jour dans Odoo. ID Odoo: " + updatedLocalPatient.getOdooId());
        //     }
        // } catch (Exception e) {
        //     System.err.println("Erreur lors de la mise à jour Odoo pour le patient: " + e.getMessage());
        // }

        return convertToDto(updatedLocalPatient);
    }

    public List<PatientResponseDTO> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPatientsDto();
        }
        // IMPORTANT : La méthode dans PatientRepository doit être renommée pour correspondre aux nouveaux champs
        // Par exemple: findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase
        return patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(searchTerm, searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<PatientResponseDTO> getAllPatientsPaginated(Pageable pageable) {
        Page<Patient> patientsPage = patientRepository.findAll(pageable);
        return patientsPage.map(this::convertToDto);
    }
}