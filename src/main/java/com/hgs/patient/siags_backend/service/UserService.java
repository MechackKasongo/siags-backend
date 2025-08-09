package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.UserCreateRequest;
import com.hgs.patient.siags_backend.dto.UserResponseDTO;
import com.hgs.patient.siags_backend.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserCreateRequest userCreateRequest);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);


    List<UserResponseDTO> getAllUsers(String searchTerm);

    Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable, String searchTerm);

    UserResponseDTO updateUser(Long id, UserUpdateRequest userUpdateRequest);
    void deleteUser(Long id);
}
