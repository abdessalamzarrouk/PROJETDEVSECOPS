package com.main.projetstage.models; // Ensure your package is correct

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "societe_contribuable") // Table specifically for society/legal entity contribuables
@PrimaryKeyJoinColumn(name = "id_contribuable") // Links to the parent Contribuable's PK
public class SocieteContribuable extends Contribuable {

    @Column(name = "id_fiscal", unique = true, nullable = false)
    private String idFiscal; // Changed to String as fiscal IDs can be alphanumeric

    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    @Column(name = "adresse_fiscale")
    private String adresseFiscale;

    @Column(name = "type_organisme")
    private String typeOrganisme;

    // --- Constructors ---
    public SocieteContribuable() {
    }

    public SocieteContribuable(String idFiscal, String raisonSociale, String adresseFiscale, String typeOrganisme) {
        this.idFiscal = idFiscal;
        this.raisonSociale = raisonSociale;
        this.adresseFiscale = adresseFiscale;
        this.typeOrganisme = typeOrganisme;
    }

    // --- Getters and Setters ---
    public String getIdFiscal() {
        return idFiscal;
    }

    public void setIdFiscal(String idFiscal) {
        this.idFiscal = idFiscal;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getAdresseFiscale() {
        return adresseFiscale;
    }

    public void setAdresseFiscale(String adresseFiscale) {
        this.adresseFiscale = adresseFiscale;
    }

    public String getTypeOrganisme() {
        return typeOrganisme;
    }

    public void setTypeOrganisme(String typeOrganisme) {
        this.typeOrganisme = typeOrganisme;
    }

    // --- toString() for debugging ---
    @Override
    public String toString() {
        return "S_Contribuable{" +
                "idContribuable=" + getIdContribuable() +
                ", idFiscal='" + idFiscal + '\'' +
                ", raisonSociale='" + raisonSociale + '\'' +
                '}';
    }

    // equals() and hashCode() from Contribuable parent will handle ID-based equality
}