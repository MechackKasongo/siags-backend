package com.hgs.patient.siags_backend.dto;

import com.hgs.patient.siags_backend.model.ERole;

import java.util.Set;

public class RoleResponseDTO {
    private Long id;
    private ERole name; // La classe ERole est maintenant reconnue
    private Set<String> permissions;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}