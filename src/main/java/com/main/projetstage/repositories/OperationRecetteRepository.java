package com.main.projetstage.repositories;

import com.main.projetstage.models.OperationRecette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OperationRecetteRepository extends JpaRepository<OperationRecette, Long> {
    // Custom query to find operations within a date range
    List<OperationRecette> findByDateOperationBetween(LocalDate startDate, LocalDate endDate);

    // Custom queries for partial date ranges
    @Query("SELECT o FROM OperationRecette o WHERE o.dateOperation >= :startDate")
    List<OperationRecette> findByDateOperationAfterOrEqual(@Param("startDate") LocalDate startDate);

    @Query("SELECT o FROM OperationRecette o WHERE o.dateOperation <= :endDate")
    List<OperationRecette> findByDateOperationBeforeOrEqual(@Param("endDate") LocalDate endDate);
}