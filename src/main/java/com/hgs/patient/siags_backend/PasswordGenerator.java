package com.hgs.patient.siags_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Crée une instance de BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hache le mot de passe "password"
        String hashedPassword = encoder.encode("password");

        // Affiche le mot de passe haché dans la console
        System.out.println("Le mot de passe haché pour 'password' est : " + hashedPassword);
    }
}
