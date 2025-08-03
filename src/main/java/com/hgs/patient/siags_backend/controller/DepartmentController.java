package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.DepartmentRequest;
import com.hgs.patient.siags_backend.dto.DepartmentResponseDTO;
import com.hgs.patient.siags_backend.service.DepartmentService;
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

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // --- Créer un nouveau département ---
    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_WRITE')")
    // Seul un ADMIN peut créer un département (ou quiconque a cette permission)
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponseDTO newDepartment = departmentService.createDepartment(departmentRequest);
        return new ResponseEntity<>(newDepartment, HttpStatus.CREATED);
    }

    // --- Récupérer un département par ID ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')") // Ces rôles peuvent consulter
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    // --- Récupérer tous les départements (avec ou sans pagination) ---
    @GetMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')") // Ces rôles peuvent consulter
    public ResponseEntity<?> getAllDepartments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {

        if (page != null && size != null) {
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.isEmpty()) {
                sort = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DepartmentResponseDTO> departmentsPage = departmentService.getAllDepartmentsPaginated(pageable);
            return ResponseEntity.ok(departmentsPage);
        } else {
            List<DepartmentResponseDTO> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        }
    }

    // --- Mettre à jour un département existant ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_WRITE')") // Seul un ADMIN peut mettre à jour un département
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponseDTO updatedDepartment = departmentService.updateDepartment(id, departmentRequest);
        return ResponseEntity.ok(updatedDepartment);
    }

    // --- Supprimer un département ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE')") // Seul un ADMIN peut supprimer un département
    public ResponseEntity<HttpStatus> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}