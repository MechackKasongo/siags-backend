package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MedicalRecord medicalRecord;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String firstName;

    @Size(max = 100, message = "Post-name must not exceed 100 characters")
    @Column(length = 100)
    private String postName; // Renamed from postNom

    @Size(max = 100, message = "Profession must not exceed 100 characters")
    @Column(length = 100)
    private String profession;

    @Size(max = 100, message = "Employer must not exceed 100 characters")
    @Column(length = 100)
    private String employer; // Renamed from employeur

    @Size(max = 50, message = "Patient number must not exceed 50 characters")
    @Column(length = 50, unique = true)
    private String patientNumber; // Renamed from matricule

    @Size(max = 100, message = "Spouse's name must not exceed 100 characters")
    @Column(name = "spouse_name", length = 100)
    private String spouseName; // Renamed from nomConjoint

    @Size(max = 100, message = "Spouse's profession must not exceed 100 characters")
    @Column(name = "spouse_profession", length = 100)
    private String spouseProfession; // Renamed from professionConjoint

    @Size(max = 10, message = "Gender must not exceed 10 characters")
    @Column(length = 10)
    private String gender;

    @PastOrPresent(message = "Birth date cannot be in the future")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 150, message = "Address must not exceed 150 characters")
    @Column(length = 150)
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(length = 100)
    private String city;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "record_number", unique = true, length = 50)
    private String recordNumber;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(length = 100)
    private String email;

    @Size(max = 15, message = "Blood type must not exceed 15 characters")
    @Column(name = "blood_type", length = 15)
    private String bloodType;

    @Column(name = "known_illnesses", columnDefinition = "TEXT")
    private String knownIllnesses;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (recordNumber == null) {
            String year = String.valueOf(LocalDateTime.now().getYear());
            String month = String.format("%02d", LocalDateTime.now().getMonthValue());
            this.recordNumber = "DOS-" + year + "-" + month + "-" + generateUniqueIdSuffix();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateUniqueIdSuffix() {
        return String.valueOf(System.currentTimeMillis());
    }
}