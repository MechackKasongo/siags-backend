package com.hgs.patient.siags_backend.service;

import jakarta.annotation.PostConstruct;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

@Service
public class OdooService {

    private static final Logger logger = LoggerFactory.getLogger(OdooService.class);

    @Value("${odoo.url}")
    private String odooUrl;

    @Value("${odoo.database}")
    private String odooDatabase;

    @Value("${odoo.username}")
    private String odooUsername;

    @Value("${odoo.password}")
    private String odooPassword;

    private Integer odooUserId;

    private XmlRpcClient commonClient;
    private XmlRpcClient objectClient;

    @PostConstruct
    public void init() {
        try {
            // Configuration pour le client 'common' (authentification)
            XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
            commonConfig.setServerURL(new URL(odooUrl + "/xmlrpc/2/common"));
            // *** CORRECTION ICI ***
            commonConfig.setEnabledForExtensions(true); // <--- Utilisez cette méthode !
            commonClient = new XmlRpcClient();
            commonClient.setConfig(commonConfig);

            // Configuration pour le client 'object' (appels d'API sur les modèles)
            XmlRpcClientConfigImpl objectConfig = new XmlRpcClientConfigImpl();
            objectConfig.setServerURL(new URL(odooUrl + "/xmlrpc/2/object"));
            // *** CORRECTION ICI ***
            objectConfig.setEnabledForExtensions(true); // <--- Utilisez cette méthode !
            objectClient = new XmlRpcClient();
            objectClient.setConfig(objectConfig);

            authenticate();
            logger.info("Connexion Odoo initialisée avec succès pour l'utilisateur: {}", odooUsername);

        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la connexion Odoo : {}", e.getMessage(), e);
        }
    }

    public Integer authenticate() throws Exception {
        logger.info("Tentative d'authentification Odoo pour l'utilisateur: {}", odooUsername);
        try {
            Object[] params = new Object[]{odooDatabase, odooUsername, odooPassword, null}; // Le 4ème paramètre peut être null
            Object result = commonClient.execute("authenticate", params);

            if (result instanceof Integer) {
                this.odooUserId = (Integer) result;
                logger.info("Authentification Odoo réussie. UID: {}", odooUserId);
                return this.odooUserId;
            } else {
                logger.error("Échec de l'authentification Odoo: La réponse n'est pas un UID entier. Résultat: {}", result);
                throw new Exception("Échec de l'authentification Odoo: UID non reçu.");
            }
        } catch (Exception e) {
            logger.error("Erreur d'authentification Odoo: {}", e.getMessage(), e);
            throw new Exception("Erreur d'authentification Odoo: " + e.getMessage(), e);
        }
    }

    public Object executeOdooMethod(String model, String method, Object[] args) throws Exception {
        if (odooUserId == null) {
            authenticate();
            if (odooUserId == null) {
                throw new Exception("Impossible de s'authentifier auprès d'Odoo. UID est null.");
            }
        }
        Object[] params = new Object[]{odooDatabase, odooUserId, odooPassword, model, method, Arrays.asList(args), Collections.emptyMap()};
        logger.debug("Appel Odoo: Modèle={}, Méthode={}, Args={}", model, method, Arrays.asList(args));
        try {
            return objectClient.execute("execute_kw", params);
        } catch (Exception e) {
            logger.error("Erreur lors de l'appel Odoo (modèle: {}, méthode: {}): {}", model, method, e.getMessage(), e);
            throw new Exception("Erreur lors de l'appel Odoo: " + e.getMessage(), e);
        }
    }

    public XmlRpcClient getCommonClient() {
        return commonClient;
    }

    public XmlRpcClient getObjectClient() {
        return objectClient;
    }

    public Integer getOdooUserId() {
        return odooUserId;
    }
}