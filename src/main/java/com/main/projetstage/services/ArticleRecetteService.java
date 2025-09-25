package com.main.projetstage.services;

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus;
import com.main.projetstage.models.PosteComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface ArticleRecetteService {

    Page<ArticleRecette> getFilteredArticlesRecette(
            Long numArticleRecette,
            String typeAR,
            ArticleRecetteStatus statusAR,
            String codeBordereau,
            String intitulePoste,
            LocalDate dateEmissionStart,
            LocalDate dateEmissionEnd,
            LocalDate datePECStart,
            LocalDate datePECEnd,
            Pageable pageable);

    List<ArticleRecette> findAllArticlesRecette();
    Optional<ArticleRecette> findArticleRecetteById(Long id);
    ArticleRecette saveArticleRecette(ArticleRecette articleRecette);
    void deleteArticleRecette(Long id);
    // Méthode pour mettre à jour le statut d'un article de recette
    ArticleRecette updateArticleRecetteStatus(Long id, ArticleRecetteStatus status, String notes);
    // Méthode pour mettre à jour les statuts de tous les articles d'un bordereau
    void updateArticlesStatusForBordereau(Long bordereauCode, ArticleRecetteStatus status);

    void updateArticlesStatusAndDatePecForBordereau(Long codeBordereau, ArticleRecetteStatus articleRecetteStatus, LocalDate validationDate, PosteComptable posteComptable);

    public List<ArticleRecette> findArticlesByDatePECRange(LocalDate startDate, LocalDate endDate);
}