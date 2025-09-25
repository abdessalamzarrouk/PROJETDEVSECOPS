package com.main.projetstage.repositories;

import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.models.RubriqueBudgetaireId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RubriqueBudgetaireRepository extends JpaRepository<RubriqueBudgetaire, RubriqueBudgetaireId>, JpaSpecificationExecutor<RubriqueBudgetaire> {
    // JpaSpecificationExecutor is added for more flexible, dynamic filtering later if needed.

    // Spring Data JPA can derive queries from method names for properties of embedded IDs
    // The format is `findById_PropertyName`
    List<RubriqueBudgetaire> findById_NumChapitre(int numChapitre);
    List<RubriqueBudgetaire> findById_NumChapitreAndId_NumArticle(int numChapitre, int numArticle);
    List<RubriqueBudgetaire> findById_NumChapitreAndId_NumArticleAndId_NumParagraphe(int numChapitre, int numArticle, int numParagraphe);
    List<RubriqueBudgetaire> findById_NumChapitreAndId_NumArticleAndId_NumParagrapheAndId_LigneRubrique(int numChapitre, int numArticle, int numParagraphe, int ligneRubrique);

    // You can also add custom queries with @Query annotation if method name derivation becomes too complex
    // @Query("SELECT r FROM RubriqueBudgetaire r WHERE (:chapitre is null or r.id.numChapitre = :chapitre)")
    // List<RubriqueBudgetaire> findByOptionalChapitre(@Param("chapitre") Integer chapitre);
}