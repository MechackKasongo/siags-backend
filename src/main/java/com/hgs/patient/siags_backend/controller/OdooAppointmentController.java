package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.service.OdooIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/odoo/appointments") // Chemin de base pour les requêtes des rendez-vous Odoo
public class OdooAppointmentController {

    private final OdooIntegrationService odooIntegrationService;

    @Autowired
    public OdooAppointmentController(OdooIntegrationService odooIntegrationService) {
        this.odooIntegrationService = odooIntegrationService;
    }

    /**
     * Récupère tous les rendez-vous depuis Odoo.
     * URL: GET /api/odoo/appointments
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE (ou tout rôle pertinent pour voir les agendas).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER')") // Exemple de rôles
    public ResponseEntity<List<Map<String, Object>>> getAllOdooAppointments() {
        List<Map<String, Object>> appointments = odooIntegrationService.getOdooAppointments();
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si aucune donnée
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // Note: Si des opérations de création/mise à jour/suppression de rendez-vous Odoo sont nécessaires
    // via votre API, il faudrait d'abord ajouter ces méthodes dans OdooIntegrationService (si ce n'est pas déjà fait),
    // puis les exposer ici avec des endpoints POST, PUT, DELETE appropriés.
    // Votre OdooIntegrationService a actuellement seulement `getOdooAppointments()`.
}