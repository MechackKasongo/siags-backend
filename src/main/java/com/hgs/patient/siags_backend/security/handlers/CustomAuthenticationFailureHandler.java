package com.hgs.patient.siags_backend.security.handlers;

import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.services.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository; // Pour accéder à l'utilisateur
    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Pour appeler increaseFailedAttempts

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username"); // Ou le nom du paramètre pour votre username

        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                if (exception instanceof BadCredentialsException) {
                    userDetailsService.increaseFailedAttempts(user);
                } else if (exception instanceof LockedException) {
                    // Le compte est déjà verrouillé, la logique est gérée dans loadUserByUsername
                }
            }
        }
        // Rediriger ou retourner une erreur JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"message\": \"" + exception.getMessage() + "\"}");
    }
}
