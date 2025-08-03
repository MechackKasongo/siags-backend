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
     * URL: POST /api/attachments/upload/patient/{patientId}
     * Paramètres: file (MultipartFile), description (String, optionnel)
     */
    @PostMapping("/upload/patient/{patientId}")
    // Qui peut uploader une pièce jointe (ex: un réceptionniste, un médecin qui ajoute un rapport)
    // Assurez-vous d'avoir une permission comme 'ATTACHMENT_UPLOAD' ou 'PATIENT_WRITE' pourrait inclure cela.
    // Pour l'exemple, j'utiliserai une permission ATTACHMENT_WRITE.
    @PreAuthorize("hasAuthority('ATTACHMENT_WRITE')")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long patientId,
            @RequestParam(value = "description", required = false) String description) {

        Attachment attachment = fileStorageService.storeFile(file, patientId, description);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/attachments/download/")
                .path(attachment.getStoredFileName())
                .toUriString();

        return new ResponseEntity<>("Fichier " + attachment.getFileName() + " uploadé avec succès. Télécharger ici : " + fileDownloadUri, HttpStatus.OK);
    }

    /**
     * Télécharge un fichier en utilisant son nom stocké unique.
     * URL: GET /api/attachments/download/{storedFileName}
     */
    @GetMapping("/download/{storedFileName}")
    // Qui peut télécharger : n'importe quel rôle ayant besoin de consulter les dossiers
    // J'utiliserai ici une permission 'ATTACHMENT_READ'
    @PreAuthorize("hasAuthority('ATTACHMENT_READ')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedFileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(storedFileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Could not determine file type.");
        }

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
     * URL: GET /api/attachments/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    // Qui peut lister les pièces jointes : n'importe quel rôle ayant besoin de consulter les dossiers
    // J'utiliserai ici 'ATTACHMENT_READ'
    @PreAuthorize("hasAuthority('ATTACHMENT_READ')")
    public ResponseEntity<List<Attachment>> getAttachmentsByPatient(@PathVariable Long patientId) {
        List<Attachment> attachments = fileStorageService.getAttachmentsByPatientId(patientId);
        if (attachments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }

    /**
     * Supprime une pièce jointe par son ID.
     * URL: DELETE /api/attachments/{attachmentId}
     */
    @DeleteMapping("/{attachmentId}")
    // Seul l'Admin ou un rôle ayant la permission de suppression peut faire cela
    @PreAuthorize("hasAuthority('ATTACHMENT_DELETE')")
    public ResponseEntity<HttpStatus> deleteAttachment(@PathVariable Long attachmentId) {
        fileStorageService.deleteAttachment(attachmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}