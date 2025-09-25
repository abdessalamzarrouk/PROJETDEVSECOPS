package com.main.projetstage.specifications; // Create a new package for specifications

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ArticleRecetteSpecifications {

    public static Specification<ArticleRecette> hasNumArticleRecette(Long numArticleRecette) {
        return (root, query, cb) -> numArticleRecette == null ? null : cb.equal(root.get("numArticleRecette"), numArticleRecette);
    }

    public static Specification<ArticleRecette> hasTypeAR(String typeAR) {
        return (root, query, cb) -> typeAR == null || typeAR.isEmpty() ? null : cb.like(cb.lower(root.get("typeAR")), "%" + typeAR.toLowerCase() + "%");
    }

    public static Specification<ArticleRecette> hasStatusAR(ArticleRecetteStatus statusAR) {
        return (root, query, cb) -> statusAR == null ? null : cb.equal(root.get("statusAR"), statusAR);
    }

    public static Specification<ArticleRecette> hasCodeBordereau(String codeBordereau) {
        return (root, query, cb) -> {
            if (codeBordereau == null || codeBordereau.isEmpty()) {
                return null;
            }
            // If codeBordereau is a Long in your Bordereau entity, you cannot use like().
            // You need to parse the input string to a Long and then use cb.equal().
            // Handle potential NumberFormatException if the input string isn't a valid Long.
            try {
                Long codeBordereauLong = Long.parseLong(codeBordereau);
                return cb.equal(root.join("bordereau", JoinType.INNER).get("codeBordereau"), codeBordereauLong);
            } catch (NumberFormatException e) {
                // Handle cases where the input string for a Long field is not a valid number.
                // For example, return a predicate that matches nothing, or log the error.
                // For now, we'll return a false predicate to ensure no results match an invalid input.
                return cb.disjunction(); // Represents 'OR FALSE', effectively matching nothing
            }
        };
    }

    public static Specification<ArticleRecette> hasIntitulePosteComptable(String intitulePoste) {
        return (root, query, cb) -> {
            if (intitulePoste == null || intitulePoste.isEmpty()) {
                return null;
            }
            // Join with PosteComptable entity
            return cb.like(cb.lower(root.join("posteComptable", JoinType.INNER).get("intitulePoste")), "%" + intitulePoste.toLowerCase() + "%");
        };
    }

    public static Specification<ArticleRecette> hasDateEmissionBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate != null && endDate != null) {
                return cb.between(root.get("dateEmissionAR"), startDate, endDate);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("dateEmissionAR"), startDate);
            }
            // If only endDate is provided, filter until endDate
            return cb.lessThanOrEqualTo(root.get("dateEmissionAR"), endDate);
        };
    }

    public static Specification<ArticleRecette> hasDatePECBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate != null && endDate != null) {
                return cb.between(root.get("datePECAR"), startDate, endDate);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("datePECAR"), startDate);
            }
            // If only endDate is provided, filter until endDate
            return cb.lessThanOrEqualTo(root.get("datePECAR"), endDate);
        };
    }
}