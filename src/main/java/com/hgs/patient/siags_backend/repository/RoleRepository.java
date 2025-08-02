package com.hgs.patient.siags_backend.repository;


import com.hgs.patient.siags_backend.model.Role;

import com.hgs.patient.siags_backend.model.ERole;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

}