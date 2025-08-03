package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "permissions") // Nom de la table dans la base de données
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String name; // Ex: PATIENT_READ, PATIENT_WRITE, USER_CREATE, etc.

    @Column(length = 255)
    private String description; // Description optionnelle de la permission

    // Relation Many-to-Many avec Role (Optionnel si vous gérez la relation uniquement depuis Role)
    // Inverse de la relation dans Role, mappedBy fait référence au champ "permissions" dans la classe Role
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Optionnel : Méthodes equals et hashCode pour une meilleure gestion des entités
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) || Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}