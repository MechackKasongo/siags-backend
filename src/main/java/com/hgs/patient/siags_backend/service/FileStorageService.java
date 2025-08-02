package com.hgs.patient.siags_backend.service;

import com.hgs.patient.siags_backend.exception.FileNotFoundException;
import com.hgs.patient.siags_backend.exception.FileStorageException;
import com.hgs.patient.siags_backend.exception.ResourceNotFoundException;
import com.hgs.patient.siags_backend.model.Attachment;
import com.hgs.patient.siags_backend.model.Patient;
import com.hgs.patient.siags_backend.repository.AttachmentRepository;
import com.hgs.patient.siags_backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final AttachmentRepository attachmentRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public FileStorageService(

            @Value("${file.upload-dir}") String uploadDir, // Chemin défini dans application.properties
            AttachmentRepository attachmentRepository,
            PatientRepository patientRepository) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.attachmentRepository = attachmentRepository;
        this.patientRepository = patientRepository;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Impossible de créer le répertoire de stockage des fichiers.", ex);
        }
    }

    /**
     * Stocke un fichier dans le système de fichiers et enregistre ses métadonnées dans la base de données.
     *
     * @param file        Le fichier à stocker.
     * @param patientId   L'ID du patient associé à la pièce jointe.
     * @param description La description de la pièce jointe.
     * @return L'objet Attachment contenant les métadonnées du fichier stocké.
     */

    public Attachment storeFile(MultipartFile file, Long patientId, String description) {
        // Normalise le nom du fichier
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            // Vérifie si le nom du fichier contient des caractères invalides
            if (fileName.contains("..")) {
                throw new FileStorageException("Le nom du fichier contient une séquence de chemin invalide " + fileName);
            }

            // Génère un nom de fichier unique pour le stockage
            String storedFileName = UUID.randomUUID() + "_" + fileName;
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);

            // Copie le fichier dans l'emplacement cible (remplace un fichier existant)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trouve le patient associé
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID : " + patientId));

            // Crée et sauvegarde les métadonnées de la pièce jointe
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setStoredFileName(storedFileName);
            attachment.setFilePath(targetLocation.toString()); // Chemin absolu du fichier stocké
            attachment.setPatient(patient);
            attachment.setUploadDate(LocalDateTime.now());
            attachment.setDescription(description);
            return attachmentRepository.save(attachment);

        } catch (IOException ex) {
            throw new FileStorageException("Impossible de stocker le fichier " + fileName + ". Veuillez réessayer!", ex);
        }
    }

    /**
     * Charge un fichier depuis le système de stockage en utilisant son nom stocké.
     *
     * @param storedFileName Le nom unique du fichier tel que stocké.
     * @return Une ressource Spring pour le fichier.
     */

    public Resource loadFileAsResource(String storedFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("Fichier non trouvé " + storedFileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("Fichier non trouvé " + storedFileName, ex);
        }
    }

    // Supprime une pièce jointe en supprimant le fichier du système de fichiers
    // et en supprimant les métadonnées de la base de données.
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe non trouvée avec l'ID : " + attachmentId));
        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Files.deleteIfExists(filePath); // Supprime le fichier du système de fichiers
        } catch (IOException ex) {
            throw new FileStorageException("Impossible de supprimer le fichier du système de fichiers : " + attachment.getFileName(), ex);
        }

        attachmentRepository.delete(attachment); // Supprime les métadonnées de la base de données
    }

    public List<Attachment> getAttachmentsByPatientId(Long patientId) {
        return attachmentRepository.findByPatientId(patientId);
    }
}
