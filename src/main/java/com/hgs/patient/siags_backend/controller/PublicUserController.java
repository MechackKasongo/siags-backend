package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users") // Nouveau chemin de base
public class PublicUserController {

    private final UserService userService;

    @Autowired
    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDECIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(@RequestParam(required = false) String searchTerm) {
        List<UserResponseDTO> users = userService.getAllUsers(searchTerm);
        return ResponseEntity.ok(users);
    }
}