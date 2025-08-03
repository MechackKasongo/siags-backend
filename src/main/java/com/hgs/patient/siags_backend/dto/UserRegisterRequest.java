package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegisterRequest {

    // ... autres champs comme username, email

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Le mot de passe doit contenir au moins un chiffre, une minuscule, une majuscule et un caractère spécial.")
    private String password;

    // Getters et setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}