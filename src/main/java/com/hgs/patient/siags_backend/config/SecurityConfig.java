package com.hgs.patient.siags_backend.config;

import com.hgs.patient.siags_backend.security.jwt.AuthEntryPointJwt;
import com.hgs.patient.siags_backend.security.jwt.AuthTokenFilter;
import com.hgs.patient.siags_backend.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Permet @PreAuthorize et @PostAuthorize
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Injection de notre service UserDetails personnalisé

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Injection du gestionnaire d'exceptions d'authentification

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(); // Crée une instance de votre filtre JWT
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF pour les API REST (JWT est stateless)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Gère les erreurs d'authentification (401)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de session côté serveur (JWT est stateless)
                .authorizeHttpRequests(auth ->
                        // Permet l'accès non authentifié aux endpoints d'authentification (signup, signin)
                        auth.requestMatchers("/api/v1/auth/**").permitAll()
                                // Exemple: Les endpoints pour les patients peuvent nécessiter une authentification
                                // Si vous voulez que /api/patients/** soit accessible sans authentification pour certains cas (ex: recherche publique),
                                // vous devrez ajuster cette ligne ou ajouter d'autres règles.
                                // Pour l'instant, toutes les requêtes vers /api/patients/** nécessitent d'être authentifiées.
                                .requestMatchers("/api/patients/**").authenticated() // Ajoutez cette ligne si elle était manquante
                                // Toutes les autres requêtes nécessitent une authentification
                                .anyRequest().authenticated()
                );

        // Enregistre notre fournisseur d'authentification personnalisé
        http.authenticationProvider(authenticationProvider());

        // Ajoute notre filtre JWT personnalisé avant le filtre d'authentification par nom d'utilisateur/mot de passe de Spring
        // Cela assure que le token JWT est validé en premier
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}