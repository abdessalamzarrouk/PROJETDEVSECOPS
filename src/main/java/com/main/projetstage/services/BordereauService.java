package com.main.projetstage.services;

import com.main.projetstage.models.Bordereau;
import java.util.List;
import java.util.Optional;

public interface BordereauService {
    List<Bordereau> findAllBordereaux();
    Optional<Bordereau> findBordereauByCode(Long codeBordereau);
    Bordereau saveBordereau(Bordereau bordereau);
    void deleteBordereau(Long codeBordereau);
    // Méthode pour mettre à jour le statut et la raison de rejet d'un bordereau
    Bordereau updateBordereauStatus(Long codeBordereau, String status, String raisonRejet);
}