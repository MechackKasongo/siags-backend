package com.hgs.patient.siags_backend.security.services;


import com.hgs.patient.siags_backend.model.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;

import java.util.List;

import java.util.Objects;

import java.util.stream.Collectors;


// Cette classe enveloppe notre entité User et fournit les infos attendues par Spring Security

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;


    private Long id;


    private String username;


    private String email; // Si vous avez un champ email dans User


    @JsonIgnore // Ne pas exposer le mot de passe dans les réponses JSON

    private String password;


    private Collection<? extends GrantedAuthority> authorities;


    public UserDetailsImpl(Long id, String username, String password, String email,

                           Collection<? extends GrantedAuthority> authorities) {

        this.id = id;

        this.username = username;

        this.password = password;

        this.email = email;

        this.authorities = authorities;

    }


// Construit un objet UserDetailsImpl à partir de notre entité User

    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities = user.getRoles().stream()

                .map(role -> new SimpleGrantedAuthority(role.getName().name()))

                .collect(Collectors.toList());


        return new UserDetailsImpl(

                user.getId(),

                user.getUsername(),

                user.getPassword(),

                user.getEmail(), // Si vous l'avez

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