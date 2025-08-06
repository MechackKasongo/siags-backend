package com.hgs.patient.siags_backend.service.imp;

import com.hgs.patient.siags_backend.dto.RoleResponseDTO;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import com.hgs.patient.siags_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponseDTO)
                .collect(Collectors.toList());
    }

    private RoleResponseDTO mapToRoleResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(role.getPermissions().stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet()));
        return dto;
    }
}