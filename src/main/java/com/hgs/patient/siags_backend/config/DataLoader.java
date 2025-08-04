package com.hgs.patient.siags_backend.config;

import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Liste de tous les rôles définis dans ton ERole
        ERole[] allERoles = ERole.values();

        // Pour chaque ERole, vérifie s'il existe dans la base de données
        for (ERole eRole : allERoles) {
            if (roleRepository.findByName(eRole).isEmpty()) {
                // Si le rôle n'existe pas, crée une nouvelle instance de Role et sauvegarde-la
                Role newRole = new Role(eRole);
                roleRepository.save(newRole);
                System.out.println("Rôle '" + eRole.name() + "' initialisé et ajouté à la base de données.");
            } else {
                System.out.println("Rôle '" + eRole.name() + "' existe déjà dans la base de données.");
            }
        }
    }
}