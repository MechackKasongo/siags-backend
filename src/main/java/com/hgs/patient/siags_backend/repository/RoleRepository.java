package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Cette méthode est utilisée par RoleInitializer pour vérifier si un rôle existe sans erreur,
    // même s'il y a des doublons.
    long countByName(ERole name);

    // Cette méthode est utilisée par UserServiceImp pour récupérer un rôle par son nom.
    // Elle renvoie un Optional<Role> et est la plus adaptée pour une recherche unique.
    Optional<Role> findByName(ERole name);
}
