package com.hgs.patient.siags_backend.security.jwt;

import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Classe utilitaire pour la génération, la validation et l'extraction d'informations des tokens JWT.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${hgs.siags.jwtSecret}")
    private String jwtSecret;

    @Value("${hgs.siags.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Génère un token JWT à partir des informations d'authentification.
     *
     * @param authentication L'objet d'authentification de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        // La clé doit être décodée en Base64.
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Récupère le nom d'utilisateur à partir du token JWT.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valide la signature du token JWT.
     * @param authToken Le token JWT.
     * @return true si le token est valide, false sinon.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT invalide : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("La chaîne de revendications JWT est vide : {}", e.getMessage());
        }
        return false;
    }
}
