package com.main.projetstage.models;

import jakarta.persistence.*;
import java.time.LocalDate; // Recommended for dates without time
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fonctionnaire")
public class Fonctionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_personne", nullable = false)
    private String nomPersonne;

    @Column(name = "prenom_personne", nullable = false)
    private String prenomPersonne;

    @Column(name = "date_debut_service")
    private LocalDate dateDebutService; // Using LocalDate for date

    @Column(name = "date_fin_service")
    private LocalDate dateFinService; // Using LocalDate for date

    // Using @ElementCollection for String array/list of functions
    // This will create a separate table (e.g., fonctionnaire_fonctions)
    // to store the collection of strings for each fonctionnaire.
    @ElementCollection(fetch = FetchType.EAGER) // EAGERly fetch functions with the Fonctionnaire
    @CollectionTable(name = "fonctionnaire_fonctions", joinColumns = @JoinColumn(name = "fonctionnaire_id"))
    @Column(name = "fonction") // Name of the column in the new table
    private Set<String> fonctions = new HashSet<>(); // Using Set to avoid duplicate functions


    // --- MANY-TO-ONE RELATIONSHIP WITH PosteComptable ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poste_comptable_id", nullable = false)
    private PosteComptable posteComptable;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 'cascade = CascadeType.ALL' for saving new user with fonctionnaire
    @JoinColumn(name = "utilisateur_id", unique = true, nullable = true) // 'utilisateur_id' is the FK in 'fonctionnaire' table
    private Utilisateur utilisateur; // THIS FIELD MUST HAVE A JPA RELATIONSHIP ANNOTATION!


    public Fonctionnaire() {
    }

    public Fonctionnaire(String nomPersonne, String prenomPersonne, LocalDate dateDebutService, LocalDate dateFinService, Set<String> fonctions, PosteComptable posteComptable,Utilisateur utilisateur) {
        this.nomPersonne = nomPersonne;
        this.prenomPersonne = prenomPersonne;
        this.dateDebutService = dateDebutService;
        this.dateFinService = dateFinService;
        this.fonctions = fonctions;
        this.posteComptable = posteComptable;
        this.utilisateur = utilisateur; // Set the user during construction
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPersonne() {
        return nomPersonne;
    }

    public void setNomPersonne(String nomPersonne) {
        this.nomPersonne = nomPersonne;
    }

    public String getPrenomPersonne() {
        return prenomPersonne;
    }

    public void setPrenomPersonne(String prenomPersonne) {
        this.prenomPersonne = prenomPersonne;
    }

    public LocalDate getDateDebutService() {
        return dateDebutService;
    }

    public void setDateDebutService(LocalDate dateDebutService) {
        this.dateDebutService = dateDebutService;
    }

    public LocalDate getDateFinService() {
        return dateFinService;
    }

    public void setDateFinService(LocalDate dateFinService) {
        this.dateFinService = dateFinService;
    }

    public Set<String> getFonctions() {
        return fonctions;
    }

    public void setFonctions(Set<String> fonctions) {
        this.fonctions = fonctions;
    }

    public PosteComptable getPosteComptable() {
        return posteComptable;
    }

    public void setPosteComptable(PosteComptable posteComptable) {
        this.posteComptable = posteComptable;
    }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        // Maintain bidirectional consistency
        if (utilisateur != null && utilisateur.getFonctionnaire() != this) {
            utilisateur.setFonctionnaire(this);
        }
    }

    @Override
    public String toString() {
        return "Fonctionnaire{" +
                "id=" + id +
                ", nomPersonne='" + nomPersonne + '\'' +
                ", prenomPersonne='" + prenomPersonne + '\'' +
                ", dateDebutService=" + dateDebutService +
                ", dateFinService=" + dateFinService +
                ", fonctions=" + fonctions +
                ", posteComptableId=" + (posteComptable != null ? posteComptable.getCodePoste()  : "null") +
                '}';
    }
}