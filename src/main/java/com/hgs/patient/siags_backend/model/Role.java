package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Stocke le nom de l'énumération (ROLE_ADMIN, ROLE_MEDECIN...)
    @Column(length = 20)
    private ERole name;
}
