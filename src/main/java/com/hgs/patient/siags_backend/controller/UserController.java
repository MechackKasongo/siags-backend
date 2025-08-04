// src/main/java/com/hgs/patient/siags_backend/controller/UserController.java

package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.dto.UserUpdateRequest;
import com.hgs.patient.siags_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // NOUVEAU ENDPOINT : Récupérer un utilisateur par ID (utile pour pré-remplir le formulaire d'édition)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }


    // NOUVEAU ENDPOINT : Mettre à jour un utilisateur
    @PutMapping("/{id}") // Mappe à PUT /api/v1/users/{id}
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userUpdateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Utilisateur non trouvé
        } catch (IllegalArgumentException e) {
            // Pour les erreurs comme "email déjà pris"
            return ResponseEntity.badRequest().body(null); // Tu pourrais renvoyer e.getMessage() si tu veux le texte de l'erreur
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}