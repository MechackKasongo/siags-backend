package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.AdmissionRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdmissionService {

    @Autowired
    private AdmissionRepository admissionRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Méthode utilitaire pour mapper une entité Admission à un AdmissionResponseDTO
    private AdmissionResponseDTO mapToDTO(Admission admission) {
        AdmissionResponseDTO dto = new AdmissionResponseDTO();
        dto.setId(admission.getId());
        dto.setAdmissionDate(admission.getAdmissionDate());
        dto.setReasonForAdmission(admission.getReasonForAdmission());
        dto.setAssignedDepartment(admission.getAssignedDepartment());
        dto.setRoomNumber(admission.getRoomNumber());
        dto.setBedNumber(admission.getBedNumber());
        dto.setStatus(admission.getStatus());
        dto.setDischargeDate(admission.getDischargeDate());
        dto.setDischargeSummary(admission.getDischargeSummary());

        // Remplir les informations du patient
        if (admission.getPatient() != null) {
            dto.setPatientId(admission.getPatient().getId());
            // Concaténer nom et prénom pour le nom complet du patient en utilisant les nouveaux noms de champs
            dto.setPatientNomComplet(admission.getPatient().getLastName() + " " + admission.getPatient().getFirstName());
            // REMARQUE : Il est recommandé de renommer 'patientNomComplet' en 'patientFullName'
            // dans votre AdmissionResponseDTO pour maintenir la cohérence anglaise.
        }

        // Remplir le nom d'utilisateur du personnel d'admission (si présent)
        if (admission.getAdmissionPersonnel() != null) {
            dto.setAdmissionPersonnelUsername(admission.getAdmissionPersonnel().getUsername());
        }

        return dto;
    }

    // Créer une nouvelle admission (retourne le DTO de l'admission créée)
    public AdmissionResponseDTO createAdmission(Long patientId, Admission admission) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

        admission.setPatient(patient);
        admission.setAdmissionDate(LocalDateTime.now());
        if (admission.getStatus() == null) {
            admission.setStatus(Admission.AdmissionStatus.ACTIVE);
        }
        Admission savedAdmission = admissionRepository.save(admission);
        return mapToDTO(savedAdmission);
    }

    // Récupérer une admission par son ID (retourne le DTO)
    public AdmissionResponseDTO getAdmissionById(Long id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID : " + id));
        return mapToDTO(admission);
    }

    // Récupérer toutes les admissions (retourne une liste de DTOs)
    public List<AdmissionResponseDTO> getAllAdmissions() {
        return admissionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les admissions d'un patient spécifique (retourne une liste de DTOs)
    public List<AdmissionResponseDTO> getAdmissionsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));
        return admissionRepository.findByPatient(patient).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour une admission (retourne le DTO de l'admission mise à jour)
    public AdmissionResponseDTO updateAdmission(Long id, Admission admissionDetails) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID : " + id));

        admission.setReasonForAdmission(admissionDetails.getReasonForAdmission());
        admission.setAssignedDepartment(admissionDetails.getAssignedDepartment());
        admission.setRoomNumber(admissionDetails.getRoomNumber());
        admission.setBedNumber(admissionDetails.getBedNumber());
        admission.setStatus(admissionDetails.getStatus());
        admission.setDischargeDate(admissionDetails.getDischargeDate());
        admission.setDischargeSummary(admissionDetails.getDischargeSummary());

        Admission updatedAdmission = admissionRepository.save(admission);
        return mapToDTO(updatedAdmission);
    }

    // Marquer une admission comme sortie (retourne le DTO)
    public AdmissionResponseDTO dischargeAdmission(Long id, String dischargeSummary) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID : " + id));

        admission.setStatus(Admission.AdmissionStatus.DISCHARGED);
        admission.setDischargeDate(LocalDateTime.now());
        admission.setDischargeSummary(dischargeSummary);

        Admission dischargedAdmission = admissionRepository.save(admission);
        return mapToDTO(dischargedAdmission);
    }

    // Supprimer une admission
    public void deleteAdmission(Long id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID : " + id));
        admissionRepository.delete(admission);
    }
}