package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.RoleResponseDTO;

import java.util.List;

/**
 * Interface pour le service de gestion des rôles.
 * Fournit des méthodes pour interagir avec les données des rôles.
 */
public interface RoleService {

    /**
     * Récupère la liste de tous les rôles disponibles.
     *
     * @return Une liste de RoleResponseDTO.
     */
    List<RoleResponseDTO> getAllRoles();
}