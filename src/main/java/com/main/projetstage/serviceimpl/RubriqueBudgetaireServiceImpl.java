package com.main.projetstage.serviceimpl;

import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.models.RubriqueBudgetaireId;
import com.main.projetstage.repositories.RubriqueBudgetaireRepository;
import com.main.projetstage.services.RubriqueBudgetaireService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification; // Import for Specification API

import java.util.List;
import java.util.Optional;
import jakarta.persistence.criteria.Predicate; // For Specification API

@Service
public class RubriqueBudgetaireServiceImpl implements RubriqueBudgetaireService {

    private final RubriqueBudgetaireRepository rubriqueBudgetaireRepository;

    public RubriqueBudgetaireServiceImpl(RubriqueBudgetaireRepository rubriqueBudgetaireRepository) {
        this.rubriqueBudgetaireRepository = rubriqueBudgetaireRepository;
    }

    @Override
    public List<RubriqueBudgetaire> findAllRubriquesBudgetaires() {
        return rubriqueBudgetaireRepository.findAll();
    }

    @Override
    public Optional<RubriqueBudgetaire> findRubriqueBudgetaireById(RubriqueBudgetaireId id) {
        return rubriqueBudgetaireRepository.findById(id);
    }

    @Override
    @Transactional
    public RubriqueBudgetaire saveRubriqueBudgetaire(RubriqueBudgetaire rubriqueBudgetaire) {
        return rubriqueBudgetaireRepository.save(rubriqueBudgetaire);
    }

    @Override
    @Transactional
    public void deleteRubriqueBudgetaire(RubriqueBudgetaireId id) {
        rubriqueBudgetaireRepository.deleteById(id);
    }

    @Override
    public List<RubriqueBudgetaire> getFilteredRubriquesBudgetaires(
            Integer numChapitre, Integer numArticle, Integer numParagraphe, Integer ligneRubrique) {

        // Using Spring Data JPA's Specification API for more flexible and maintainable filtering
        // This avoids complex if-else if chains for multiple optional parameters.
        Specification<RubriqueBudgetaire> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction(); // Start with a true predicate

            if (numChapitre != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id").get("numChapitre"), numChapitre));
            }
            if (numArticle != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id").get("numArticle"), numArticle));
            }
            if (numParagraphe != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id").get("numParagraphe"), numParagraphe));
            }
            if (ligneRubrique != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id").get("ligneRubrique"), ligneRubrique));
            }

            return predicate;
        };

        return rubriqueBudgetaireRepository.findAll(spec);
    }
}