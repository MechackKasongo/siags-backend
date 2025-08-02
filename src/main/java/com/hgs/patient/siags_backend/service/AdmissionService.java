package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.AdmissionRequestDTO;
import com.hgs.patient.siags_backend.dto.AdmissionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime; // Import si d'autres méthodes utilisent LocalDateTime directement

public interface AdmissionService {

    /**
     * Crée une nouvelle admission pour un patient.
     *
     * @param admissionRequestDTO Les données de l'admission à créer.
     * @return Le DTO de réponse de l'admission créée.
     */
    AdmissionResponseDTO createAdmission(AdmissionRequestDTO admissionRequestDTO);

    /**
     * Récupère une admission par son ID.
     *
     * @param id L'ID de l'admission.
     * @return Le DTO de réponse de l'admission.
     * @throws com.hgs.patient.siags_backend.exception.ResourceNotFoundException si l'admission n'est pas trouvée.
     */
    AdmissionResponseDTO getAdmissionById(Long id);

    /**
     * Récupère toutes les admissions.
     *
     * @return Une liste de DTOs de réponse des admissions.
     */
    List<AdmissionResponseDTO> getAllAdmissions();

    /**
     * Récupère toutes les admissions avec pagination.
     *
     * @param pageable Les informations de pagination.
     * @return Une page de DTOs de réponse des admissions.
     */
    Page<AdmissionResponseDTO> getAllAdmissionsPaginated(Pageable pageable);

    /**
     * Met à jour une admission existante.
     *
     * @param id                  L'ID de l'admission à mettre à jour.
     * @param admissionRequestDTO Les nouvelles données de l'admission.
     * @return Le DTO de réponse de l'admission mise à jour.
     * @throws com.hgs.patient.siags_backend.exception.ResourceNotFoundException si l'admission n'est pas trouvée.
     */
    AdmissionResponseDTO updateAdmission(Long id, AdmissionRequestDTO admissionRequestDTO);

    /**
     * Supprime une admission par son ID.
     *
     * @param id L'ID de l'admission à supprimer.
     * @throws com.hgs.patient.siags_backend.exception.ResourceNotFoundException si l'admission n'est pas trouvée.
     */
    void deleteAdmission(Long id);

    /**
     * Récupère toutes les admissions pour un patient donné.
     *
     * @param patientId L'ID du patient.
     * @return Une liste de DTOs de réponse des admissions du patient.
     */
    List<AdmissionResponseDTO> getAdmissionsByPatientId(Long patientId);

    /**
     * Récupère toutes les admissions pour un patient donné à une date spécifique.
     *
     * @param patientId L'ID du patient.
     * @param date      La date de l'admission au format "YYYY-MM-DD".
     * @return Une liste de DTOs de réponse des admissions du patient pour la date spécifiée.
     */
    List<AdmissionResponseDTO> getAdmissionsByPatientIdAndDate(Long patientId, String date); // <--- NOUVELLE DÉCLARATION
}