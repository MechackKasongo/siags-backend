package com.hgs.patient.siags_backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hgs.patient.siags_backend.dto.OdooAdmissionDTO;
import com.hgs.patient.siags_backend.dto.OdooPatientContactDTO;
import com.hgs.patient.siags_backend.exception.OdooIntegrationException;
import com.hgs.patient.siags_backend.model.Admission;
import com.hgs.patient.siags_backend.model.BloodType;
import com.hgs.patient.siags_backend.model.Department;
import com.hgs.patient.siags_backend.model.Gender;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.AdmissionRepository; // Ajouté pour la persistance de l'ID Odoo d'admission
import com.hgs.patient.siags_backend.repository.DepartmentRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository; // Ajouté pour la persistance de l'ID Odoo patient
import com.hgs.patient.siags_backend.service.OdooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Ajouté pour les opérations de mise à jour sur les entités
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OdooServiceImp implements OdooService {

    @Value("${odoo.url}")
    private String odooUrl;

    @Value("${odoo.db}")
    private String odooDb;

    @Value("${odoo.username}")
    private String odooUsername;

    @Value("${odoo.password}")
    private String odooPassword;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository; // Injecté
    private final DepartmentRepository departmentRepository; // Injecté
    private final AdmissionRepository admissionRepository; // Injecté

    @Autowired
    public OdooServiceImp(RestTemplate restTemplate, ObjectMapper objectMapper,
                          PatientRepository patientRepository,
                          DepartmentRepository departmentRepository,
                          AdmissionRepository admissionRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.admissionRepository = admissionRepository;
    }

    /**
     * Authentifie l'utilisateur Odoo et récupère l'UID.
     *
     * @return L'ID utilisateur (UID) pour les appels RPC.
     * @throws OdooIntegrationException si l'authentification échoue.
     */
    private Long authenticateOdoo() throws OdooIntegrationException {
        String url = odooUrl + "/web/session/authenticate";
        Map<String, Object> params = new HashMap<>();
        params.put("db", odooDb);
        params.put("login", odooUsername);
        params.put("password", odooPassword);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "call");
        requestBody.put("params", params);
        requestBody.put("id", 1); // Un ID de requête arbitraire

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
            JsonNode result = response.getBody().get("result");
            if (result != null && result.has("uid")) {
                return result.get("uid").asLong();
            } else if (response.getBody().has("error")) {
                throw new OdooIntegrationException("Erreur d'authentification Odoo: " + response.getBody().get("error").toPrettyString());
            } else {
                throw new OdooIntegrationException("Réponse d'authentification Odoo inattendue: " + response.getBody().toPrettyString());
            }
        } catch (HttpClientErrorException e) {
            throw new OdooIntegrationException("Erreur HTTP lors de l'authentification Odoo: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new OdooIntegrationException("Erreur lors de l'authentification Odoo: " + e.getMessage(), e);
        }
    }

    /**
     * Appelle une méthode RPC du modèle Odoo.
     *
     * @param uid    L'ID utilisateur authentifié.
     * @param model  Le nom du modèle Odoo (ex: "res.partner", "hospital.patient").
     * @param method La méthode à appeler (ex: "create", "write", "search_read").
     * @param args   Les arguments positionnels pour la méthode.
     * @param kwargs Les arguments par mot-clé pour la méthode.
     * @return La réponse JSON de l'appel RPC.
     * @throws OdooIntegrationException si l'appel RPC échoue.
     */
    private JsonNode callOdooModelRpc(Long uid, String model, String method, List<Object> args, Map<String, Object> kwargs) throws OdooIntegrationException {
        String url = odooUrl + "/web/dataset/call_kw/" + model + "/" + method;

        Map<String, Object> params = new HashMap<>();
        params.put("model", model);
        params.put("method", method);
        params.put("args", args != null ? args : Collections.emptyList());
        params.put("kwargs", kwargs != null ? kwargs : Collections.emptyMap());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "call");
        requestBody.put("params", params);
        requestBody.put("id", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Ajoute le cookie de session si disponible (non directement géré ici, mais important pour les appels subséquents)
        // Pour une implémentation complète, vous stockeriez et réutiliseriez le cookie de session Odoo.
        // Pour cet exemple simple, nous nous basons sur l'authentification UID.

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
            JsonNode responseBody = response.getBody();

            if (responseBody != null && responseBody.has("result")) {
                return responseBody.get("result");
            } else if (responseBody != null && responseBody.has("error")) {
                throw new OdooIntegrationException("Erreur RPC Odoo pour le modèle " + model + ", méthode " + method + ": " + responseBody.get("error").toPrettyString());
            } else {
                throw new OdooIntegrationException("Réponse RPC Odoo inattendue pour le modèle " + model + ", méthode " + method + ": " + (responseBody != null ? responseBody.toPrettyString() : "null"));
            }
        } catch (HttpClientErrorException e) {
            throw new OdooIntegrationException("Erreur HTTP lors de l'appel RPC Odoo (" + model + "." + method + "): " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new OdooIntegrationException("Erreur lors de l'appel RPC Odoo (" + model + "." + method + "): " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional // Permet de persister les modifications sur l'entité Patient
    public Long createOrUpdateOdooPatientContact(Patient patient) throws OdooIntegrationException {
        Long uid = authenticateOdoo();
        Long odooContactId = patient.getOdooContactId();

        if (odooContactId == null) {
            // Créer un nouveau contact
            odooContactId = createOdooContact(mapPatientToOdooPatientContactDTO(patient), uid);
            patient.setOdooContactId(odooContactId); // Met à jour l'ID Odoo dans l'entité Patient
            patientRepository.save(patient); // Sauvegarde l'entité Patient avec le nouvel ID Odoo
            System.out.println("Contact Odoo créé avec l'ID: " + odooContactId + " pour patient SIAGS ID: " + patient.getId());
        } else {
            // Mettre à jour un contact existant
            updateOdooContact(mapPatientToOdooPatientContactDTO(patient), uid);
            System.out.println("Contact Odoo mis à jour avec l'ID: " + odooContactId + " pour patient SIAGS ID: " + patient.getId());
        }
        return odooContactId; // Retourne l'ID Odoo
    }

    /**
     * Crée un nouveau contact dans Odoo.
     */
    private Long createOdooContact(OdooPatientContactDTO odooContactData, Long uid) throws OdooIntegrationException {
        System.out.println("Création d'un nouveau contact Odoo pour: " + odooContactData.getName());
        String model = "res.partner";
        String method = "create";

        Map<String, Object> fields = new HashMap<>();
        fields.put("name", odooContactData.getName());
        fields.put("email", odooContactData.getEmail());
        fields.put("phone", odooContactData.getPhone());
        fields.put("mobile", odooContactData.getMobile());
        fields.put("street", odooContactData.getStreet());
        fields.put("city", odooContactData.getCity());
        fields.put("zip", odooContactData.getZip());
        fields.put("type", odooContactData.getType());
        fields.put("x_siags_patient_id", odooContactData.getSiagsPatientId().intValue());

        if (odooContactData.getBirthDate() != null) {
            fields.put("x_birth_date", odooContactData.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (odooContactData.getGender() != null) {
            fields.put("x_gender", odooContactData.getGender().toString().toLowerCase());
        }
        if (odooContactData.getRecordNumber() != null) {
            fields.put("x_record_number", odooContactData.getRecordNumber());
        }
        if (odooContactData.getBloodType() != null) {
            fields.put("x_blood_type", odooContactData.getBloodType().toString().toLowerCase());
        }
        if (odooContactData.getKnownIllnesses() != null) {
            fields.put("x_known_illnesses", odooContactData.getKnownIllnesses());
        }
        if (odooContactData.getAllergies() != null) {
            fields.put("x_allergies", odooContactData.getAllergies());
        }

        List<Object> args = Collections.singletonList(fields);

        JsonNode newIdNode = callOdooModelRpc(uid, model, method, args, null);
        if (newIdNode != null && newIdNode.isIntegralNumber()) {
            return newIdNode.asLong();
        }
        throw new OdooIntegrationException("Échec de la création du contact Odoo: ID non retourné.");
    }

    /**
     * Met à jour un contact existant dans Odoo.
     */
    private void updateOdooContact(OdooPatientContactDTO odooContactData, Long uid) throws OdooIntegrationException {
        if (odooContactData.getOdooContactId() == null) {
            throw new OdooIntegrationException("Impossible de mettre à jour le contact Odoo: l'ID Odoo est null.");
        }
        System.out.println("Mise à jour du contact Odoo ID: " + odooContactData.getOdooContactId() + " pour: " + odooContactData.getName());
        String model = "res.partner";
        String method = "write";

        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("name", odooContactData.getName());
        fieldsToUpdate.put("email", odooContactData.getEmail());
        fieldsToUpdate.put("phone", odooContactData.getPhone());
        fieldsToUpdate.put("mobile", odooContactData.getMobile());
        fieldsToUpdate.put("street", odooContactData.getStreet());
        fieldsToUpdate.put("city", odooContactData.getCity());
        fieldsToUpdate.put("zip", odooContactData.getZip());

        if (odooContactData.getBirthDate() != null) {
            fieldsToUpdate.put("x_birth_date", odooContactData.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            fieldsToUpdate.put("x_birth_date", false); // Pour effacer la valeur si null
        }

        if (odooContactData.getGender() != null) {
            fieldsToUpdate.put("x_gender", odooContactData.getGender().toString().toLowerCase());
        } else {
            fieldsToUpdate.put("x_gender", false);
        }

        if (odooContactData.getRecordNumber() != null) {
            fieldsToUpdate.put("x_record_number", odooContactData.getRecordNumber());
        } else {
            fieldsToUpdate.put("x_record_number", false);
        }

        if (odooContactData.getBloodType() != null) {
            fieldsToUpdate.put("x_blood_type", odooContactData.getBloodType().toString().toLowerCase());
        } else {
            fieldsToUpdate.put("x_blood_type", false);
        }

        if (odooContactData.getKnownIllnesses() != null) {
            fieldsToUpdate.put("x_known_illnesses", odooContactData.getKnownIllnesses());
        } else {
            fieldsToUpdate.put("x_known_illnesses", false);
        }

        if (odooContactData.getAllergies() != null) {
            fieldsToUpdate.put("x_allergies", odooContactData.getAllergies());
        } else {
            fieldsToUpdate.put("x_allergies", false);
        }

        List<Object> args = List.of(Collections.singletonList(odooContactData.getOdooContactId()), fieldsToUpdate);

        callOdooModelRpc(uid, model, method, args, null);
    }


    @Override
    @Transactional // Permet de persister les modifications sur l'entité Admission
    public Long createOdooAdmission(Admission admission) throws OdooIntegrationException {
        Long uid = authenticateOdoo();
        System.out.println("Création d'une nouvelle admission Odoo pour le patient: " + admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName());
        String model = "hospital.admission"; // Assurez-vous que c'est le bon modèle Odoo
        String method = "create";

        OdooAdmissionDTO odooAdmissionData = mapAdmissionToOdooAdmissionDTO(admission);

        Map<String, Object> fields = new HashMap<>();
        fields.put("siags_admission_id", odooAdmissionData.getSiagsAdmissionId().intValue()); // L'ID de SIAGS
        fields.put("patient_id", odooAdmissionData.getOdooPatientContactId()); // ID du contact Odoo du patient
        fields.put("patient_name", odooAdmissionData.getPatientName()); // Nom du patient (peut être utilisé pour l'affichage)
        fields.put("reason_for_admission", odooAdmissionData.getReasonForAdmission());
        fields.put("department_name", odooAdmissionData.getDepartmentName());
        fields.put("status", odooAdmissionData.getStatus());
        if (odooAdmissionData.getAdmissionDate() != null) {
            fields.put("admission_date", odooAdmissionData.getAdmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (odooAdmissionData.getDischargeDate() != null) {
            fields.put("discharge_date", odooAdmissionData.getDischargeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        fields.put("diagnosis", odooAdmissionData.getDiagnosis());
        fields.put("room_number", odooAdmissionData.getRoomNumber());
        fields.put("bed_number", odooAdmissionData.getBedNumber());

        List<Object> args = Collections.singletonList(fields);

        JsonNode newIdNode = callOdooModelRpc(uid, model, method, args, null);
        if (newIdNode != null && newIdNode.isIntegralNumber()) {
            Long newOdooId = newIdNode.asLong();
            admission.setOdooAdmissionId(newOdooId); // Sauvegarde l'ID Odoo dans l'entité Admission
            admissionRepository.save(admission); // Persiste l'entité Admission avec le nouvel ID Odoo
            System.out.println("Admission Odoo créée avec l'ID: " + newOdooId);
            return newOdooId;
        }
        throw new OdooIntegrationException("Échec de la création de l'admission Odoo: ID non retourné.");
    }

    @Override
    @Transactional // Permet de persister les modifications sur l'entité Admission
    public Long updateOdooAdmission(Admission admission) throws OdooIntegrationException {
        Long uid = authenticateOdoo();
        Long odooAdmissionId = admission.getOdooAdmissionId();
        if (odooAdmissionId == null) {
            throw new OdooIntegrationException("Impossible de mettre à jour l'admission Odoo: l'ID Odoo est null. Utilisez createOdooAdmission pour la première synchronisation.");
        }
        System.out.println("Mise à jour de l'admission Odoo ID: " + odooAdmissionId + " pour le patient: " + admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName());
        String model = "hospital.admission"; // Assurez-vous que c'est le bon modèle Odoo
        String method = "write";

        OdooAdmissionDTO odooAdmissionData = mapAdmissionToOdooAdmissionDTO(admission);

        Map<String, Object> fieldsToUpdate = new HashMap<>();
        // Note: siags_admission_id, patient_id et patient_name ne devraient pas changer lors d'une mise à jour normale
        // fieldsToUpdate.put("siags_admission_id", odooAdmissionData.getSiagsAdmissionId().intValue());
        // fieldsToUpdate.put("patient_id", odooAdmissionData.getOdooPatientContactId());
        // fieldsToUpdate.put("patient_name", odooAdmissionData.getPatientName());
        fieldsToUpdate.put("reason_for_admission", odooAdmissionData.getReasonForAdmission());
        fieldsToUpdate.put("department_name", odooAdmissionData.getDepartmentName());
        fieldsToUpdate.put("status", odooAdmissionData.getStatus());
        if (odooAdmissionData.getAdmissionDate() != null) {
            fieldsToUpdate.put("admission_date", odooAdmissionData.getAdmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            fieldsToUpdate.put("admission_date", false); // Pour effacer si null
        }
        if (odooAdmissionData.getDischargeDate() != null) {
            fieldsToUpdate.put("discharge_date", odooAdmissionData.getDischargeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            fieldsToUpdate.put("discharge_date", false);
        }
        fieldsToUpdate.put("diagnosis", odooAdmissionData.getDiagnosis());
        fieldsToUpdate.put("room_number", odooAdmissionData.getRoomNumber());
        fieldsToUpdate.put("bed_number", odooAdmissionData.getBedNumber());

        List<Object> args = List.of(Collections.singletonList(odooAdmissionId), fieldsToUpdate);

        callOdooModelRpc(uid, model, method, args, null);
        System.out.println("Admission Odoo ID: " + odooAdmissionId + " mise à jour.");
        return odooAdmissionId;
    }


    /**
     * Mappe une entité Patient à un DTO OdooPatientContactDTO.
     */
    private OdooPatientContactDTO mapPatientToOdooPatientContactDTO(Patient patient) {
        Gender genderEnum = null;
        if (patient.getGender() != null && !patient.getGender().trim().isEmpty()) {
            try {
                genderEnum = Gender.valueOf(patient.getGender().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Chaîne '" + patient.getGender() + "' n'est pas une valeur valide pour l'enum Gender.");
            }
        }

        BloodType bloodTypeEnum = null;
        if (patient.getBloodType() != null && !patient.getBloodType().trim().isEmpty()) {
            try {
                bloodTypeEnum = BloodType.valueOf(patient.getBloodType().trim().toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                System.err.println("Chaîne '" + patient.getBloodType() + "' n'est pas une valeur valide pour l'enum BloodType.");
            }
        }

        return new OdooPatientContactDTO(
                patient.getId(), // siagsPatientId
                patient.getFirstName() + " " + patient.getLastName(), // name
                patient.getEmail(),
                patient.getPhoneNumber(), // phone
                patient.getPhoneNumber(), // mobile (souvent le même que phone)
                patient.getAddress(), // street
                patient.getCity(),
                patient.getZipCode(),
                null, // parentId - non utilisé ici
                false, // isCompany - false pour un contact
                "contact", // type
                patient.getOdooContactId(), // odooContactId
                null, // companyId - non utilisé ici
                patient.getBirthDate(),
                genderEnum,
                patient.getRecordNumber(),
                bloodTypeEnum,
                patient.getKnownIllnesses(),
                patient.getAllergies()
        );
    }

    /**
     * Mappe une entité Admission à un DTO OdooAdmissionDTO.
     */
    private OdooAdmissionDTO mapAdmissionToOdooAdmissionDTO(Admission admission) {
        String status = "draft";
        if (admission.getStatus() != null) {
            status = admission.getStatus().toString().toLowerCase();
        }

        String departmentName = null;
        if (admission.getAssignedDepartment() != null) {
            departmentName = admission.getAssignedDepartment().getName();
        }

        return new OdooAdmissionDTO(
                admission.getId(), // siagsAdmissionId
                admission.getPatient().getOdooContactId(), // odooPatientContactId
                admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName(), // patientName
                admission.getReasonForAdmission(),
                departmentName, // departmentName
                status,
                admission.getOdooAdmissionId(), // odooAdmissionRecordId
                admission.getAdmissionDate(),
                admission.getDischargeDate(),
                admission.getDiagnosis(),
                admission.getRoomNumber(),
                admission.getBedNumber()
        );
    }
}