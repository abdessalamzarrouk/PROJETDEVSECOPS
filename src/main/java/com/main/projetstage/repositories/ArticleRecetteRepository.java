package com.main.projetstage.repositories;

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface ArticleRecetteRepository extends JpaRepository<ArticleRecette, Long>, JpaSpecificationExecutor<ArticleRecette> {

    List<ArticleRecette> findByBordereau_CodeBordereau(Long codeBordereau);

    // NEW METHOD: Find articles by their status
    List<ArticleRecette> findByStatusAR(ArticleRecetteStatus status);

    // @EntityGraph ensures that 'bordereau', 'rubriqueBudgetaire', and 'natureImpot' are fetched
    // along with ArticleRecette to prevent LazyInitializationException when accessing them
    // in the HTML template outside a transactional context.
    @EntityGraph(attributePaths = {"bordereau", "rubriqueBudgetaire", "natureImpot"})
    List<ArticleRecette> findByDatePECARBetween(LocalDate startDate, LocalDate endDate);
}