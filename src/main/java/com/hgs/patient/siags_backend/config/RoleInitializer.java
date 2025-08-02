package com.hgs.patient.siags_backend.config;


import com.hgs.patient.siags_backend.model.ERole;

import com.hgs.patient.siags_backend.model.Role;

import com.hgs.patient.siags_backend.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;


import java.util.Arrays;


@Component // Indique à Spring de gérer ce bean

public class RoleInitializer implements CommandLineRunner {


    @Autowired

    RoleRepository roleRepository;


    @Override

    public void run(String... args) throws Exception {

// Initialiser les rôles si ils n'existent pas déjà

        Arrays.stream(ERole.values()).forEach(eRole -> {

            if (!roleRepository.findByName(eRole).isPresent()) {

                Role role = new Role();

                role.setName(eRole);

                roleRepository.save(role);

                System.out.println("Rôle " + eRole.name() + " créé dans la base de données.");

            }

        });

    }

}
