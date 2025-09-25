package com.main.projetstage.models; // Adjust package as needed

import jakarta.persistence.*;
import java.time.LocalDate; // Recommended for date-only fields
import java.util.Objects;

@Entity
@Table(name = "operation_recette") // Table name for the entity
public class OperationRecette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_recette") // Primary key column
    private Long numRecette; // Changed to Long for consistency with other IDs

    @Column(name = "date_recouv")
    private LocalDate dateRecouv; // Date of actual recovery/collection

    @Column(name = "date_valeur_op")
    private LocalDate dateValeurOP; // Value date of the operation

    @Column(name = "date_operation")
    private LocalDate dateOperation; // General date of the operation

    @Column(name = "montant_recette")
    private Float montantRecette; // Total amount of the collected revenue for this operation

    @Column(name = "provenance")
    private String provenance; // Origin or source related to this collection operation

    @Column(name = "etat_recette")
    private String etatRecette; // Current status of the collection operation

    @Column(name = "montant_majoration")
    private Float montantMajoration; // Total surcharges applied at the operation level

    @Column(name = "mode_paiement")
    private String modePaiement; // How the payment for this operation was made

    @Column(name = "observation", columnDefinition = "TEXT")
    private String observation; // Any notes or remarks about the operation

    // --- Relationship: Many-to-One (1,1 from OperationRecette to ArticleRecette) ---
    // An OperationRecette must always be linked to one ArticleRecette.
    // This is the owning side of the relationship, meaning the 'operation_recette'
    // table will have the foreign key column 'article_recette_id'.
    @ManyToOne(fetch = FetchType.EAGER, optional = false) // optional=false ensures 1,1 from this side
    @JoinColumn(name = "article_recette_id", nullable = false) // Foreign key to ArticleRecette
    private ArticleRecette articleRecette;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // optional=false enforces 1,1
    @JoinColumn(name = "id_contribuable", nullable = false) // Foreign key to Contribuable
    private Contribuable contribuable;


    // --- Constructors ---
    public OperationRecette() {
    }

    public OperationRecette(LocalDate dateRecouv, LocalDate dateValeurOP, LocalDate dateOperation,
                            Float montantRecette, String provenance, String etatRecette,
                            Float montantMajoration, String modePaiement, String observation,
                            ArticleRecette articleRecette) {
        this.dateRecouv = dateRecouv;
        this.dateValeurOP = dateValeurOP;
        this.dateOperation = dateOperation;
        this.montantRecette = montantRecette;
        this.provenance = provenance;
        this.etatRecette = etatRecette;
        this.montantMajoration = montantMajoration;
        this.modePaiement = modePaiement;
        this.observation = observation;
        this.articleRecette = articleRecette;
    }

    // --- Getters and Setters ---
    public Long getNumRecette() {
        return numRecette;
    }

    public void setNumRecette(Long numRecette) {
        this.numRecette = numRecette;
    }

    public LocalDate getDateRecouv() {
        return dateRecouv;
    }

    public void setDateRecouv(LocalDate dateRecouv) {
        this.dateRecouv = dateRecouv;
    }

    public LocalDate getDateValeurOP() {
        return dateValeurOP;
    }

    public void setDateValeurOP(LocalDate dateValeurOP) {
        this.dateValeurOP = dateValeurOP;
    }

    public LocalDate getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDate dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Float getMontantRecette() {
        return montantRecette;
    }

    public void setMontantRecette(Float montantRecette) {
        this.montantRecette = montantRecette;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getEtatRecette() {
        return etatRecette;
    }

    public void setEtatRecette(String etatRecette) {
        this.etatRecette = etatRecette;
    }

    public Float getMontantMajoration() {
        return montantMajoration;
    }

    public void setMontantMajoration(Float montantMajoration) {
        this.montantMajoration = montantMajoration;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public ArticleRecette getArticleRecette() {
        return articleRecette;
    }

    public void setArticleRecette(ArticleRecette articleRecette) {
        this.articleRecette = articleRecette;
    }

    public Contribuable getContribuable() {
        return contribuable;
    }

    public void setContribuable(Contribuable contribuable) {
        this.contribuable = contribuable;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationRecette that = (OperationRecette) o;
        return numRecette != null && Objects.equals(numRecette, that.numRecette);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numRecette);
    }

    // --- toString() for debugging ---
    @Override
    public String toString() {
        return "OperationRecette{" +
                "numRecette=" + numRecette +
                ", dateRecouv=" + dateRecouv +
                ", montantRecette=" + montantRecette +
                ", etatRecette='" + etatRecette + '\'' +
                '}';
    }
}