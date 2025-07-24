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
    private final OdooIntegrationService odooIntegrationService; // <--- INJECTION : Déclarez votre service Odoo

    @Autowired
    public PatientServiceImp(PatientRepository patientRepository, ModelMapper modelMapper,
                             OdooIntegrationService odooIntegrationService) { // <--- AJOUT AU CONSTRUCTEUR pour l'injection
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.odooIntegrationService = odooIntegrationService; // <--- INITIALISATION
    }

    private PatientResponseDTO convertToDto(Patient patient) {
        return modelMapper.map(patient, PatientResponseDTO.class);
    }

    @Override
    public Patient savePatient(Patient patient) {
        // 1. Sauvegarder le patient dans votre base de données locale en premier
        Patient savedPatient = patientRepository.save(patient);

        // 2. Intégration avec Odoo pour créer le partenaire
        try {
            Map<String, Object> odooPartnerData = new HashMap<>();

            // Mappage **crucial** des champs vers Odoo
            // Odoo utilise 'name' pour le nom complet du partenaire
            String fullName = patient.getNom() + " " + patient.getPrenom();
            odooPartnerData.put("name", fullName); // <-- CORRECTION ICI : Utilisez 'name' pour Odoo

            // Mappez les autres champs pertinents de votre entité Patient aux champs standards ou personnalisés d'Odoo
            if (patient.getEmail() != null) {
                odooPartnerData.put("email", patient.getEmail());
            }
            if (patient.getTelephone() != null) {
                odooPartnerData.put("phone", patient.getTelephone());
            }
            if (patient.getAdresse() != null) {
                odooPartnerData.put("street", patient.getAdresse()); // Exemple: 'street' est un champ d'adresse courant dans Odoo
            }

            // Pour les champs qui ne sont pas standards dans le modèle 'res.partner' d'Odoo
            // (comme 'genre', 'dateDeNaissance', 'numeroDossier', 'typeDeSang', 'maladiesConnues', 'allergies'):
            // Vous devrez les mapper à des **champs personnalisés** que vous auriez créés dans votre instance Odoo.
            // Ces champs personnalisés sont souvent préfixés par 'x_'.
            // Si ces champs 'x_' n'existent pas dans votre Odoo, Odoo ignorera les données pour ces clés
            // ou lèvera une erreur si un champ requis n'est pas fourni.

            // Exemple pour un champ personnalisé Odoo 'x_numero_dossier' :
            if (patient.getNumeroDossier() != null) {
                odooPartnerData.put("x_numero_dossier", patient.getNumeroDossier());
            }
            // Exemple pour la date de naissance (Odoo attend généralement un format de chaîne pour les dates, ex: "YYYY-MM-DD") :
            if (patient.getDateDeNaissance() != null) {
                odooPartnerData.put("x_date_naissance", patient.getDateDeNaissance().toString());
            }
            // Exemple pour le genre :
            if (patient.getGenre() != null) {
                odooPartnerData.put("x_genre", patient.getGenre());
            }
            // Continuez pour 'typeDeSang', 'maladiesConnues', 'allergies' avec leurs noms de champs Odoo personnalisés :
            if (patient.getTypeDeSang() != null) {
                odooPartnerData.put("x_type_de_sang", patient.getTypeDeSang());
            }
            if (patient.getMaladiesConnues() != null) {
                odooPartnerData.put("x_maladies_connues", patient.getMaladiesConnues());
            }
            if (patient.getAllergies() != null) {
                odooPartnerData.put("x_allergies", patient.getAllergies());
            }


            // Appel à OdooIntegrationService pour créer le partenaire
            Integer odooId = odooIntegrationService.createOdooPartner(odooPartnerData);

            if (odooId != null) {
                System.out.println("Patient créé avec succès dans Odoo. ID Odoo: " + odooId);
                // Optionnel: Si vous souhaitez stocker l'ID Odoo dans votre entité Patient locale
                // Pour cela, vous auriez besoin d'ajouter un champ 'odooId' à votre entité Patient.
                // savedPatient.setOdooId(odooId);
                // patientRepository.save(savedPatient); // Re-sauvegarder le patient local si vous avez mis à jour 'odooId'
            } else {
                System.err.println("Échec de la création du patient dans Odoo. L'ID n'a pas été retourné.");
            }
        } catch (Exception e) {
            // C'est important de gérer cette exception pour ne pas bloquer l'enregistrement du patient localement
            // si l'intégration Odoo échoue. Vous devez décider si l'échec de l'intégration Odoo doit :
            // 1. Empêcher la création du patient localement (relancer l'exception après log).
            // 2. Permettre la création locale mais logger l'échec Odoo (comportement actuel).
            System.err.println("Erreur lors de l'intégration Odoo pour le patient '" + patient.getNom() + " " + patient.getPrenom() + "': " + e.getMessage());
            e.printStackTrace(); // Affiche la pile d'appels pour le débogage
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
        // Optionnel: Ajouter ici la logique pour supprimer le patient correspondant dans Odoo si nécessaire.
        // Cela nécessiterait de stocker l'ID Odoo du patient quelque part (ex: dans l'entité Patient).
        // odooIntegrationService.deleteOdooPartner(patient.getOdooId());
    }

    public PatientResponseDTO updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id));

        // Mettre à jour les champs du patient local
        patient.setNom(patientDetails.getNom());
        patient.setPrenom(patientDetails.getPrenom());
        patient.setDateDeNaissance(patientDetails.getDateDeNaissance());
        patient.setGenre(patientDetails.getGenre());
        patient.setAdresse(patientDetails.getAdresse());
        patient.setTelephone(patientDetails.getTelephone());
        patient.setEmail(patientDetails.getEmail());
        patient.setNumeroDossier(patientDetails.getNumeroDossier());
        patient.setTypeDeSang(patientDetails.getTypeDeSang());
        patient.setMaladiesConnues(patientDetails.getMaladiesConnues());
        patient.setAllergies(patientDetails.getAllergies());

        Patient updatedLocalPatient = patientRepository.save(patient);

        // Optionnel: Ajouter ici la logique pour mettre à jour le patient correspondant dans Odoo.
        // Cela nécessiterait de récupérer l'ID Odoo du patient (si stocké localement)
        // et de construire un Map de mise à jour similaire à celui de la création.
        // try {
        //     if (updatedLocalPatient.getOdooId() != null) { // Si vous avez un champ odooId
        //         Map<String, Object> odooUpdateData = new HashMap<>();
        //         odooUpdateData.put("name", updatedLocalPatient.getNom() + " " + updatedLocalPatient.getPrenom());
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
        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(searchTerm, searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<PatientResponseDTO> getAllPatientsPaginated(Pageable pageable) {
        Page<Patient> patientsPage = patientRepository.findAll(pageable);
        return patientsPage.map(this::convertToDto);
    }
}


//package com.hgs.patient.siags_backend.service.impl;
//
//import com.hgs.patient.siags_backend.model.Patient;
//import com.hgs.patient.siags_backend.repository.PatientRepository;
//import com.hgs.patient.siags_backend.service.PatientService;
//import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.hgs.patient.siags_backend.dto.PatientResponseDTO;
//import org.modelmapper.ModelMapper;
//
//import java.util.stream.Collectors;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class PatientServiceImp implements PatientService {
//
//    private final PatientRepository patientRepository;
//    private final ModelMapper modelMapper;
//
//    @Autowired
//    public PatientServiceImp(PatientRepository patientRepository, ModelMapper modelMapper) {
//        this.patientRepository = patientRepository;
//        this.modelMapper = modelMapper;
//    }
//
//    // Méthode utilitaire pour convertir Patient en PatientResponseDTO
//    private PatientResponseDTO convertToDto(Patient patient) {
//        return modelMapper.map(patient, PatientResponseDTO.class);
//    }
//
//    // Méthode utilitaire pour convertir PatientResponseDTO en Patient
//    private Patient convertToEntity(PatientResponseDTO patientDto) {
//        return modelMapper.map(patientDto, Patient.class);
//    }
//
//    @Override
//    public Patient savePatient(Patient patient) {
//        // Pour la mise à jour, nous voulons nous assurer que le patient existe
//        if (patient.getId() != null) {
//            patientRepository.findById(patient.getId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patient.getId()));
//        }
//        return patientRepository.save(patient);
//    }
//
//    @Override
//    public Optional<Patient> getPatientById(Long id) {
//        return patientRepository.findById(id);
//    }
//
//    @Override
//    public List<Patient> getAllPatients() {
//        return patientRepository.findAll();
//    }
//
//    @Override
//    public void deletePatient(Long id) {
//        if (!patientRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id);
//        }
//        patientRepository.deleteById(id);
//    }
//
//    // Méthode pour mettre à jour un patient existant et retourner le DTO
//    public PatientResponseDTO updatePatient(Long id, Patient patientDetails) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id));
//
//        patient.setNom(patientDetails.getNom());
//        patient.setPrenom(patientDetails.getPrenom());
//        patient.setDateNaissance(patientDetails.getDateNaissance());
//        patient.setSexe(patientDetails.getSexe());
//        patient.setAdresse(patientDetails.getAdresse());
//        patient.setTelephone(patientDetails.getTelephone());
//        patient.setNumeroDossier(patientDetails.getNumeroDossier());
//        // Ajoutez ici les autres champs si besoin
//
//        return convertToDto(patientRepository.save(patient));
//    }
//
//    // Recherche les patients par nom ou prénom
//    public List<PatientResponseDTO> searchPatients(String searchTerm) {
//        if (searchTerm == null || searchTerm.trim().isEmpty()) {
//            return patientRepository.findAll().stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//        }
//        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(searchTerm, searchTerm).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    // Récupère un patient en DTO par son ID
//    public PatientResponseDTO getPatientDtoById(Long id) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + id));
//        return convertToDto(patient);
//    }
//
//    // Récupère tous les patients en DTO
//    public List<PatientResponseDTO> getAllPatientsDto() {
//        return patientRepository.findAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//}