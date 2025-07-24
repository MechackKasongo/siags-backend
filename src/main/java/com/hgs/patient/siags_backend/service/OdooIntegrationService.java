package com.hgs.patient.siags_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OdooIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(OdooIntegrationService.class);

    private final OdooService odooService; // Injection de OdooService

    @Autowired // Spring injectera l'instance de OdooService
    public OdooIntegrationService(OdooService odooService) {
        this.odooService = odooService;
    }

    public List<Map<String, Object>> getOdooPartners() {
        try {
            // ajouter des filtres (domain) si nécessaire, ex:
            // Object[] domain = new Object[]{"|", new Object[]{"customer", "=", true}, new Object[]{"supplier", "=", true}};
            Object[] domain = {}; // Récupérer tous les partenaires (domaine vide)
            Object[] fields = {"id", "name", "email", "phone"}; // Les champs à récupérer, vous pouvez en ajouter d'autres
            Object[] result = (Object[]) odooService.executeOdooMethod("res.partner", "search_read", new Object[]{domain, fields});

            List<Map<String, Object>> partners = new ArrayList<>();
            if (result != null) {
                for (Object record : result) {
                    if (record instanceof Map) {
                        partners.add((Map<String, Object>) record);
                    }
                }
            }
            return partners;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des partenaires Odoo : {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Récupère un partenaire Odoo par son ID.
     *
     * @param id L'ID du partenaire à récupérer.
     * @return Un Map contenant les données du partenaire, ou null si non trouvé.
     */
    public Map<String, Object> getOdooPartnerById(int id) {
        try {
            // Conversion int[] en List<Integer> pour les appels de lecture Odoo
            List<Integer> idList = Collections.singletonList(id); // Utiliser Collections.singletonList pour un seul ID
            Object[] result = (Object[]) odooService.executeOdooMethod("res.partner", "read", new Object[]{idList, new String[]{"id", "name", "email", "phone"}});
            if (result != null && result.length > 0 && result[0] instanceof Map) {
                return (Map<String, Object>) result[0];
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du partenaire Odoo avec l'ID {} : {}", id, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Récupère tous les rendez-vous Odoo.
     *
     * @return Une liste de Map contenant les données des rendez-vous, ou une liste vide en cas d'erreur.
     */
    public List<Map<String, Object>> getOdooAppointments() {
        try {
            // Exemple de filtre : Récupérer les rendez-vous après une certaine date
            // Note: Les dates dans Odoo peuvent nécessiter un format spécifique (UTC)
            // Object[] domain = new Object[] { new Object[]{"start_datetime", ">=", "2025-01-01 00:00:00"} };
            Object[] domain = {}; // Pas de filtre pour l'exemple

            Object[] fields = {"id", "name", "start_datetime", "stop_datetime", "partner_ids"};
            // 'partner_ids' est un champ relationnel qui renvoie les IDs des partenaires liés.
            // Si vous voulez les noms, vous devrez faire une autre requête ou utiliser un champ "Many2one" s'il existe.

            Object[] result = (Object[]) odooService.executeOdooMethod("calendar.event", "search_read", new Object[]{domain, fields});

            List<Map<String, Object>> appointments = new ArrayList<>();
            if (result != null) {
                for (Object record : result) {
                    if (record instanceof Map) {
                        appointments.add((Map<String, Object>) record);
                    }
                }
            }
            return appointments;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des rendez-vous Odoo : {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Crée un nouveau partenaire Odoo.
     *
     * @param partnerData Un Map contenant les données du partenaire à créer.
     * @return L'ID du nouveau partenaire créé, ou null en cas d'erreur.
     */
    public Integer createOdooPartner(Map<String, Object> partnerData) {
        try {
            Object result = odooService.executeOdooMethod("res.partner", "create", new Object[]{partnerData});
            if (result instanceof Integer) {
                return (Integer) result;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création du partenaire Odoo : {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Met à jour un partenaire Odoo existant.
     *
     * @param id         L'ID du partenaire à mettre à jour.
     * @param updateData Un Map contenant les données à mettre à jour.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean updateOdooPartner(int id, Map<String, Object> updateData) {
        try {
            // Conversion int[] en List<Integer>
            List<Integer> idList = Collections.singletonList(id); // Utiliser Collections.singletonList pour un seul ID
            Object result = odooService.executeOdooMethod("res.partner", "write", new Object[]{idList, updateData});
            return result != null && (Boolean) result; // Odoo retourne souvent true/false pour write/unlink
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du partenaire Odoo avec l'ID {} : {}", id, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Supprime un partenaire Odoo par son ID.
     *
     * @param id L'ID du partenaire à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteOdooPartner(int id) {
        try {
            // Conversion int[] en List<Integer>
            List<Integer> idList = Collections.singletonList(id); // Utiliser Collections.singletonList pour un seul ID
            Object result = odooService.executeOdooMethod("res.partner", "unlink", new Object[]{idList});
            return result != null && (Boolean) result;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du partenaire Odoo avec l'ID {} : {}", id, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Récupère tous les produits Odoo.
     *
     * @return Une liste de Map contenant les données des produits, ou une liste vide en cas d'erreur.
     */
    public List<Map<String, Object>> getOdooProducts() {
        try {
            Object[] domain = {}; // Pas de filtre
            Object[] fields = {"id", "name", "list_price", "qty_available"};
            //      Object[] result = (Object[]) odooService.executeOdooMethod("product.product", "search_read", new Object[]{domain, fields});

            Object[] result = (Object[]) odooService.executeOdooMethod("product.template", "search_read", new Object[]{domain, fields});
            List<Map<String, Object>> products = new ArrayList<>();
            if (result != null) {
                for (Object record : result) {
                    if (record instanceof Map) {
                        products.add((Map<String, Object>) record);
                    }
                }
            }
            return products;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des produits Odoo : {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Crée un nouvel enregistrement dans Odoo.
     *
     * @param modelName Le nom du modèle Odoo (ex: "res.partner")
     * @param data      Les données de l'enregistrement sous forme de Map (clé-valeur)
     * @return L'ID du nouvel enregistrement créé dans Odoo, ou null en cas d'échec.
     */
    public Integer createOdooRecord(String modelName, Map<String, Object> data) {
        try {
            logger.info("Tentative de création d'un enregistrement dans Odoo. Modèle: {}, Données: {}", modelName, data);
            Object result = odooService.executeOdooMethod(modelName, "create", new Object[]{data});
            if (result instanceof Integer) {
                logger.info("Enregistrement créé avec succès dans Odoo. Modèle: {}, ID: {}", modelName, result);
                return (Integer) result;
            } else {
                logger.warn("La création de l'enregistrement dans Odoo a retourné un résultat inattendu: {}", result);
                return null;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'enregistrement dans Odoo. Modèle: {}, Erreur: {}", modelName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Met à jour un ou plusieurs enregistrements existants dans Odoo.
     *
     * @param modelName Le nom du modèle Odoo (ex: "res.partner")
     * @param ids       Les IDs des enregistrements à mettre à jour (peut être un seul ID dans un tableau)
     * @param data      Les données à mettre à jour sous forme de Map (clé-valeur)
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean updateOdooRecords(String modelName, int[] ids, Map<String, Object> data) {
        try {
            logger.info("Tentative de mise à jour d'enregistrements dans Odoo. Modèle: {}, IDs: {}, Données: {}", modelName, ids, data);

            // CORRECTION: Convertir le tableau d'IDs primitifs (int[]) en List<Integer>
            // C'est cette conversion qui est cruciale pour que XML-RPC envoie une liste d'entiers correcte.
            List<Integer> idList = Arrays.stream(ids).boxed().collect(Collectors.toList());

            // Les arguments de la méthode 'write' d'Odoo sont [liste_IDs, dictionnaire_valeurs]
            Object result = odooService.executeOdooMethod(modelName, "write", new Object[]{idList, data});

            if (result instanceof Boolean) {
                if ((Boolean) result) {
                    logger.info("Enregistrements mis à jour avec succès dans Odoo. Modèle: {}, IDs: {}", modelName, ids);
                } else {
                    logger.warn("La mise à jour des enregistrements dans Odoo a échoué (Odoo a retourné false). Modèle: {}, IDs: {}", modelName, ids);
                }
                return (Boolean) result;
            } else {
                logger.warn("La mise à jour des enregistrements dans Odoo a retourné un résultat inattendu: {}", result);
                return false;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des enregistrements dans Odoo. Modèle: {}, IDs: {}, Erreur: {}", modelName, ids, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Supprime un ou plusieurs enregistrements dans Odoo.
     *
     * @param modelName Le nom du modèle Odoo (ex: "res.partner")
     * @param ids       Les IDs des enregistrements à supprimer
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteOdooRecords(String modelName, int[] ids) {
        try {
            logger.info("Tentative de suppression d'enregistrements dans Odoo. Modèle: {}, IDs: {}", modelName, ids);

            // CORRECTION: Convertir le tableau d'IDs primitifs (int[]) en List<Integer>
            List<Integer> idList = Arrays.stream(ids).boxed().collect(Collectors.toList());

            Object result = odooService.executeOdooMethod(modelName, "unlink", new Object[]{idList});
            if (result instanceof Boolean) {
                if ((Boolean) result) {
                    logger.info("Enregistrements supprimés avec succès dans Odoo. Modèle: {}, IDs: {}", modelName, ids);
                } else {
                    logger.warn("La suppression des enregistrements dans Odoo a échoué (Odoo a retourné false). Modèle: {}, IDs: {}", modelName, ids);
                }
                return (Boolean) result;
            } else {
                logger.warn("La suppression des enregistrements dans Odoo a retourné un résultat inattendu: {}", result);
                return false;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression des enregistrements dans Odoo. Modèle: {}, IDs: {}, Erreur: {}", modelName, ids, e.getMessage(), e);
            return false;
        }
    }
}