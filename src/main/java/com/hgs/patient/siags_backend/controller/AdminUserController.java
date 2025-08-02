package com.hgs.patient.siags_backend.controller;


import com.hgs.patient.siags_backend.dto.UserCreateRequest;

import com.hgs.patient.siags_backend.dto.UserResponseDTO;

import com.hgs.patient.siags_backend.dto.UserUpdateRequest;

import com.hgs.patient.siags_backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;


import java.util.List;


//@CrossOrigin(origins = "*", maxAge = 3600) // Permet les requêtes Cross-Origin

@RestController // Indique que c'est un contrôleur REST

@RequestMapping("/api/admin/users") // Toutes les routes de ce contrôleur commenceront par /api/admin/users

@PreAuthorize("hasRole('ADMIN')") // Toutes les méthodes de ce contrôleur nécessitent le rôle ADMIN

public class AdminUserController {


    private final UserService userService;


    @Autowired

    public AdminUserController(UserService userService) {

        this.userService = userService;

    }


// --- Créer un nouvel utilisateur par l'Admin ---

    @PostMapping

    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {

        UserResponseDTO newUser = userService.createUser(userCreateRequest);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);

    }


// --- Récupérer un utilisateur par ID ---

    @GetMapping("/{id}")

    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(user);

    }


// --- Récupérer un utilisateur par Username ---

    @GetMapping("/username/{username}")

    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {

        UserResponseDTO user = userService.getUserByUsername(username);

        return ResponseEntity.ok(user);

    }


// --- Récupérer tous les utilisateurs (avec ou sans pagination) ---

    @GetMapping

    public ResponseEntity<?> getAllUsers(

            @RequestParam(required = false) Integer page,

            @RequestParam(required = false) Integer size,

            @RequestParam(required = false) String sortBy,

            @RequestParam(required = false) String sortDir) {


        if (page != null && size != null) {

// Requête paginée

            Sort sort = Sort.unsorted();

            if (sortBy != null && !sortBy.isEmpty()) {

                sort = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            }

            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserResponseDTO> usersPage = userService.getAllUsersPaginated(pageable);

            return ResponseEntity.ok(usersPage);

        } else {

// Requête sans pagination, retourne tout

            List<UserResponseDTO> users = userService.getAllUsers();

            return ResponseEntity.ok(users);

        }

    }


// --- Mettre à jour un utilisateur existant ---

    @PutMapping("/{id}")

    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {

        UserResponseDTO updatedUser = userService.updateUser(id, userUpdateRequest);

        return ResponseEntity.ok(updatedUser);

    }


// --- Supprimer un utilisateur ---

    @DeleteMapping("/{id}")

    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Réponse 204 No Content pour une suppression réussie

    }

}
