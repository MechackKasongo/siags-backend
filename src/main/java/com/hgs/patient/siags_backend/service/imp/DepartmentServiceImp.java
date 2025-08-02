package com.hgs.patient.siags_backend.service.impl;

import com.hgs.patient.siags_backend.dto.DepartmentRequest;
import com.hgs.patient.siags_backend.dto.DepartmentResponseDTO;
import com.hgs.patient.siags_backend.model.Department;
import com.hgs.patient.siags_backend.repository.DepartmentRepository;
import com.hgs.patient.siags_backend.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImp implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper; // Pour la conversion Entité <-> DTO

    @Autowired
    public DepartmentServiceImp(DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequest departmentRequest) {

        // Vérifier si un département avec le même nom existe déjà

        if (departmentRepository.findByName(departmentRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("Un département avec ce nom existe déjà.");
        }

        Department department = modelMapper.map(departmentRequest, Department.class);
        Department savedDepartment = departmentRepository.save(department);
        return modelMapper.map(savedDepartment, DepartmentResponseDTO.class);
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Département non trouvé avec l'ID: " + id));
        return modelMapper.map(department, DepartmentResponseDTO.class);
    }

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> modelMapper.map(department, DepartmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<DepartmentResponseDTO> getAllDepartmentsPaginated(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(department -> modelMapper.map(department, DepartmentResponseDTO.class));
    }

    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequest departmentRequest) {
        return departmentRepository.findById(id).map(existingDepartment -> {

            // Vérifier si le nom mis à jour existe déjà pour un autre département
            if (departmentRepository.findByName(departmentRequest.getName()).isPresent() &&
                    !departmentRepository.findByName(departmentRequest.getName()).get().getId().equals(id)) {
                throw new IllegalArgumentException("Un autre département avec ce nom existe déjà.");
            }

            existingDepartment.setName(departmentRequest.getName());
            existingDepartment.setDescription(departmentRequest.getDescription());
            Department updatedDepartment = departmentRepository.save(existingDepartment);
            return modelMapper.map(updatedDepartment, DepartmentResponseDTO.class);
        }).orElseThrow(() -> new EntityNotFoundException("Département non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Département non trouvé avec l'ID: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
