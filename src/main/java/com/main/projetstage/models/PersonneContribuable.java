package com.main.projetstage.models; // Ensure your package is correct

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "personne_contribuable") // Table specifically for physical contribuables
@PrimaryKeyJoinColumn(name = "id_contribuable") // Links to the parent Contribuable's PK
public class PersonneContribuable extends Contribuable {

    @Column(name = "cin_contribuable", unique = true, nullable = false)
    private String cinContribuable;

    @Column(name = "nom_contribuable", nullable = false)
    private String nomContribuable;

    @Column(name = "prenom_contribuable", nullable = false)
    private String prenomContribuable;

    @Column(name = "adresse_auxiliaire")
    private String adresseAuxiliaire;

    // --- Constructors ---
    public PersonneContribuable() {
    }

    public PersonneContribuable(String cinContribuable, String nomContribuable, String prenomContribuable, String adresseAuxiliaire) {
        this.cinContribuable = cinContribuable;
        this.nomContribuable = nomContribuable;
        this.prenomContribuable = prenomContribuable;
        this.adresseAuxiliaire = adresseAuxiliaire;
    }

    // --- Getters and Setters ---
    public String getCinContribuable() {
        return cinContribuable;
    }

    public void setCinContribuable(String cinContribuable) {
        this.cinContribuable = cinContribuable;
    }

    public String getNomContribuable() {
        return nomContribuable;
    }

    public void setNomContribuable(String nomContribuable) {
        this.nomContribuable = nomContribuable;
    }

    public String getPrenomContribuable() {
        return prenomContribuable;
    }

    public void setPrenomContribuable(String prenomContribuable) {
        this.prenomContribuable = prenomContribuable;
    }

    public String getAdresseAuxiliaire() {
        return adresseAuxiliaire;
    }

    public void setAdresseAuxiliaire(String adresseAuxiliaire) {
        this.adresseAuxiliaire = adresseAuxiliaire;
    }

    // --- toString() for debugging ---
    @Override
    public String toString() {
        return "P_Contribuable{" +
                "idContribuable=" + getIdContribuable() +
                ", cinContribuable='" + cinContribuable + '\'' +
                ", nomContribuable='" + nomContribuable + '\'' +
                ", prenomContribuable='" + prenomContribuable + '\'' +
                '}';
    }

    // equals() and hashCode() from Contribuable parent will handle ID-based equality
}