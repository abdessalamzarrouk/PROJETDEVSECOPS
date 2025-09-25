package com.main.projetstage.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class NatureImpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="code_impot")
    private int codeImpot;

    @Column(name="intitule_impot")
    private String intituleImpot;

    @Column(name="accroche_impot")
    private String accrocheImpot;

    @Column(name="provenance")
    private String provenance;

    @Column(name="type_impot")
    private String typeImpot;

    @Column(name="majorable")
    private int majorable;


    // Constructors

    public NatureImpot() {
    }

    public NatureImpot(String intituleImpot, String accrocheImpot, String provenance, String typeImpot, int majorable) {
        this.intituleImpot = intituleImpot;
        this.accrocheImpot = accrocheImpot;
        this.provenance = provenance;
        this.typeImpot = typeImpot;
        this.majorable = majorable;
    }


    // SETTERS

    public void setCodeImpot(int codeImpot) {
        this.codeImpot = codeImpot;
    }
    public void setIntituleImpot(String intituleImpot) {
        this.intituleImpot = intituleImpot;
    }
    public void setAccrocheImpot(String accrocheImpot) {
        this.accrocheImpot = accrocheImpot;
    }
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }
    public void setTypeImpot(String typeImpot) {
        this.typeImpot = typeImpot;
    }
    public void setMajorable(int majorable) {
        this.majorable = majorable;
    }

    // GETTERS

    public int getCodeImpot() {
        return codeImpot;
    }
    public String getIntituleImpot() {
        return intituleImpot;
    }
    public String getAccrocheImpot() {
        return accrocheImpot;
    }
    public String getProvenance() {
        return provenance;
    }
    public String getTypeImpot() {
        return typeImpot;
    }
    public int getMajorable() {
        return majorable;
    }


    @Override
    public String toString() {
        return "NatureImpot{" +
                "codeImpot=" + codeImpot +
                ", intituleImpot='" + intituleImpot + '\'' +
                ", typeImpot='" + typeImpot + '\'' +
                '}';
    }



}
