package com.main.projetstage.models; // Ensure your package is correct

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "contribuable")
@Inheritance(strategy = InheritanceType.JOINED) // Defines JOINED strategy
public abstract class Contribuable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contribuable")
    private Long idContribuable;

    // Common attributes shared by all types of Contribuables (if any)
    // For example, if all contribuables have a general contact email, it would go here.
    // private String contactEmail;

    // Relationship: One-to-Many with OperationRecette
    // A Contribuable can have 0 or more OperationRecette records.
    // 'mappedBy' refers to the 'contribuable' field in the OperationRecette entity.
    // CascadeType.ALL: Operations will be persisted/removed when the contribuable is.
    // FetchType.LAZY: Operations are loaded only when explicitly accessed.
    @OneToMany(mappedBy = "contribuable", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OperationRecette> operationsRecette = new ArrayList<>();

    // --- Constructors ---
    public Contribuable() {
    }

    public Contribuable(Long idContribuable) {
        this.idContribuable = idContribuable;
    }

    // --- Getters and Setters ---
    public Long getIdContribuable() {
        return idContribuable;
    }

    public void setIdContribuable(Long idContribuable) {
        this.idContribuable = idContribuable;
    }

    public List<OperationRecette> getOperationsRecette() {
        return operationsRecette;
    }

    public void setOperationsRecette(List<OperationRecette> operationsRecette) {
        this.operationsRecette = operationsRecette;
    }

    // --- Helper methods to manage bi-directional relationship ---
    public void addOperationRecette(OperationRecette operation) {
        operationsRecette.add(operation);
        operation.setContribuable(this);
    }

    public void removeOperationRecette(OperationRecette operation) {
        operationsRecette.remove(operation);
        operation.setContribuable(null);
    }

    // --- equals() and hashCode() based on ID for proper entity management ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contribuable that = (Contribuable) o;
        return idContribuable != null && Objects.equals(idContribuable, that.idContribuable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idContribuable);
    }

    // --- toString() for debugging (avoiding circular references) ---
    @Override
    public String toString() {
        return "Contribuable{" +
                "idContribuable=" + idContribuable +
                '}';
    }
}