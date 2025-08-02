package com.hgs.patient.siags_backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.convention.MatchingStrategies;

@Configuration // Indique à Spring que cette classe contient des définitions de beans
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configure ModelMapper pour un mappage strict afin d'éviter les ambiguïtés
        // et pour que les noms de propriétés doivent correspondre exactement.
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Crée un mappage explicite pour AdmissionRequestDTO vers Admission
        // et ignore les champs patientId et departmentId, car ils sont gérés manuellement.
        modelMapper.createTypeMap(com.hgs.patient.siags_backend.dto.AdmissionRequestDTO.class, com.hgs.patient.siags_backend.model.Admission.class)
                .addMappings(mapper -> {
                    mapper.skip(com.hgs.patient.siags_backend.model.Admission::setId); // Ignore le mappage de l'ID de l'admission
                    mapper.skip(com.hgs.patient.siags_backend.model.Admission::setPatient); // Ignore le mappage du patient (géré manuellement)
                    mapper.skip(com.hgs.patient.siags_backend.model.Admission::setAssignedDepartment); // Ignore le mappage du département (géré manuellement)
                });
        // Note: Si vous avez d'autres champs dans AdmissionRequestDTO qui pourraient créer des conflits
        // ou que vous gérez manuellement, vous devrez les ajouter ici avec .skip().
        return modelMapper;
    }
}