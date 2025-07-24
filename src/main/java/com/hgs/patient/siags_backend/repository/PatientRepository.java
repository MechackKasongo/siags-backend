package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // méthode pour trouver un patient par son ID
    Optional<Patient> findByNumeroDossier(String numeroDossier);

    // méthode pour la recherche par nom ou prénom (insensible à la casse)
    List<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // méthode pour la recherche par numéro de dossier (insensible à la casse)
    // List<Patient> findByNumeroDossierContainingIgnoreCase(String numeroDossier);
}