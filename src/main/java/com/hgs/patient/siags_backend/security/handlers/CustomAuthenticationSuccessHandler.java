package com.hgs.patient.siags_backend.security.handlers;

import com.hgs.patient.siags_backend.model.User;
import com.hgs.patient.siags_backend.repository.UserRepository;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import com.hgs.patient.siags_backend.security.services.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (user != null) {
            userDetailsService.resetFailedAttempts(user);
        }
        // Continuer la chaîne de filtre ou retourner une réponse réussie
        // Par exemple, rediriger ou retourner un token JWT
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Connexion réussie!\"}");
        // Si vous utilisez JWT, vous généreriez et retourneriez le token ici.
    }
}
