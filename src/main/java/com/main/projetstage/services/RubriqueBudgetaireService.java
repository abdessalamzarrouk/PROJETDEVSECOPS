package com.main.projetstage.services;

import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.models.RubriqueBudgetaireId;
import java.util.List;
import java.util.Optional;

public interface RubriqueBudgetaireService {
    List<RubriqueBudgetaire> findAllRubriquesBudgetaires();
    Optional<RubriqueBudgetaire> findRubriqueBudgetaireById(RubriqueBudgetaireId id);
    RubriqueBudgetaire saveRubriqueBudgetaire(RubriqueBudgetaire rubriqueBudgetaire);
    void deleteRubriqueBudgetaire(RubriqueBudgetaireId id);

    // New method for flexible filtering
    List<RubriqueBudgetaire> getFilteredRubriquesBudgetaires(
            Integer numChapitre, Integer numArticle, Integer numParagraphe, Integer ligneRubrique);
}