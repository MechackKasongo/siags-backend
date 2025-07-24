package com.hgs.patient.siags_backend.controller;

import com.hgs.patient.siags_backend.service.OdooIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/odoo") // Chemin de base pour tous les endpoints Odoo
public class OdooController {

    private static final Logger logger = LoggerFactory.getLogger(OdooController.class); // AJOUTEZ CETTE LIGNE

    // CORRECTION: Nom de la variable en camelCase (minuscule au début)
    private final OdooIntegrationService odooIntegrationService;

    @Autowired
    public OdooController(OdooIntegrationService odooIntegrationService) {
        this.odooIntegrationService = odooIntegrationService;
    }

    // --- Endpoints pour les Partenaires (res.partner) ---

    /**
     * Endpoint pour récupérer tous les partenaires Odoo.
     *
     * @return ResponseEntity contenant la liste des partenaires ou
     * un statut 204 No Content si la liste est vide.
     */
    @GetMapping("/partners")
    public ResponseEntity<List<Map<String, Object>>> getAllOdooPartners() {
        List<Map<String, Object>> partners = odooIntegrationService.getOdooPartners();
        if (partners.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si vide
        }
        return ResponseEntity.ok(partners); // 200 OK avec la liste des partenaires
    }

    /**
     * Endpoint pour récupérer un partenaire Odoo par son ID.
     *
     * @param id L'ID du partenaire à récupérer.
     * @return ResponseEntity contenant les données du partenaire ou un statut 404 Not Found.
     */
    @GetMapping("/partners/{id}")
    public ResponseEntity<Map<String, Object>> getOdooPartnerById(@PathVariable int id) {
        Map<String, Object> partner = odooIntegrationService.getOdooPartnerById(id);
        if (partner == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found si le partenaire n'existe pas
        }
        return ResponseEntity.ok(partner); // 200 OK avec le partenaire
    }

    /**
     * Endpoint pour créer un nouveau partenaire dans Odoo.
     *
     * @param partnerData Un Map contenant les données du partenaire à créer.
     * @return ResponseEntity avec l'ID du nouveau partenaire créé (201 Created) ou un statut 500 Internal Server Error.
     */
    @PostMapping("/partners")
    public ResponseEntity<Integer> createOdooPartner(@RequestBody Map<String, Object> partnerData) {
        Integer newId = odooIntegrationService.createOdooRecord("res.partner", partnerData);
        if (newId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(newId); // 201 Created
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur interne
    }

    /**
     * Endpoint pour mettre à jour un partenaire Odoo existant.
     *
     * @param id         L'ID du partenaire à mettre à jour.
     * @param updateData Un Map contenant les données à mettre à jour.
     * @return ResponseEntity avec un statut 200 OK si la mise à jour a réussi, ou 500 Internal Server Error.
     */
    @PutMapping("/partners/{id}")
    public ResponseEntity<Void> updateOdooPartner(@PathVariable int id, @RequestBody Map<String, Object> updateData) {
        boolean success = odooIntegrationService.updateOdooRecords("res.partner", new int[]{id}, updateData);
        if (success) {
            return ResponseEntity.ok().build(); // 200 OK
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur interne
    }

    /**
     * Endpoint pour supprimer un partenaire Odoo par son ID.
     *
     * @param id L'ID du partenaire à supprimer.
     * @return ResponseEntity avec un statut 200 OK si la suppression a réussi, ou 500 Internal Server Error.
     */
    @DeleteMapping("/partners/{id}")
    public ResponseEntity<Void> deleteOdooPartner(@PathVariable int id) {
        boolean success = odooIntegrationService.deleteOdooRecords("res.partner", new int[]{id});
        if (success) {
            return ResponseEntity.ok().build(); // 200 OK
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur interne
    }

    // --- Endpoints pour les Rendez-vous (calendar.event) ---

    /**
     * Endpoint pour récupérer tous les rendez-vous Odoo.
     *
     * @return ResponseEntity contenant la liste des rendez-vous ou
     * un statut 204 No Content si la liste est vide.
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAllOdooAppointments() {
        List<Map<String, Object>> appointments = odooIntegrationService.getOdooAppointments();
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }

    // --- Endpoints pour les Produits (product.product) ---

    // GARDEZ SEULEMENT CETTE MÉTHODE GET POUR LES PRODUITS

    /**
     * Endpoint pour récupérer tous les produits Odoo.
     *
     * @return ResponseEntity contenant la liste des produits ou
     * un statut 204 No Content si la liste est vide.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getAllOdooProducts() {
        List<Map<String, Object>> products = odooIntegrationService.getOdooProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    // NOUVEL ENDPOINT À AJOUTER POUR LA CRÉATION DE PRODUITS (que vous avez déjà ajouté, juste assurez-vous de l'unicité)

    /**
     * Endpoint pour créer un nouveau produit dans Odoo.
     *
     * @param productData Un Map contenant les données du produit à créer.
     * @return ResponseEntity avec l'ID du nouveau produit créé (201 Created) ou un statut 500 Internal Server Error.
     */
    @PostMapping("/products")
    public ResponseEntity<?> createOdooProduct(@RequestBody Map<String, Object> productData) {
        try {
            // Utilisez la méthode générique createOdooRecord de votre service
            // Le modèle pour la création de produits est product.template
            Integer productId = odooIntegrationService.createOdooRecord("product.template", productData);
            if (productId != null) {
                // Retourne l'ID du produit créé avec un statut 201 Created
                return new ResponseEntity<>(Collections.singletonMap("id", productId), HttpStatus.CREATED);
            } else {
                // Si createOdooRecord retourne null, cela signifie une erreur côté service/Odoo
                return new ResponseEntity<>("Failed to create product in Odoo.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            // Log l'erreur complète pour le débogage
            logger.error("Error creating Odoo product: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error creating Odoo product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint pour mettre à jour un produit Odoo existant.
     *
     * @param id         L'ID du produit à mettre à jour.
     * @param updateData Un Map contenant les données à mettre à jour.
     * @return ResponseEntity avec un statut 200 OK si la mise à jour a réussi, ou 500 Internal Server Error.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateOdooProduct(@PathVariable int id, @RequestBody Map<String, Object> updateData) {
        try {
            boolean success = odooIntegrationService.updateOdooRecords("product.template", new int[]{id}, updateData);
            if (success) {
                return ResponseEntity.ok().build(); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
            }
        } catch (Exception e) {
            logger.error("Error updating Odoo product with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Endpoint pour supprimer un produit Odoo par son ID.
     *
     * @param id L'ID du produit à supprimer.
     * @return ResponseEntity avec un statut 200 OK si la suppression a réussi, ou 500 Internal Server Error.
     */
    @DeleteMapping("/products/{id}") // NOUVEL ENDPOINT À AJOUTER POUR LA SUPPRESSION DE PRODUITS
    public ResponseEntity<Void> deleteOdooProduct(@PathVariable int id) {
        try {
            boolean success = odooIntegrationService.deleteOdooRecords("product.template", new int[]{id});
            if (success) {
                return ResponseEntity.ok().build(); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
            }
        } catch (Exception e) {
            logger.error("Error deleting Odoo product with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
