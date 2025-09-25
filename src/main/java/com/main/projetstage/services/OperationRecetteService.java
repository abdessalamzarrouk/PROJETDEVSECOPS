package com.main.projetstage.services;

import com.main.projetstage.models.OperationRecette;
import com.main.projetstage.repositories.OperationRecetteRepository; // You'll need to create this

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class OperationRecetteService {

    private final OperationRecetteRepository operationRecetteRepository;

    public OperationRecetteService(OperationRecetteRepository operationRecetteRepository) {
        this.operationRecetteRepository = operationRecetteRepository;
    }

    // Example method to find operations by date range
    public List<OperationRecette> findOperationsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return operationRecetteRepository.findByDateOperationBetween(startDate, endDate);
        } else if (startDate != null) {
            return operationRecetteRepository.findByDateOperationAfterOrEqual(startDate);
        } else if (endDate != null) {
            return operationRecetteRepository.findByDateOperationBeforeOrEqual(endDate);
        } else {
            return operationRecetteRepository.findAll();
        }
    }

    // You can add other methods here as needed, e.g., findById, save, etc.
}