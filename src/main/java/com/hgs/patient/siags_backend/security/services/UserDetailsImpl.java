package com.hgs.patient.siags_backend.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hgs.patient.siags_backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

// Cette classe enveloppe notre entité User et fournit les infos attendues par Spring Security
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String email; // Si vous avez un champ email dans User

    @JsonIgnore // Ne pas exposer le mot de passe dans les réponses JSON
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password, // Changé l'ordre pour email/password
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Construit un objet UserDetailsImpl à partir de notre entité User
    public static UserDetailsImpl build(User user) {
        // Crée une liste mutables pour ajouter les autorités
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();

        // 1. Ajoute les rôles comme autorités (ex: ROLE_ADMIN, ROLE_RECEPTIONNISTE)
        user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .forEach(authorities::add);

        // 2. Ajoute les permissions de chaque rôle comme autorités
        // Parcourt tous les rôles de l'utilisateur
        user.getRoles().stream()
                // Pour chaque rôle, récupère son ensemble de permissions
                .flatMap(role -> role.getPermissions().stream())
                // Convertit chaque permission en SimpleGrantedAuthority en utilisant son nom
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                // Ajoute la permission à la liste principale des autorités
                .forEach(authorities::add);

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Vous pouvez ajuster ces méthodes si vous gérez des comptes expirés/verrouillés/désactivés
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}