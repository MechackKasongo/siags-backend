package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email); // Vérifie si un utilisateur existe avec cet email

    Optional<User> findByEmail(String email); // Cette ligne pourrait aussi être utile si pas déjà présente

}