package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Méthode pour trouver une permission par son nom (qui doit être unique)
    Optional<Permission> findByName(String name);

    // Méthode pour vérifier si une permission existe par son nom
    boolean existsByName(String name);
}