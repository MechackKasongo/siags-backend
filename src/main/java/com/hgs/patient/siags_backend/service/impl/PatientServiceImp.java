package com.hgs.patient.siags_backend.service.impl;

import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.service.PatientService;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException; // <-- NOUVEL IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImp(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient savePatient(Patient patient) {
        // Pour la mise à jour, nous voulons nous assurer que le patient existe
        if (patient.getId() != null) { // Si c'est une mise à jour (ID fourni)
            patientRepository.findById(patient.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patient.getId()));
        }
        System.out.println("Sauvegarde du patient: " + patient.getNom() + " " + patient.getPrenom());
        return patientRepository.save(patient);
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        System.out.println("Récupération du patient avec l'ID: " + id);
        // La méthode findById renvoie déjà un Optional, donc le service peut toujours retourner un Optional ici.
        // C'est le contrôleur ou le consommateur du service qui décidera de lancer l'exception si Optional est vide.
        // OU, si vous voulez que le service soit responsable de l'exception:
        // return Optional.of(patientRepository.findById(id)
        //                      .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id)));
        return patientRepository.findById(id); // Nous gardons Optional ici pour le moment.
    }

    @Override
    public List<Patient> getAllPatients() {
        System.out.println("Récupération de tous les patients.");
        return patientRepository.findAll();
    }

    @Override
    public void deletePatient(Long id) {
        System.out.println("Suppression du patient avec l'ID: " + id);
        // Vérifie si le patient existe avant de tenter de supprimer
        if (!patientRepository.existsById(id)) { // Utiliser existsById pour vérifier l'existence
            throw new ResourceNotFoundException("Impossible de supprimer. Patient non trouvé avec l'ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}