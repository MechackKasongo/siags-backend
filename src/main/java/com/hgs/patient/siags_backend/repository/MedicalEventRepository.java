package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.MedicalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {

}