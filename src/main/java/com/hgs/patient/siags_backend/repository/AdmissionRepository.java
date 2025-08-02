package com.hgs.patient.siags_backend.repository;

import com.hgs.patient.siags_backend.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hgs.patient.siags_backend.dto.AdmissionCountByDepartmentDTO;
import com.hgs.patient.siags_backend.dto.MonthlyAdmissionCountDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // N'oubliez pas cette importation si vous utilisez @Param

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    List<Admission> findByPatientId(Long patientId);

    List<Admission> findByPatientIdAndAdmissionDateBetween(Long patientId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.hgs.patient.siags_backend.dto.AdmissionCountByDepartmentDTO(a.assignedDepartment.name, COUNT(a.id)) " +
            "FROM Admission a GROUP BY a.assignedDepartment.name")
    List<AdmissionCountByDepartmentDTO> countAdmissionsByDepartment();

    @Query("SELECT new com.hgs.patient.siags_backend.dto.MonthlyAdmissionCountDTO(CAST(FUNCTION('MONTH', a.admissionDate) AS int), COUNT(a.id)) " +
            "FROM Admission a WHERE FUNCTION('YEAR', a.admissionDate) = :year GROUP BY FUNCTION('MONTH', a.admissionDate) ORDER BY FUNCTION('MONTH', a.admissionDate)")
    List<MonthlyAdmissionCountDTO> countAdmissionsByMonth(@Param("year") int year);

    // Votre nouvelle méthode, qui est correcte
    @Query("SELECT COUNT(a) FROM Admission a WHERE a.admissionDate BETWEEN :startDate AND :endDate")
    Long countAdmissionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    // Ou si vous préférez une méthode dérivée de requête (sans @Query):
    // Long countByAdmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}