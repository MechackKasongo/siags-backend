package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.DepartmentRequest;
import com.hgs.patient.siags_backend.dto.DepartmentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequest departmentRequest);

    DepartmentResponseDTO getDepartmentById(Long id);

    List<DepartmentResponseDTO> getAllDepartments();

    Page<DepartmentResponseDTO> getAllDepartmentsPaginated(Pageable pageable);

    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequest departmentRequest);

    void deleteDepartment(Long id);
}
