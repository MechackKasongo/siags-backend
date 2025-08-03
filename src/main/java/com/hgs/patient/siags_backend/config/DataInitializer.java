package com.hgs.patient.siags_backend.config;

import com.hgs.patient.siags_backend.model.ERole;
import com.hgs.patient.siags_backend.model.Permission;
import com.hgs.patient.siags_backend.model.Role;
import com.hgs.patient.siags_backend.repository.PermissionRepository;
import com.hgs.patient.siags_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cette classe s'exécute au démarrage de l'application et initialise les rôles et permissions
 * de base dans la base de données si elles n'existent pas déjà.
 */
@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initializePermissionsAndRoles(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository) {
        return args -> {
            // ÉTAPE 1: Récupérer toutes les permissions existantes pour éviter les requêtes N+1
            // Nous utilisons un Map pour un accès rapide par le nom de la permission
            Map<String, Permission> existingPermissionsMap = permissionRepository.findAll().stream()
                    .collect(Collectors.toMap(Permission::getName, Function.identity()));

            Set<Permission> allPermissions = new HashSet<>();

            // Définition de toutes les permissions nécessaires
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "PATIENT_READ", "Permet de lire les informations des patients."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "PATIENT_WRITE", "Permet de créer et mettre à jour les informations des patients."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "PATIENT_DELETE", "Permet de supprimer les patients."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DAILY_RECORD_READ", "Permet de lire les enregistrements quotidiens des patients."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DAILY_RECORD_WRITE", "Permet de créer et mettre à jour les enregistrements quotidiens des patients."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DAILY_RECORD_DELETE", "Permet de supprimer les enregistrements quotidiens."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "USER_READ", "Permet de lire les informations des utilisateurs."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "USER_WRITE", "Permet de créer et mettre à jour les informations des utilisateurs."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "USER_DELETE", "Permet de supprimer les utilisateurs."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "USER_ASSIGN_ROLE", "Permet d'assigner des rôles aux utilisateurs."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "CONSULTATION_READ", "Permet de lire les consultations."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "CONSULTATION_WRITE", "Permet de créer et mettre à jour les consultations."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "CONSULTATION_DELETE", "Permet de supprimer les consultations."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "ADMISSION_READ", "Permet de lire les admissions."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "ADMISSION_WRITE", "Permet de créer et mettre à jour les admissions."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "ADMISSION_DISCHARGE", "Permet de gérer la sortie des patients."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DEPARTMENT_READ", "Permet de lire les informations des départements."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DEPARTMENT_WRITE", "Permet de créer et mettre à jour les informations des départements."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "DEPARTMENT_DELETE", "Permet de supprimer les départements."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "MEDICAL_RECORD_READ", "Permet de lire les dossiers médicaux."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "MEDICAL_RECORD_WRITE", "Permet de créer et mettre à jour les dossiers médicaux."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "MEDICAL_RECORD_DELETE", "Permet de supprimer les dossiers médicaux."));

            // *** NOUVELLE PERMISSION POUR L'AUDIT ***
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "AUDIT_READ", "Permet de lire les journaux d'audit."));

            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "REPORT_READ_PATIENT", "Permet de lire les rapports liés aux patients."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "REPORT_READ_ADMISSION", "Permet de lire les rapports liés aux admissions."));
            allPermissions.add(createOrGetPermission(permissionRepository, existingPermissionsMap, "REPORT_READ_CONSULTATION", "Permet de lire les rapports liés aux consultations."));

            System.out.println("Permissions initialisées ou récupérées.");

            // ÉTAPE 2: Créer ou récupérer tous les rôles en une seule requête
            Map<ERole, Role> existingRolesMap = Arrays.stream(ERole.values())
                    .map(roleName -> roleRepository.findByName(roleName).orElse(roleRepository.save(new Role(roleName))))
                    .collect(Collectors.toMap(Role::getName, Function.identity()));

            // ÉTAPE 3: Association des Permissions aux Rôles
            System.out.println("Association des permissions aux rôles...");

            // Rôle ADMIN
            // Assigner toutes les permissions à l'administrateur, y compris la nouvelle
            updateRolePermissions(existingRolesMap.get(ERole.ROLE_ADMIN), allPermissions, roleRepository);

            // Rôle RECEPTIONNISTE
            Set<Permission> receptionistPermissions = new HashSet<>(Arrays.asList(
                    existingPermissionsMap.get("PATIENT_READ"), existingPermissionsMap.get("PATIENT_WRITE"),
                    existingPermissionsMap.get("ADMISSION_READ"), existingPermissionsMap.get("ADMISSION_WRITE"),
                    existingPermissionsMap.get("CONSULTATION_READ"), existingPermissionsMap.get("DEPARTMENT_READ"),
                    existingPermissionsMap.get("DAILY_RECORD_READ"), existingPermissionsMap.get("REPORT_READ_PATIENT"),
                    existingPermissionsMap.get("REPORT_READ_ADMISSION"), existingPermissionsMap.get("REPORT_READ_CONSULTATION"),
                    existingPermissionsMap.get("MEDICAL_RECORD_READ")
            ));
            updateRolePermissions(existingRolesMap.get(ERole.ROLE_RECEPTIONNISTE), receptionistPermissions, roleRepository);

            // Rôle MEDECIN
            Set<Permission> medecinPermissions = new HashSet<>(Arrays.asList(
                    existingPermissionsMap.get("PATIENT_READ"), existingPermissionsMap.get("DAILY_RECORD_READ"),
                    existingPermissionsMap.get("DAILY_RECORD_WRITE"), existingPermissionsMap.get("CONSULTATION_READ"),
                    existingPermissionsMap.get("CONSULTATION_WRITE"), existingPermissionsMap.get("ADMISSION_READ"),
                    existingPermissionsMap.get("DEPARTMENT_READ"), existingPermissionsMap.get("REPORT_READ_CONSULTATION"),
                    existingPermissionsMap.get("MEDICAL_RECORD_READ"), existingPermissionsMap.get("MEDICAL_RECORD_WRITE")
            ));
            updateRolePermissions(existingRolesMap.get(ERole.ROLE_MEDECIN), medecinPermissions, roleRepository);

            // Rôle INFIRMIER
            Set<Permission> infirmierPermissions = new HashSet<>(Arrays.asList(
                    existingPermissionsMap.get("PATIENT_READ"), existingPermissionsMap.get("DAILY_RECORD_READ"),
                    existingPermissionsMap.get("DAILY_RECORD_WRITE"), existingPermissionsMap.get("ADMISSION_READ"),
                    existingPermissionsMap.get("CONSULTATION_READ"), existingPermissionsMap.get("DEPARTMENT_READ"),
                    existingPermissionsMap.get("REPORT_READ_PATIENT"), existingPermissionsMap.get("REPORT_READ_ADMISSION"),
                    existingPermissionsMap.get("REPORT_READ_CONSULTATION"),
                    existingPermissionsMap.get("MEDICAL_RECORD_READ")
            ));
            updateRolePermissions(existingRolesMap.get(ERole.ROLE_INFIRMIER), infirmierPermissions, roleRepository);

            // Rôle PERSONNEL_ADMIN_SORTIE
            Set<Permission> personnelAdminSortiePermissions = new HashSet<>(Arrays.asList(
                    existingPermissionsMap.get("PATIENT_READ"), existingPermissionsMap.get("ADMISSION_READ"),
                    existingPermissionsMap.get("ADMISSION_DISCHARGE")
            ));
            updateRolePermissions(existingRolesMap.get(ERole.ROLE_PERSONNEL_ADMIN_SORTIE), personnelAdminSortiePermissions, roleRepository);

            System.out.println("Association des permissions aux rôles terminée.");
        };
    }

    /**
     * Crée ou récupère une permission de manière transactionnelle et efficace en utilisant une map en mémoire.
     */
    public Permission createOrGetPermission(PermissionRepository permissionRepository, Map<String, Permission> existingPermissionsMap, String name, String description) {
        if (existingPermissionsMap.containsKey(name)) {
            return existingPermissionsMap.get(name);
        } else {
            Permission newPermission = permissionRepository.save(new Permission(name, description));
            existingPermissionsMap.put(name, newPermission); // Met à jour la map en mémoire
            return newPermission;
        }
    }

    /**
     * Met à jour les permissions d'un rôle.
     * Cette méthode ne génère pas de requêtes de recherche car le rôle est déjà chargé.
     */
    public void updateRolePermissions(Role role, Set<Permission> newPermissions, RoleRepository roleRepository) {
        if (role != null) {
            // Vérifie si l'ensemble des permissions a changé pour éviter un `save` inutile
            if (!role.getPermissions().containsAll(newPermissions) || !newPermissions.containsAll(role.getPermissions())) {
                role.setPermissions(newPermissions);
                roleRepository.save(role);
                System.out.println("Permissions assignées au rôle " + role.getName().name() + ".");
            }
        }
    }
}
