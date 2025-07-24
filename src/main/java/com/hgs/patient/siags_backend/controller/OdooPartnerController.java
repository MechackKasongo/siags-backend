package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.service.OdooIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/odoo/partners") // Chemin de base pour les requêtes des partenaires Odoo
public class OdooPartnerController {

    private final OdooIntegrationService odooIntegrationService;

    @Autowired
    public OdooPartnerController(OdooIntegrationService odooIntegrationService) {
        this.odooIntegrationService = odooIntegrationService;
    }

    /**
     * Récupère tous les partenaires depuis Odoo.
     * URL: GET /api/odoo/partners
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<List<Map<String, Object>>> getAllOdooPartners() {
        List<Map<String, Object>> partners = odooIntegrationService.getOdooPartners();
        if (partners.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si aucune donnée
        }
        return new ResponseEntity<>(partners, HttpStatus.OK);
    }

    /**
     * Récupère un partenaire Odoo par son ID.
     * URL: GET /api/odoo/partners/{id}
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<Map<String, Object>> getOdooPartnerById(@PathVariable int id) {
        Map<String, Object> partner = odooIntegrationService.getOdooPartnerById(id);
        if (partner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found si non trouvé
        }
        return new ResponseEntity<>(partner, HttpStatus.OK);
    }

    /**
     * Crée un nouveau partenaire dans Odoo.
     * URL: POST /api/odoo/partners
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE.
     * Le corps de la requête est un Map<String, Object> qui correspond aux champs Odoo.
     * Exemple de corps de requête JSON:
     * {
     * "name": "Nouveau Partenaire Test",
     * "email": "test@example.com",
     * "phone": "123-456-7890"
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<Integer> createOdooPartner(@RequestBody Map<String, Object> partnerData) {
        Integer newPartnerId = odooIntegrationService.createOdooPartner(partnerData);
        if (newPartnerId == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Ou un code d'erreur plus spécifique
        }
        return new ResponseEntity<>(newPartnerId, HttpStatus.CREATED);
    }

    /**
     * Met à jour un partenaire existant dans Odoo.
     * URL: PUT /api/odoo/partners/{id}
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE.
     * Le corps de la requête est un Map<String, Object> qui correspond aux champs Odoo à mettre à jour.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<Void> updateOdooPartner(@PathVariable int id, @RequestBody Map<String, Object> updateData) {
        boolean success = odooIntegrationService.updateOdooPartner(id, updateData);
        if (!success) {
            // Ici, vous pourriez vouloir vérifier si l'ID n'existe pas ou s'il y a une autre erreur Odoo
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content pour une mise à jour réussie sans corps de réponse
    }

    /**
     * Supprime un partenaire dans Odoo.
     * URL: DELETE /api/odoo/partners/{id}
     * Nécessite le rôle ADMIN (ou un rôle plus restrictif si nécessaire pour la suppression dans Odoo).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOdooPartner(@PathVariable int id) {
        boolean success = odooIntegrationService.deleteOdooPartner(id);
        if (!success) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}