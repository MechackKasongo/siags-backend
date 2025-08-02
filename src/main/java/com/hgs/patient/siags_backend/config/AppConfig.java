package com.hgs.patient.siags_backend.config;


import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestTemplate;


@Configuration // Indique à Spring que cette classe contient des définitions de Beans

public class AppConfig {


    @Bean // Indique à Spring de créer un "bean" de type RestTemplate

    public RestTemplate restTemplate() {

        return new RestTemplate();

    }

}