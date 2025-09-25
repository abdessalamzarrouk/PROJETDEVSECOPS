package com.main.projetstage.models;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "rubrique_budgetaire")
public class RubriqueBudgetaire {

    @EmbeddedId
    private RubriqueBudgetaireId id;

    @Column(name = "libelle_rubrique")
    private String libelleRubrique;

    // --- Constructors ---
    public RubriqueBudgetaire() {
    }

    public RubriqueBudgetaire(RubriqueBudgetaireId id, String libelleRubrique) {
        this.id = id;
        this.libelleRubrique = libelleRubrique;
    }

    public RubriqueBudgetaire(int numChapitre, int numArticle, int numParagraphe, int ligneRubrique, String libelleRubrique) {
        this.id = new RubriqueBudgetaireId(numChapitre, numArticle, numParagraphe, ligneRubrique);
        this.libelleRubrique = libelleRubrique;
    }

    // --- Getters and Setters ---
    public RubriqueBudgetaireId getId() {
        return id;
    }

    public void setId(RubriqueBudgetaireId id) {
        this.id = id;
    }

    public String getLibelleRubrique() {
        return libelleRubrique;
    }

    public void setLibelleRubrique(String libelleRubrique) {
        this.libelleRubrique = libelleRubrique;
    }

    public int getNumChapitre() {
        return id != null ? id.getNumChapitre() : 0; // Return 0 or throw exception if ID is null
    }

    public int getNumArticle() {
        return id != null ? id.getNumArticle() : 0;
    }

    public int getNumParagraphe() {
        return id != null ? id.getNumParagraphe() : 0;
    }

    public int getLigneRubrique() {
        return id != null ? id.getLigneRubrique() : 0;
    }

    // --- Essential for Entities: equals() and hashCode() ---
    // These should typically use the ID for comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RubriqueBudgetaire that = (RubriqueBudgetaire) o;
        // For entities with @EmbeddedId, rely on the embedded ID's equals method
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString() for debugging ---
    @Override
    public String toString() {
        return "RubriqueBudgetaire{" +
                "id=" + id +
                ", libelleRubrique='" + libelleRubrique + '\'' +
                '}';
    }
}
