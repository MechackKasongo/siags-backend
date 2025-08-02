package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.dto.OdooAdmissionDTO;
import com.hgs.patient.siags_backend.dto.OdooPatientContactDTO;
import com.hgs.patient.siags_backend.exception.OdooIntegrationException;
import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.model.Patient;

public interface OdooService {

    /**
     * Crée ou met à jour un contact patient dans Odoo.
     *
     * @param patient L'entité Patient à synchroniser.
     * @return L'ID Odoo du contact créé/mis à jour.
     * @throws OdooIntegrationException si l'intégration Odoo échoue.
     */
    Long createOrUpdateOdooPatientContact(Patient patient) throws OdooIntegrationException;

    /**
     * Crée une nouvelle admission dans Odoo.
     *
     * @param admission L'entité Admission à synchroniser.
     * @return L'ID Odoo de l'admission créée.
     * @throws OdooIntegrationException si l'intégration Odoo échoue.
     */
    Long createOdooAdmission(Admission admission) throws OdooIntegrationException; // <<< TYPE DE RETOUR MODIFIÉ

    /**
     * Met à jour une admission existante dans Odoo.
     *
     * @param admission L'entité Admission mise à jour.
     * @return L'ID Odoo de l'admission mise à jour.
     * @throws OdooIntegrationException si l'intégration Odoo échoue.
     */
    Long updateOdooAdmission(Admission admission) throws OdooIntegrationException; // <<< TYPE DE RETOUR MODIFIÉ

}