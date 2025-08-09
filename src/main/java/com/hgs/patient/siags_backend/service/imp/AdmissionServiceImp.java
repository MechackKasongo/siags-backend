package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.AdmissionRequestDTO;
import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
import com.hgs.patient.siags_backend.dto.DepartmentSummaryDTO;
import com.hgs.patient.siags_backend.dto.PatientSummaryDTO;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.model.Department;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.AdmissionRepository;
import com.hgs.patient.siags_backend.repository.DepartmentRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import com.hgs.patient.siags_backend.service.AdmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdmissionServiceImp implements AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    // Removed the OdooService field.
    // private final OdooService odooService;

    @Autowired
    public AdmissionServiceImp(AdmissionRepository admissionRepository,
                               PatientRepository patientRepository,
                               DepartmentRepository departmentRepository,
                               ModelMapper modelMapper
                               // Removed OdooService from the constructor
                               // OdooService odooService
    ) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
        // Removed the assignment of OdooService.
        // this.odooService = odooService;
    }

    @Override
    public AdmissionResponseDTO createAdmission(AdmissionRequestDTO admissionRequestDTO) {
        // Vérifier l'existence du patient et du département
        Patient patient = patientRepository.findById(admissionRequestDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + admissionRequestDTO.getPatientId()));

        Department department = departmentRepository.findById(admissionRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé avec l'ID: " + admissionRequestDTO.getDepartmentId()));

        // Mapper le DTO vers l'entité Admission
        Admission admission = modelMapper.map(admissionRequestDTO, Admission.class);
        admission.setPatient(patient);
        admission.setAssignedDepartment(department);

        // Définir la date d'admission si non fournie (ou si toujours l'heure actuelle)
        if (admission.getAdmissionDate() == null) {
            admission.setAdmissionDate(LocalDateTime.now());
        }
        // Statut par défaut si non fourni
        if (admission.getStatus() == null) {
            admission.setStatus(Admission.AdmissionStatus.ACTIVE);
        }

        Admission savedAdmission = admissionRepository.save(admission);

        // Synchroniser avec Odoo - This block has been removed
        /*
        try {
            odooService.createOdooAdmission(savedAdmission);
            System.out.println("Admission " + savedAdmission.getId() + " créée localement et synchronisée avec Odoo.");
        } catch (OdooIntegrationException e) {
            System.err.println("Échec de la synchronisation Odoo lors de la création de l'admission " + savedAdmission.getId() + ": " + e.getMessage());
            // Envisagez de journaliser l'erreur ou de la relancer pour une gestion plus fine
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la synchronisation Odoo (création) pour l'admission " + savedAdmission.getId() + ": " + e.getMessage());
        }
        */

        return modelMapper.map(savedAdmission, AdmissionResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionResponseDTO getAdmissionById(Long id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID: " + id));
        return modelMapper.map(admission, AdmissionResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmissionResponseDTO> getAllAdmissions() {
        return admissionRepository.findAll().stream()
                .map(admission -> modelMapper.map(admission, AdmissionResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdmissionResponseDTO> getAllAdmissionsPaginated(Pageable pageable) {
        return admissionRepository.findAll(pageable)
                .map(admission -> modelMapper.map(admission, AdmissionResponseDTO.class));
    }

    @Override
    public AdmissionResponseDTO updateAdmission(Long id, AdmissionRequestDTO admissionRequestDTO) {
        Admission existingAdmission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission non trouvée avec l'ID: " + id));

        // Mettre à jour les champs de l'admission existante
        // Note: Patient et Department ne devraient pas changer pour une admission existante via cette méthode.
        // Si vous voulez permettre de les changer, il faudra ajouter la logique de recherche ici
        // et potentiellement un nouveau champ departmentId/patientId dans le DTO si le changement est permis.
        if (admissionRequestDTO.getReasonForAdmission() != null) {
            existingAdmission.setReasonForAdmission(admissionRequestDTO.getReasonForAdmission());
        }
        if (admissionRequestDTO.getRoomNumber() != null) {
            existingAdmission.setRoomNumber(admissionRequestDTO.getRoomNumber());
        }
        if (admissionRequestDTO.getBedNumber() != null) {
            existingAdmission.setBedNumber(admissionRequestDTO.getBedNumber());
        }
        if (admissionRequestDTO.getStatus() != null) {
            existingAdmission.setStatus(admissionRequestDTO.getStatus());
        }
        if (admissionRequestDTO.getDischargeDate() != null) {
            existingAdmission.setDischargeDate(admissionRequestDTO.getDischargeDate());
        }
        if (admissionRequestDTO.getDischargeSummary() != null) {
            existingAdmission.setDischargeSummary(admissionRequestDTO.getDischargeSummary());
        }
        if (admissionRequestDTO.getDiagnosis() != null) {
            existingAdmission.setDiagnosis(admissionRequestDTO.getDiagnosis());
        }

        // Si la date d'admission doit être mise à jour (peu courant, mais laissé pour flexibilité)
        if (admissionRequestDTO.getAdmissionDate() != null) {
            existingAdmission.setAdmissionDate(admissionRequestDTO.getAdmissionDate());
        }

        Admission updatedAdmission = admissionRepository.save(existingAdmission);

        // Synchroniser la mise à jour avec Odoo - This block has been removed
        /*
        try {
            odooService.updateOdooAdmission(updatedAdmission);
            System.out.println("Admission " + updatedAdmission.getId() + " mise à jour localement et synchronisée avec Odoo.");
        } catch (OdooIntegrationException e) {
            System.err.println("Échec de la synchronisation Odoo lors de la mise à jour de l'admission " + updatedAdmission.getId() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la synchronisation Odoo (mise à jour) pour l'admission " + updatedAdmission.getId() + ": " + e.getMessage());
        }
        */

        return modelMapper.map(updatedAdmission, AdmissionResponseDTO.class);
    }

    @Override
    public void deleteAdmission(Long id) {
        if (!admissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admission non trouvée avec l'ID: " + id);
        }
        admissionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmissionResponseDTO> getAdmissionsByPatientId(Long patientId) {
        // Vérifier si le patient existe avant de chercher ses admissions
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patientId);
        }
        List<Admission> admissions = admissionRepository.findByPatientId(patientId);
        return admissions.stream()
                .map(this::convertToDto) // Utilisez la méthode utilitaire pour un mappage complet
                .collect(Collectors.toList());
    }

    /**
     * Récupère la liste des admissions pour un patient donné et une date spécifique.
     * La date doit être fournie au format "YYYY-MM-DD".
     *
     * @param patientId L'ID du patient.
     * @param date      La date de l'admission au format "YYYY-MM-DD".
     * @return Une liste de DTOs d'admission.
     * @throws IllegalArgumentException  Si le format de la date est invalide.
     * @throws ResourceNotFoundException Si le patient n'est pas trouvé.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdmissionResponseDTO> getAdmissionsByPatientIdAndDate(Long patientId, String date) {
        // 1. Vérifier si le patient existe
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient non trouvé avec l'ID: " + patientId);
        }

        // 2. Parser la date pour obtenir une plage de temps pour la recherche
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;
        try {
            LocalDate localDate = LocalDate.parse(date);
            startOfDay = localDate.atStartOfDay();
            endOfDay = localDate.plusDays(1).atStartOfDay().minusNanos(1); // Fin de la journée
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Veuillez utiliser le format YYYY-MM-DD. Erreur: " + e.getMessage());
        }

        // 3. Rechercher les admissions dans le repository
        // Assurez-vous que votre AdmissionRepository a la méthode suivante :
        // List<Admission> findByPatientIdAndAdmissionDateBetween(Long patientId, LocalDateTime startOfDay, LocalDateTime endOfDay);
        List<Admission> admissions = admissionRepository.findByPatientIdAndAdmissionDateBetween(patientId, startOfDay, endOfDay);

        // 4. Mapper les entités Admission aux DTOs de réponse
        return admissions.stream()
                .map(this::convertToDto) // Utilisez la méthode utilitaire pour un mappage complet
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires pour le mappage (si non géré par ModelMapper partout)
    private AdmissionResponseDTO convertToDto(Admission admission) {
        AdmissionResponseDTO dto = modelMapper.map(admission, AdmissionResponseDTO.class);

        if (admission.getPatient() != null) {
            dto.setPatient(modelMapper.map(admission.getPatient(), PatientSummaryDTO.class));
        }
        if (admission.getAssignedDepartment() != null) {
            dto.setDepartment(modelMapper.map(admission.getAssignedDepartment(), DepartmentSummaryDTO.class));
        }
        return dto;
    }
}
