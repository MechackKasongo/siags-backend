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

/**
 * Classe de configuration de la sécurité pour l'application.
 * Elle définit les filtres de sécurité, l'encodage des mots de passe, et les règles d'accès.
 *
 * @EnableMethodSecurity permet l'utilisation des annotations de sécurité comme @PreAuthorize
 * sur les méthodes des contrôleurs.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Crée et retourne le filtre de token d'authentification.
     * Ce filtre sera utilisé pour valider les tokens JWT dans chaque requête.
     *
     * @return le filtre de token JWT.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Crée et retourne l'encodeur de mot de passe.
     * BCrypt est utilisé pour hacher les mots de passe de manière sécurisée.
     *
     * @return un PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crée et retourne le fournisseur d'authentification.
     * Il est configuré pour utiliser notre UserDetailsService personnalisé et notre PasswordEncoder.
     * @return un DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Crée et retourne le gestionnaire d'authentification.
     * C'est le point d'entrée pour le processus d'authentification.
     * @param authConfig La configuration d'authentification.
     * @return l'AuthenticationManager.
     * @throws Exception en cas d'erreur de configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Définit la chaîne de filtres de sécurité.
     * Configure les points d'entrée de l'API publique, la gestion des exceptions et la politique de session.
     * @param http L'objet HttpSecurity.
     * @return la SecurityFilterChain.
     * @throws Exception en cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF car nous utilisons des tokens JWT
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/v1/auth/**").permitAll() // Permet les requêtes sur les endpoints d'auth
                                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
