package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Indique à Spring que c'est un composant de dépôt
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    // Méthode personnalisée pour trouver les admissions par patient
    List<Admission> findByPatient(Patient patient);

    // Méthode personnalisée pour trouver les admissions actives d'un patient
    List<Admission> findByPatientAndStatus(Patient patient, Admission.AdmissionStatus status);

    // Méthode pour trouver la dernière admission active d'un patient (si nécessaire)
    // Optional<Admission> findTopByPatientAndStatusOrderByAdmissionDateDesc(Patient patient, Admission.AdmissionStatus status);
}