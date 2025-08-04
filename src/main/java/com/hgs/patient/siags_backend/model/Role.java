package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor // Génère un constructeur avec tous les champs
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Stocke le nom de l'énumération (ROLE_ADMIN, ROLE_MEDECIN...)
    @Column(length = 20)
    private ERole name;

    // Nouvelle relation Many-to-Many avec Permission
    @ManyToMany(fetch = FetchType.EAGER)
    // EAGER pour que les permissions soient chargées avec le rôle (souvent nécessaire pour Spring Security)
    @JoinTable(name = "role_permissions", // Nom de la table de jointure
            joinColumns = @JoinColumn(name = "role_id"), // Colonne pour l'ID du rôle dans la table de jointure
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    // Colonne pour l'ID de la permission dans la table de jointure
    private Set<Permission> permissions = new HashSet<>(); // Initialiser pour éviter les NullPointerExceptions

    // Constructeur sans les relations, si AllArgsConstructor est utilisé, Lombok en génère un par défaut
    public Role(ERole name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
