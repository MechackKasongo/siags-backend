package com.hgs.patient.siags_backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Indique à Spring que cette classe contient des définitions de beans
public class ApplicationConfig {

    @Bean // Indique à Spring de créer et de gérer cette instance comme un bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}