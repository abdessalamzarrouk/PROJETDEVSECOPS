package com.main.projetstage.serviceimpl; // Ou com.main.projetstage.services

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus;
import com.main.projetstage.models.PosteComptable;
import com.main.projetstage.repositories.ArticleRecetteRepository;
import com.main.projetstage.services.ArticleRecetteService;
import com.main.projetstage.specifications.ArticleRecetteSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // C'est l'annotation cruciale pour que Spring le reconnaisse comme un "bean"
public class ArticleRecetteServiceImpl implements ArticleRecetteService {

    private final ArticleRecetteRepository articleRecetteRepository;

    @Autowired
    public ArticleRecetteServiceImpl(ArticleRecetteRepository articleRecetteRepository) {
        this.articleRecetteRepository = articleRecetteRepository;
    }

    @Override
    public List<ArticleRecette> findAllArticlesRecette() {
        return articleRecetteRepository.findAll();
    }

    @Override
    public Optional<ArticleRecette> findArticleRecetteById(Long id) {
        return articleRecetteRepository.findById(id);
    }

    @Override
    @Transactional
    public ArticleRecette saveArticleRecette(ArticleRecette articleRecette) {
        return articleRecetteRepository.save(articleRecette);
    }

    @Override
    @Transactional
    public void deleteArticleRecette(Long id) {
        articleRecetteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ArticleRecette updateArticleRecetteStatus(Long id, ArticleRecetteStatus status, String notes) {
        Optional<ArticleRecette> articleOptional = articleRecetteRepository.findById(id);
        if (articleOptional.isPresent()) {
            ArticleRecette article = articleOptional.get();
            article.setStatusAR(status);
            article.setNotesTraitementAR(notes);
            return articleRecetteRepository.save(article);
        }
        throw new RuntimeException("Article de recette non trouvé pour la mise à jour du statut: " + id);
    }

    @Override
    @Transactional
    public void updateArticlesStatusForBordereau(Long bordereauCode, ArticleRecetteStatus status) {
        List<ArticleRecette> articles = articleRecetteRepository.findByBordereau_CodeBordereau(bordereauCode);
        for (ArticleRecette article : articles) {
            article.setStatusAR(status);
            // Si le bordereau est rejeté, vous pouvez ajouter une note par défaut aux articles ou laisser vide
            if (status.equals(ArticleRecetteStatus.REJETE)) {
                article.setNotesTraitementAR("Rejeté suite au rejet du bordereau parent.");
            } else if (status.equals(ArticleRecetteStatus.VALIDE)) {
                article.setNotesTraitementAR("Validé suite à la validation du bordereau parent.");
            } else {
                article.setNotesTraitementAR(null); // Réinitialise les notes si en attente ou autre
            }
            articleRecetteRepository.save(article);
        }
    }

    @Override
    @Transactional
    public void updateArticlesStatusAndDatePecForBordereau(Long codeBordereau, ArticleRecetteStatus status, LocalDate datePec, PosteComptable posteComptable) {
        List<ArticleRecette> articles = articleRecetteRepository.findByBordereau_CodeBordereau(codeBordereau);
        for (ArticleRecette article : articles) {
            article.setStatusAR(status);
            article.setDatePECAR(datePec);// Set the date_pec here
            article.setPosteComptable(posteComptable);
        }
        articleRecetteRepository.saveAll(articles);
    }
    public List<ArticleRecette> findArticlesByDatePECRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            // If no dates are provided, return all articles
            return articleRecetteRepository.findAll();
        } else if (startDate != null && endDate == null) {
            // If only start date is provided, find from start date to today/future
            // For 'between' to work, provide a very late date
            return articleRecetteRepository.findByDatePECARBetween(startDate, LocalDate.MAX);
        } else if (startDate == null && endDate != null) {
            // If only end date is provided, find from very early date to end date
            // For 'between' to work, provide a very early date
            return articleRecetteRepository.findByDatePECARBetween(LocalDate.MIN, endDate);
        } else {
            // Both start and end dates are provided
            return articleRecetteRepository.findByDatePECARBetween(startDate, endDate);
        }
    }

    @Override
    public Page<ArticleRecette> getFilteredArticlesRecette(
            Long numArticleRecette,
            String typeAR,
            ArticleRecetteStatus statusAR,
            String codeBordereau,
            String intitulePoste,
            LocalDate dateEmissionStart,
            LocalDate dateEmissionEnd,
            LocalDate datePECStart,
            LocalDate datePECEnd,
            Pageable pageable) {

        // Use a List to collect specifications and then combine them
        List<Specification<ArticleRecette>> specs = new ArrayList<>();

        if (numArticleRecette != null) {
            specs.add(ArticleRecetteSpecifications.hasNumArticleRecette(numArticleRecette));
        }
        if (typeAR != null && !typeAR.isEmpty()) {
            specs.add(ArticleRecetteSpecifications.hasTypeAR(typeAR));
        }
        if (statusAR != null) {
            specs.add(ArticleRecetteSpecifications.hasStatusAR(statusAR));
        }
        if (codeBordereau != null && !codeBordereau.isEmpty()) {
            specs.add(ArticleRecetteSpecifications.hasCodeBordereau(codeBordereau));
        }
        if (intitulePoste != null && !intitulePoste.isEmpty()) {
            specs.add(ArticleRecetteSpecifications.hasIntitulePosteComptable(intitulePoste));
        }
        if (dateEmissionStart != null || dateEmissionEnd != null) {
            specs.add(ArticleRecetteSpecifications.hasDateEmissionBetween(dateEmissionStart, dateEmissionEnd));
        }
        if (datePECStart != null || datePECEnd != null) {
            specs.add(ArticleRecetteSpecifications.hasDatePECBetween(datePECStart, datePECEnd));
        }

        // Combine all specifications.
        // If the list is empty, it means no filters were applied, so we return all results.
        // Otherwise, combine them using `and()`.
        Specification<ArticleRecette> combinedSpec = specs.stream()
                .reduce(Specification::and)
                .orElse(null); // If no specs, return null to signify no filter

        // If combinedSpec is null, findAll(Specification, Pageable) will effectively behave like findAll(Pageable)
        return articleRecetteRepository.findAll(combinedSpec, pageable);
    }



}