package com.hgs.patient.siags_backend.security.jwt;


import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import com.hgs.patient.siags_backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;


import java.security.Key;

import java.util.Date;


@Component

public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


    @Value("${hgs.app.jwtSecret}") // Clé secrète définie dans l'application.properties

    private String jwtSecret;


    @Value("${hgs.app.jwtExpirationMs}") // Durée de validité du token

    private int jwtExpirationMs;


// Génère le token JWT

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

        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

    }


// Récupère le nom d'utilisateur depuis le token

    public String getUserNameFromJwtToken(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(key())

                .build()

                .parseClaimsJws(token)

                .getBody()

                .getSubject();

    }


// Valide le token JWT

    public boolean validateJwtToken(String authToken) {

        try {

            Jwts.parserBuilder()

                    .setSigningKey(key())

                    .build()

                    .parseClaimsJws(authToken);

            return true;

        } catch (MalformedJwtException e) {

            logger.error("Invalid JWT token: {}", e.getMessage());

        } catch (ExpiredJwtException e) {

            logger.error("JWT token is expired: {}", e.getMessage());

        } catch (UnsupportedJwtException e) {

            logger.error("JWT token is unsupported: {}", e.getMessage());

        } catch (IllegalArgumentException e) {

            logger.error("JWT claims string is empty: {}", e.getMessage());

        }

        return false;

    }

}
