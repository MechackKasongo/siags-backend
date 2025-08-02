package com.hgs.patient.siags_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName; // Nom original du fichier tel qu'uploadé

    @Column(nullable = false)
    private String fileType; // Type MIME du fichier (ex: application/pdf, image/jpeg)

    @Column(nullable = false)
    private long fileSize; // Taille du fichier en octets

    @Column(nullable = false, unique = true)
    private String storedFileName; // Nom unique du fichier tel que stocké sur le système de fichiers (ex: UUID.extension)

    @Column(nullable = false)
    private String filePath; // Chemin complet où le fichier est stocké sur le serveur

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false) // Clé étrangère vers l'entité Patient
    private Patient patient; // Lien vers le patient auquel cette pièce jointe appartient

    @Column(nullable = false)
    private LocalDateTime uploadDate; // Date et heure de l'upload

    @Column
    private String description; // Description facultative de la pièce jointe
}
