package com.hgs.patient.siags_backend.controller;


import com.hgs.patient.siags_backend.model.Attachment;

import com.hgs.patient.siags_backend.service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;

import java.util.List;


//@CrossOrigin(origins = "*", maxAge = 3600)

@RestController

@RequestMapping("/api/attachments")

public class AttachmentController {


    private final FileStorageService fileStorageService;


    @Autowired

    public AttachmentController(FileStorageService fileStorageService) {

        this.fileStorageService = fileStorageService;

    }


    /**
     * Uploade un fichier pour un patient spécifique.
     * <p>
     * URL: POST /api/attachments/upload/patient/{patientId}
     * <p>
     * Paramètres: file (MultipartFile), description (String, optionnel)
     */

    @PostMapping("/upload/patient/{patientId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')") // Qui peut uploader une pièce jointe

    public ResponseEntity<String> uploadFile(

            @RequestParam("file") MultipartFile file,

            @PathVariable Long patientId,

            @RequestParam(value = "description", required = false) String description) {


        Attachment attachment = fileStorageService.storeFile(file, patientId, description);


// Crée l'URL de téléchargement pour le fichier uploadé

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()

                .path("/api/attachments/download/")

                .path(attachment.getStoredFileName()) // Utilise le nom de fichier stocké unique

                .toUriString();


        return new ResponseEntity<>("Fichier " + attachment.getFileName() + " uploadé avec succès. Télécharger ici : " + fileDownloadUri, HttpStatus.OK);

    }


    /**
     * Télécharge un fichier en utilisant son nom stocké unique.
     * <p>
     * URL: GET /api/attachments/download/{storedFileName}
     */

    @GetMapping("/download/{storedFileName}")

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")

// Qui peut télécharger

    public ResponseEntity<Resource> downloadFile(@PathVariable String storedFileName, HttpServletRequest request) {

// Charge le fichier en tant que ressource Spring

        Resource resource = fileStorageService.loadFileAsResource(storedFileName);


// Détermine le type de contenu du fichier

        String contentType = null;

        try {

            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        } catch (IOException ex) {

// Log l'erreur, mais ne bloque pas le téléchargement

            System.err.println("Could not determine file type.");

        }


// Si le type de contenu n'a pas pu être déterminé, utilise un type générique

        if (contentType == null) {

            contentType = "application/octet-stream";

        }


        return ResponseEntity.ok()

                .contentType(MediaType.parseMediaType(contentType))

                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")

                .body(resource);

    }


    /**
     * Récupère la liste des métadonnées de toutes les pièces jointes pour un patient.
     * <p>
     * URL: GET /api/attachments/patient/{patientId}
     */

    @GetMapping("/patient/{patientId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'MEDECIN', 'INFIRMIER', 'PERSONNEL_ADMIN_SORTIE')")

    public ResponseEntity<List<Attachment>> getAttachmentsByPatient(@PathVariable Long patientId) {

        List<Attachment> attachments = fileStorageService.getAttachmentsByPatientId(patientId);

        if (attachments.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

        return new ResponseEntity<>(attachments, HttpStatus.OK);

    }


    /**
     * Supprime une pièce jointe par son ID.
     * <p>
     * URL: DELETE /api/attachments/{attachmentId}
     */

    @DeleteMapping("/{attachmentId}")

    @PreAuthorize("hasRole('ADMIN')") // Seul l'Admin peut supprimer définitivement

    public ResponseEntity<HttpStatus> deleteAttachment(@PathVariable Long attachmentId) {

        fileStorageService.deleteAttachment(attachmentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
