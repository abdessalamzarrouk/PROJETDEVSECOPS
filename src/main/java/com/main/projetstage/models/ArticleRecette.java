package com.main.projetstage.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate; // Recommended for dates without time
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article_recette")
public class ArticleRecette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_article_recette")
    private Long numArticleRecette; // Changed to Integer (Object type for nullability with IDENTITY)

    @Column(name = "type_ar", nullable = false) // Assuming typeAR is mandatory
    private String typeAR;

    @Column(name = "date_pec_ar")
    private LocalDate datePECAR; // Using LocalDate for date

    @Column(name = "date_exigibilite")
    private LocalDate dateExigibilite; // Using LocalDate for date

    @Column(name = "date_emission_ar")
    private LocalDate dateEmissionAR; // Using LocalDate for date

    @Column(name = "adresse_ar")
    private String adresseAR;

    @Column(name = "motif_ar")
    private String motifAR;

    @Column(name = "droits_simples_ar")
    private Float droitsSimplesAR; // Using Float as specified

    @Column(name = "montant_ar", nullable = false) // Assuming montantAR is mandatory
    private Float montantAR; // Using Float as specified

    @Column(name = "total_penalite")
    private Float totalPenalite; // Using Float as specified



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_bordereau") // This is the foreign key column in 'article_recette' table
    @JsonBackReference // Prevents infinite recursion during JSON serialization
    private Bordereau bordereau; // The Bordereau this article belongs to


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_poste")
    @JsonBackReference // Prevents infinite recursion during JSON serialization (if PosteComptable also has articles)
    private PosteComptable posteComptable;

    @ManyToOne
    @JoinColumn(name = "nature_impot_code")
    private NatureImpot natureImpot;

    @OneToOne(mappedBy = "articleRecette", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PropositionNonValeur propositionNonValeur;

    @OneToMany(mappedBy = "articleRecette", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OperationRecette> operationsRecette = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "ligne_rubrique"),
            @JoinColumn(name = "num_article"),
            @JoinColumn(name = "num_chapitre"),
            @JoinColumn(name = "num_paragraphe")

    })
    private RubriqueBudgetaire rubriqueBudgetaire;

    @Enumerated(EnumType.STRING) // Stocke les noms des enums comme des chaînes de caractères dans la DB
    @Column(name = "status_ar", nullable = false)
    private ArticleRecetteStatus statusAR = ArticleRecetteStatus.EN_ATTENTE; // Statut par défaut de l'article

    @Column(name = "notes_traitement_ar", length = 500) // Notes facultatives pour le traitement
    private String notesTraitementAR;



    // --- Constructors ---
    public ArticleRecette() {
    }

    public ArticleRecette(String typeAR, LocalDate datePECAR, LocalDate dateExigibilite,
                          LocalDate dateEmissionAR, String adresseAR, String motifAR,
                          Float droitsSimplesAR, Float montantAR, Float totalPenalite,
                          Bordereau bordereau, PosteComptable posteComptable) {
        this.typeAR = typeAR;
        this.datePECAR = datePECAR;
        this.dateExigibilite = dateExigibilite;
        this.dateEmissionAR = dateEmissionAR;
        this.adresseAR = adresseAR;
        this.motifAR = motifAR;
        this.droitsSimplesAR = droitsSimplesAR;
        this.montantAR = montantAR;
        this.totalPenalite = totalPenalite;
        this.bordereau = bordereau;
        this.posteComptable = posteComptable;
    }

    public ArticleRecette( Float montantAR,String typeAR) {
        this.typeAR = typeAR;
        this.montantAR = montantAR;
        // Other fields will be null
    }
    // --- Getters and Setters for all attributes ---
    public Long getNumArticleRecette() {
        return numArticleRecette;
    }

    public void setNumArticleRecette(Long numArticleRecette) {
        this.numArticleRecette = numArticleRecette;
    }

    public String getTypeAR() {
        return typeAR;
    }

    public void setTypeAR(String typeAR) {
        this.typeAR = typeAR;
    }

    public LocalDate getDatePECAR() {
        return datePECAR;
    }

    public void setDatePECAR(LocalDate datePECAR) {
        this.datePECAR = datePECAR;
    }

    public LocalDate getDateExigibilite() {
        return dateExigibilite;
    }

    public void setDateExigibilite(LocalDate dateExigibilite) {
        this.dateExigibilite = dateExigibilite;
    }

    public LocalDate getDateEmissionAR() {
        return dateEmissionAR;
    }

    public void setDateEmissionAR(LocalDate dateEmissionAR) {
        this.dateEmissionAR = dateEmissionAR;
    }

    public String getAdresseAR() {
        return adresseAR;
    }

    public void setAdresseAR(String adresseAR) {
        this.adresseAR = adresseAR;
    }

    public String getMotifAR() {
        return motifAR;
    }

    public void setMotifAR(String motifAR) {
        this.motifAR = motifAR;
    }

    public Float getDroitsSimplesAR() {
        return droitsSimplesAR;
    }

    public void setDroitsSimplesAR(Float droitsSimplesAR) {
        this.droitsSimplesAR = droitsSimplesAR;
    }

    public Float getMontantAR() {
        return montantAR;
    }

    public void setMontantAR(Float montantAR) {
        this.montantAR = montantAR;
    }

    public Float getTotalPenalite() {
        return totalPenalite;
    }

    public void setTotalPenalite(Float totalPenalite) {
        this.totalPenalite = totalPenalite;
    }

    public Bordereau getBordereau() {
        return bordereau;
    }

    public void setBordereau(Bordereau bordereau) {
        this.bordereau = bordereau;
    }

    public PosteComptable getPosteComptable() {
        return posteComptable;
    }

    public void setPosteComptable(PosteComptable posteComptable) {
        this.posteComptable = posteComptable;
    }

    public void setNatureImpot(NatureImpot natureImpot) {
        this.natureImpot = natureImpot;
    }

    public NatureImpot getNatureImpot() {
        return this.natureImpot;
    }

    public PropositionNonValeur getPropositionNonValeur() {
        return propositionNonValeur;
    }

    public void setPropositionNonValeur(PropositionNonValeur propositionNonValeur) {
        this.propositionNonValeur = propositionNonValeur;
    }
    // Getters et Setters pour les nouveaux champs
    public ArticleRecetteStatus getStatusAR() { return statusAR; }
    public void setStatusAR(ArticleRecetteStatus statusAR) { this.statusAR = statusAR; }
    public String getNotesTraitementAR() { return notesTraitementAR; }
    public void setNotesTraitementAR(String notesTraitementAR) { this.notesTraitementAR = notesTraitementAR; }

    // --- NEW: Getters and Setters for operationsRecette List ---
    public List<OperationRecette> getOperationsRecette() {
        return operationsRecette;
    }

    public void setOperationsRecette(List<OperationRecette> operationsRecette) {
        this.operationsRecette = operationsRecette;
    }

    public RubriqueBudgetaire getRubriqueBudgetaire(){
        return this.rubriqueBudgetaire;
    }

    public void setRubriqueBudgetaire(RubriqueBudgetaire rubriqueBudgetaire){
        this.rubriqueBudgetaire = rubriqueBudgetaire;
    }

    // --- NEW: Helper methods for managing the bidirectional relationship with OperationRecette ---
    public void addOperationRecette(OperationRecette operation) {
        if (operation != null && !this.operationsRecette.contains(operation)) {
            this.operationsRecette.add(operation);
            operation.setArticleRecette(this); // Crucial: set the many-to-one side
        }
    }

    public void removeOperationRecette(OperationRecette operation) {
        if (operation != null && this.operationsRecette.remove(operation)) {
            operation.setArticleRecette(null); // Crucial: remove the many-to-one side reference
        }
    }

    @Override
    public String toString() {
        return "ArticleRecette{" +
                "numArticleRecette=" + numArticleRecette +
                ", typeAR='" + typeAR + '\'' +
                ", datePECAR=" + datePECAR +
                ", dateExigibilite=" + dateExigibilite +
                ", dateEmissionAR=" + dateEmissionAR +
                ", adresseAR='" + adresseAR + '\'' +
                ", motifAR='" + motifAR + '\'' +
                ", droitsSimplesAR=" + droitsSimplesAR +
                ", montantAR=" + montantAR +
                ", totalPenalite=" + totalPenalite +
                ", bordereauCode=" + (bordereau != null ? bordereau.getCodeBordereau() : "null") +
                ", posteComptableCode=" + (posteComptable != null ? posteComptable.getCodePoste() : "null") +
                ", natureImpot" + (natureImpot != null ? natureImpot.getCodeImpot() : "null") +
                '}';
    }
}