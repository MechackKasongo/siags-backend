package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.dto.ConsultationCountByDoctorDTO;
import com.hgs.patient.siags_backend.dto.ConsultationRequest;
import com.hgs.patient.siags_backend.dto.ConsultationResponseDTO;
import com.hgs.patient.siags_backend.service.ConsultationService;
import com.hgs.patient.siags_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final UserService userService;

    @Autowired
    public ConsultationController(ConsultationService consultationService, UserService userService) {
        this.consultationService = consultationService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN', 'ROLE_INFIRMIER')")
    public ResponseEntity<ConsultationResponseDTO> createConsultation(@Valid @RequestBody ConsultationRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUsername = authentication.getName();

        Optional<Long> currentUserIdOptional = userService.getUserIdByUsername(currentUsername);

        if (currentUserIdOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        request.setDoctorId(currentUserIdOptional.get());

        ConsultationResponseDTO createdConsultation = consultationService.createConsultation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConsultation);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN', 'ROLE_INFIRMIER')")
    public ResponseEntity<ConsultationResponseDTO> getConsultationById(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN', 'ROLE_INFIRMIER', 'ROLE_RECEPTIONNISTE')")
    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN', 'ROLE_INFIRMIER')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationsByPatient(patientId));
    }

    @GetMapping("/medecin/{doctorId}")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(consultationService.getConsultationsByDoctor(doctorId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN', 'ROLE_INFIRMIER')")
    public ResponseEntity<ConsultationResponseDTO> updateConsultation(@PathVariable Long id, @Valid @RequestBody ConsultationRequest request) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDateRange(
            @RequestParam @jakarta.validation.constraints.NotNull LocalDateTime startDate,
            @RequestParam @jakarta.validation.constraints.NotNull LocalDateTime endDate) {
        return ResponseEntity.ok(consultationService.getConsultationsByDateRange(startDate, endDate));
    }

    @GetMapping("/patient/search")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatientLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(consultationService.getConsultationsByPatientLastName(lastName));
    }

    @GetMapping("/diagnosis/search")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByDiagnosisKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(consultationService.getConsultationsByDiagnosisKeyword(keyword));
    }

    @GetMapping("/count-by-doctor")
    @PreAuthorize("hasAnyRole('ROLE_MEDECIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationCountByDoctorDTO>> countConsultationsByDoctor() {
        return ResponseEntity.ok(consultationService.countConsultationsByDoctor());
    }
}