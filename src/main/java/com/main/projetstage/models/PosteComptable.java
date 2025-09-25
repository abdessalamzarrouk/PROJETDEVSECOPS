package com.main.projetstage.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poste_comptable")
public class PosteComptable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codePoste; // Changed from 'id' to 'codePoste' as per your update

    @Column(name = "intitule_poste", nullable = false)
    private String intitulePoste;

    @Column(name = "ville_poste")
    private String villePoste;

    @Column(name = "province_poste")
    private String provincePoste;

    @Column(name = "date_mise_oeuvre")
    private String dateMiseOeuvre; // Keeping as String as per your definition

    @Column(name = "date_fin_service")
    private String dateFinService; // Keeping as String as per your definition

    // Existing One-to-Many with ArticleRecette (updated name here to match your provided list name)
    @OneToMany(mappedBy = "posteComptable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleRecette> articlesRecette = new ArrayList<>(); // Changed type to ArticleRecette

    // --- ONE-TO-MANY RELATIONSHIP WITH Fonctionnaire ---
    @OneToMany(mappedBy = "posteComptable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fonctionnaire> fonctionnaires = new ArrayList<>();


    // --- Constructors ---
    public PosteComptable() {
    }

    // Updated constructor to match new fields
    public PosteComptable(String intitulePoste, String villePoste, String provincePoste,
                          String dateMiseOeuvre, String dateFinService) {
        this.intitulePoste = intitulePoste;
        this.villePoste = villePoste;
        this.provincePoste = provincePoste;
        this.dateMiseOeuvre = dateMiseOeuvre;
        this.dateFinService = dateFinService;
    }

    // --- Getters and Setters (updated for new fields) ---

    public Long getCodePoste() { // Updated getter name
        return codePoste;
    }

    public void setCodePoste(Long codePoste) { // Updated setter name
        this.codePoste = codePoste;
    }

    public String getIntitulePoste() {
        return intitulePoste;
    }

    public void setIntitulePoste(String intitulePoste) {
        this.intitulePoste = intitulePoste;
    }

    public String getVillePoste() {
        return villePoste;
    }

    public void setVillePoste(String villePoste) {
        this.villePoste = villePoste;
    }

    public String getProvincePoste() {
        return provincePoste;
    }

    public void setProvincePoste(String provincePoste) {
        this.provincePoste = provincePoste;
    }

    public String getDateMiseOeuvre() {
        return dateMiseOeuvre;
    }

    public void setDateMiseOeuvre(String dateMiseOeuvre) {
        this.dateMiseOeuvre = dateMiseOeuvre;
    }

    public String getDateFinService() {
        return dateFinService;
    }

    public void setDateFinService(String dateFinService) {
        this.dateFinService = dateFinService;
    }

    // Existing methods for articlesRecette (updated list name)
    public List<ArticleRecette> getArticlesRecette() {
        return articlesRecette;
    }

    public void setArticlesRecette(List<ArticleRecette> articlesRecette) {
        this.articlesRecette = articlesRecette;
    }

    // Getters/Setters for fonctionnaires
    public List<Fonctionnaire> getFonctionnaires() {
        return fonctionnaires;
    }

    public void setFonctionnaires(List<Fonctionnaire> fonctionnaires) {
        this.fonctionnaires = fonctionnaires;
    }

    // --- Helper methods for managing the bidirectional relationship with Fonctionnaire ---
    public void addFonctionnaire(Fonctionnaire fonctionnaire) {
        fonctionnaires.add(fonctionnaire);
        fonctionnaire.setPosteComptable(this); // Crucial: set the many-to-one side
    }

    public void removeFonctionnaire(Fonctionnaire fonctionnaire) {
        fonctionnaires.remove(fonctionnaire);
        fonctionnaire.setPosteComptable(null); // Crucial: remove the many-to-one side reference
    }

    // --- Helper methods for managing the bidirectional relationship with ArticleRecette ---
    // Make sure ArticleRecette also has a setPosteComptable method
    public void addArticleRecette(ArticleRecette article) {
        articlesRecette.add(article);
        article.setPosteComptable(this);
    }

    public void removeArticleRecette(ArticleRecette article) {
        articlesRecette.remove(article);
        article.setPosteComptable(null);
    }


    @Override
    public String toString() {
        return "PosteComptable{" +
                "codePoste=" + codePoste +
                ", intitulePoste='" + intitulePoste + '\'' +
                ", villePoste='" + villePoste + '\'' +
                ", provincePoste='" + provincePoste + '\'' +
                ", dateMiseOeuvre='" + dateMiseOeuvre + '\'' +
                ", dateFinService='" + dateFinService + '\'' +
                // Avoid printing collections to prevent infinite recursion and improve performance
                '}';
    }
}