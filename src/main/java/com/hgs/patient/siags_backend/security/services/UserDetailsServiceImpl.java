package com.hgs.patient.siags_backend.security.services;

import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final long LOCK_TIME_DURATION_MINUTES = 15; // Durée du verrouillage en minutes
    private final int MAX_FAILED_ATTEMPTS = 5; // Nombre max de tentatives échouées

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // Nécessaire si vous modifiez l'utilisateur après le chargement
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom : " + username));

        // Vérifier si le compte est verrouillé et si le temps de verrouillage est écoulé
        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null && Duration.between(user.getLockTime(), LocalDateTime.now()).toMinutes() > LOCK_TIME_DURATION_MINUTES) {
                user.setAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user); // Déverrouiller le compte
            } else {
                throw new LockedException("Votre compte est verrouillé en raison de trop de tentatives de connexion échouées. Veuillez réessayer plus tard.");
            }
        }

        // Retourner votre implémentation de UserDetails (par exemple, UserDetailsImpl)
        return UserDetailsImpl.build(user);
    }

    // Méthodes pour gérer les tentatives échouées et réussies
    public void increaseFailedAttempts(User user) {
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        if (user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockTime(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        if (user.getFailedAttempt() > 0) {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        // Gère le déverrouillage automatique si le temps est écoulé
        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null && Duration.between(user.getLockTime(), LocalDateTime.now()).toMinutes() > LOCK_TIME_DURATION_MINUTES) {
                user.setAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user);
                return false; // Le compte est maintenant déverrouillé
            }
            return true; // Le compte est toujours verrouillé
        }
        return false;
    }
}