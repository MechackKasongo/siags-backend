package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users") // Renommer la table pour éviter le conflit avec 'user' qui est un mot-clé SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Nom d'utilisateur pour la connexion

    @Column(nullable = false)
    private String password; // Mot de passe encodé

    // Vous pouvez définir des rôles (ADMIN, MEDECIN, RECEPTIONNISTE, etc.)
    @ManyToMany(fetch = FetchType.EAGER) // Charger les rôles avec l'utilisateur
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Autres informations de l'utilisateur si nécessaire (nom, prénom, email...)
    private String nomComplet;
    private String email;
}