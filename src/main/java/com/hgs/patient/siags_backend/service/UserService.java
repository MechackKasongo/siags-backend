package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.UserCreateRequest;
import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
// import java.util.Optional;

public interface UserService {
    UserResponseDTO createUser(UserCreateRequest userCreateRequest);

    // cette méthode renvoie UserResponseDTO
    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);
    List<UserResponseDTO> getAllUsers();
    Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable);

    // Optional<UserResponseDTO> getUserByEmail(String email); // <-- S'assure que cela renvoie UserResponseDTO
    UserResponseDTO updateUser(Long id, UserUpdateRequest userUpdateRequest);
    void deleteUser(Long id);
}