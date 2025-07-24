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
@RequestMapping("/api/odoo/products") // Chemin de base pour les requêtes des produits Odoo
public class OdooProductController {

    private final OdooIntegrationService odooIntegrationService;

    @Autowired
    public OdooProductController(OdooIntegrationService odooIntegrationService) {
        this.odooIntegrationService = odooIntegrationService;
    }

    /**
     * Récupère tous les produits depuis Odoo.
     * URL: GET /api/odoo/products
     * Nécessite le rôle ADMIN ou RECEPTIONNISTE (ou tout rôle pertinent pour voir les produits/services).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER')") // Exemple de rôles
    public ResponseEntity<List<Map<String, Object>>> getAllOdooProducts() {
        List<Map<String, Object>> products = odooIntegrationService.getOdooProducts();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si aucune donnée
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Note: Pour les produits Odoo, vous pourriez aussi avoir besoin de :
    // - getProductById si votre OdooIntegrationService offre une telle méthode.
    // - createProduct, updateProduct, deleteProduct si vous souhaitez permettre la manipulation des produits depuis le backend.
    // Pour l'instant, OdooIntegrationService.getOdooProducts() est la seule méthode de lecture pour les produits.
    // Si des opérations de création/mise à jour sont nécessaires, il faudrait les ajouter à OdooIntegrationService en premier.
}