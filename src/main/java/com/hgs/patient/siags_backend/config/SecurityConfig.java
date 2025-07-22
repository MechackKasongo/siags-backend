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

@Configuration // Indique que c'est une classe de configuration Spring
@EnableMethodSecurity // Permet d'utiliser des annotations de sécurité comme @PreAuthorize
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Configure le fournisseur d'authentification (comment Spring trouvera et validera les utilisateurs)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Utilise notre service pour charger les utilisateurs
        authProvider.setPasswordEncoder(passwordEncoder());    // Utilise notre encodeur de mot de passe
        return authProvider;
    }

    // Expose l'AuthenticationManager pour l'utiliser dans le contrôleur d'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Définit l'encodeur de mot de passe (BCrypt est recommandé)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure les règles de sécurité HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF pour les API REST (JWT est stateless)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Gère les erreurs d'authentification
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de session côté serveur (JWT est stateless)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Permet l'accès non authentifié aux endpoints d'authentification
                                .requestMatchers("/api/patients/**").authenticated() // Nécessite une authentification pour les patients
                                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                );

        http.authenticationProvider(authenticationProvider()); // Enregistre notre fournisseur d'authentification

        // Ajoute le filtre JWT avant le filtre d'authentification par nom d'utilisateur/mot de passe de Spring
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}