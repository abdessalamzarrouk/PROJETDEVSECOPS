    package com.main.projetstage.models;


    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;

    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    @Entity
    @Table(name="Bordereau")
    public class Bordereau {
        @Id
        @Column(name="code_bordereau")
        private Long codeBordereau;

        @Column(name="date_emission")
        private LocalDate dateEmission;

        @Column(name="date_pec_Bord")
        private LocalDate datePecBord;

        @Column(name="montantAnterieur")
        private float montantAnterieur;

        @Column(name="montantGlobal")
        private float montantGlobal;

        @Column(name="typeBordereau")
        private String typeBordereau;

        @Column(name = "raison_rejet", length = 500) // NOUVEAU: Champ pour la raison de rejet
        private String raisonRejet;

        @Column(name="status")
        private String status;


        // --- Relationship to ArticleDeRecette (One-to-Many) ---
        @OneToMany(mappedBy = "bordereau", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        // One Bordereau can have Many ArticleDeRecette
        @JsonManagedReference
        private List<ArticleRecette> articlesRecette = new ArrayList<>(); // Initialize to prevent NullPointerException

        // --- NEW FIELD: Link to the Fonctionnaire who performed the PEC ---
        @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetch to avoid loading it unless needed
        @JoinColumn(name = "fonctionnaire_pec_id") // This will be the foreign key column in the bordereau table
        private Fonctionnaire fonctionnairePec;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agent_emetteur_id")
        private Fonctionnaire agentEmetteur;

        // + getters and setters for agentEmetteur
        public Fonctionnaire getAgentEmetteur() {
            return agentEmetteur;
        }

        public void setAgentEmetteur(Fonctionnaire agentEmetteur) {
            this.agentEmetteur = agentEmetteur;
        }

        // Constructors
        public Bordereau() {}

        public Bordereau(Long codeBordereau, LocalDate dateEmission, Float montantGlobal) {
            this.codeBordereau = codeBordereau;
            this.dateEmission = dateEmission;
            this.montantGlobal = montantGlobal;
        }



        public void setCodeBordereau(Long id) {
            this.codeBordereau = id;
        }

        public Long getCodeBordereau() {
            return this.codeBordereau;
        }
        public void setDateEmission(LocalDate dateEmission) {
            this.dateEmission = dateEmission;
        }
        public LocalDate getDateEmission() {
            return this.dateEmission;
        }

        public void setDatePecBord(LocalDate datePecBord) {
            this.datePecBord = datePecBord;
        }
        public LocalDate getDatePecBord() {
            return this.datePecBord;
        }
        public void setMontantAnterieur(float montantAnterieur) {
            this.montantAnterieur = montantAnterieur;
        }

        public float getMontantAnterieur() {
            return montantAnterieur;
        }
        public void setMontantGlobal(float montantGlobal) {
            this.montantGlobal = montantGlobal;
        }
        public float getMontantGlobal() {
            return montantGlobal;
        }
        public void setTypeBordereau(String typeBordereau) {
            this.typeBordereau = typeBordereau;
        }
        public String getTypeBordereau() {
            return this.typeBordereau;
        }
        public String getStatus(){
            return this.status;
        }
        public void setStatus(String status){
            this.status = status;
        }

        // --- CHANGE STARTS HERE ---
        // Renamed the getter to follow Java Bean convention for 'articlesRecette'
        public List<ArticleRecette> getArticlesRecette() { // Changed from getArticlesDeRecette()
            return articlesRecette;
        }

        // You might also want to rename the setter for consistency, though not strictly required for this error
        public void setArticlesRecette(List<ArticleRecette> articlesRecette) { // Changed from setArticlesDeRecette() if you had one
            this.articlesRecette = articlesRecette;
        }

        // NOUVEAU: Getters et Setters pour raisonRejet
        public String getRaisonRejet() {
            return raisonRejet;
        }

        public void setRaisonRejet(String raisonRejet) {
            this.raisonRejet = raisonRejet;
        }

        public Fonctionnaire getFonctionnairePec() {
            return fonctionnairePec;
        }

        public void setFonctionnairePec(Fonctionnaire fonctionnairePec) {
            this.fonctionnairePec = fonctionnairePec;
        }
        // --- CHANGE ENDS HERE ---


        // Helper methods to manage the collection and maintain bi-directional consistency
        public void addArticleDeRecette(ArticleRecette article) {
            if (article != null && !this.articlesRecette.contains(article)) {
                this.articlesRecette.add(article);
                article.setBordereau(this); // Set the foreign key reference on the child side
            }
        }

        public void removeArticleDeRecette(ArticleRecette article) {
            if (article != null && this.articlesRecette.remove(article)) {
                article.setBordereau(null); // Remove the foreign key reference on the child side
            }
        }


    }
