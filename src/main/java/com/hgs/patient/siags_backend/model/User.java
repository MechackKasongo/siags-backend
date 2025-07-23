package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor // Gardez-le si d'autres parties du code en dépendent ou pour des tests
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // Initialisé ici par défaut pour éviter les NPE

    private String nomComplet;
    private String email;

    // NOUVEAU CONSTRUCTEUR POUR LA CRÉATION D'UTILISATEUR VIA INSCRIPTION
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>(); // S'assure que le Set de rôles est initialisé
        this.nomComplet = null; // Par défaut à null, à définir après si besoin
    }
}