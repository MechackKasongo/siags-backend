package com.hgs.patient.siags_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(max = 100, message = "Le nom du département ne doit pas dépasser 100 caractères")
    private String name;

    @Size(max = 500, message = "La description du département ne doit pas dépasser 500 caractères")
    private String description;

}