package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    // Vous pouvez ajouter des méthodes de recherche personnalisées ici si nécessaire,
    // par exemple, pour trouver toutes les pièces jointes associées à un patient :
    List<Attachment> findByPatientId(Long patientId);

}