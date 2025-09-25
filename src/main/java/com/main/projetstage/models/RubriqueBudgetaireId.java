package com.main.projetstage.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // Marks this class as an embeddable component for an entity's ID
public class RubriqueBudgetaireId implements Serializable {

    @Column(name = "num_chapitre")
    private int numChapitre;

    @Column(name = "num_article")
    private int numArticle;

    @Column(name = "num_paragraphe")
    private int numParagraphe;

    @Column(name = "ligne_rubrique")
    private int ligneRubrique;

    // --- Constructors ---
    public RubriqueBudgetaireId() {
    }

    public RubriqueBudgetaireId(int numChapitre, int numArticle, int numParagraphe, int ligneRubrique) {
        this.numChapitre = numChapitre;
        this.numArticle = numArticle;
        this.numParagraphe = numParagraphe;
        this.ligneRubrique = ligneRubrique;
    }


    public int getNumChapitre() {
        return numChapitre;
    }

    public int getNumArticle() {
        return numArticle;
    }

    public int getNumParagraphe() {
        return numParagraphe;
    }

    public int getLigneRubrique() {
        return ligneRubrique;
    }

    // --- Essential for Composite Keys: equals() and hashCode() ---
    // These methods ensure that two RubriqueBudgetaireId objects are considered equal
    // if all their primary key fields are equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RubriqueBudgetaireId that = (RubriqueBudgetaireId) o;
        return numChapitre == that.numChapitre &&
                numArticle == that.numArticle &&
                numParagraphe == that.numParagraphe &&
                ligneRubrique == that.ligneRubrique;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numChapitre, numArticle, numParagraphe, ligneRubrique);
    }

    // --- toString() for debugging ---
    @Override
    public String toString() {
        return "RubriqueBudgetaireId{" +
                "numChapitre=" + numChapitre +
                ", numArticle=" + numArticle +
                ", numParagraphe=" + numParagraphe +
                ", ligneRubrique=" + ligneRubrique +
                '}';
    }
}
