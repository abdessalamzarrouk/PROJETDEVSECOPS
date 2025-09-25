package com.main.projetstage.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "proposition_non_valeur") // Database table name
public class PropositionNonValeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for the entity

    @Column(name = "num_prop", nullable = false, unique = true)
    private int numProp; // Unique proposition number

    @Column(name = "annee_emission_pnv", nullable = false)
    private int anneeEmissionPNV; // Year of PNV emission

    @Column(name = "date_envoi_pnv") // Date PNV was sent
    private LocalDate dateEnvoiPNV; // Use LocalDate for dates

    @Column(name = "date_recept_pnv") // Date PNV was received
    private LocalDate dateReceptPNV; // Use LocalDate for dates

    @Column(name = "etat_pnv") // Status of the PNV
    private String etatPNV;

    @Column(name = "montant_pnv") // Amount of the PNV
    private Float montantPNV;

    @Column(name = "motif_pnv", columnDefinition = "TEXT") // Reason for the PNV
    private String motifPNV;

    // --- Relationship: One-to-One (1,1) with ArticleRecette ---
    // This defines the owning side of the OneToOne relationship.
    // 'optional = false' ensures that every PropositionNonValeur MUST have an ArticleRecette.
    // 'nullable = false' on @JoinColumn enforces this at the database level.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_recette_id", nullable = false, unique = true) // Foreign key to ArticleRecette
    private ArticleRecette articleRecette;

    // --- Constructors ---
    public PropositionNonValeur() {
    }

    public PropositionNonValeur(int numProp, int anneeEmissionPNV, LocalDate dateEnvoiPNV, LocalDate dateReceptPNV,
                                String etatPNV, Float montantPNV, String motifPNV, ArticleRecette articleRecette) {
        this.numProp = numProp;
        this.anneeEmissionPNV = anneeEmissionPNV;
        this.dateEnvoiPNV = dateEnvoiPNV;
        this.dateReceptPNV = dateReceptPNV;
        this.etatPNV = etatPNV;
        this.montantPNV = montantPNV;
        this.motifPNV = motifPNV;
        this.articleRecette = articleRecette;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumProp() {
        return numProp;
    }

    public void setNumProp(int numProp) {
        this.numProp = numProp;
    }

    public int getAnneeEmissionPNV() {
        return anneeEmissionPNV;
    }

    public void setAnneeEmissionPNV(int anneeEmissionPNV) {
        this.anneeEmissionPNV = anneeEmissionPNV;
    }

    public LocalDate getDateEnvoiPNV() {
        return dateEnvoiPNV;
    }

    public void setDateEnvoiPNV(LocalDate dateEnvoiPNV) {
        this.dateEnvoiPNV = dateEnvoiPNV;
    }

    public LocalDate getDateReceptPNV() {
        return dateReceptPNV;
    }

    public void setDateReceptPNV(LocalDate dateReceptPNV) {
        this.dateReceptPNV = dateReceptPNV;
    }

    public String getEtatPNV() {
        return etatPNV;
    }

    public void setEtatPNV(String etatPNV) {
        this.etatPNV = etatPNV;
    }

    public Float getMontantPNV() {
        return montantPNV;
    }

    public void setMontantPNV(Float montantPNV) {
        this.montantPNV = montantPNV;
    }

    public String getMotifPNV() {
        return motifPNV;
    }

    public void setMotifPNV(String motifPNV) {
        this.motifPNV = motifPNV;
    }

    public ArticleRecette getArticleRecette() {
        return articleRecette;
    }

    public void setArticleRecette(ArticleRecette articleRecette) {
        this.articleRecette = articleRecette;
    }
}