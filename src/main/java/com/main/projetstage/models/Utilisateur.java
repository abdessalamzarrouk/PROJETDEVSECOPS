package com.main.projetstage.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

// MAKE SURE CREDENTIALS EXPIRED IS ON AND USER ENABLED

@Entity
@Table(name="utilisateurs")
public class Utilisateur implements UserDetails {
@Id
@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idUtilisateur;
@Column(nullable=false,unique=true)
    private String nomUtilisateur;
@Column(nullable=false)
    private String motDePasse;
@Column(unique=true)
    private String email;
@Column(nullable=false)
    private String roleApp;
@Column(name = "cree_le", updatable = false)
    private Timestamp creeLe;
@Column(name = "derniere_connexion")
    private Timestamp derniereConnexion;
@OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = false)
    private Fonctionnaire fonctionnaire; // THIS FIELD MUST HAVE A JPA RELATIONSHIP ANNOTATION!


    public Utilisateur() {

    }

    public Utilisateur(String nom_utilisateur, String mot_de_passe, String email, String role_app) {
        this.nomUtilisateur = nom_utilisateur;
        this.motDePasse = mot_de_passe; // Remember to hash this before saving!
        this.email = email;
        this.roleApp = role_app;
        this.creeLe = new Timestamp(System.currentTimeMillis());
    }

    public Long getIdUtilisateur(){
        return this.idUtilisateur;
    }
    public void setIdUtilisateur(Long id_utilisateur){
        this.idUtilisateur=id_utilisateur;
    }
    public String getNomUtilisateur(){
        return this.nomUtilisateur;
    }
    public void  setNomUtilisateur(String nom_utilisateur){
        this.nomUtilisateur=nom_utilisateur;
    }
    public String getMot_de_passe(){
        return this.motDePasse;
    }
    public void  setMot_de_passe(String mot_de_passe){
        this.motDePasse=mot_de_passe;
    }
    public String getEmail(){
        return this.email;
    }
    public void  setEmail(String email){
        this.email=email;
    }
    public String getRoleApp(){
        return this.roleApp;
    }
    public void  setRoleApp(String role_app){
        this.roleApp=role_app;
    }
    public Timestamp getCree_le(){
        return this.creeLe;
    }
    public void  setCree_le(Timestamp cree_le){
        this.creeLe=cree_le;
    }
    public Timestamp getDerniere_connexion(){
        return this.derniereConnexion;
    }
    public Fonctionnaire getFonctionnaire() { return fonctionnaire; }
    public void setFonctionnaire(Fonctionnaire fonctionnaire) {
        this.fonctionnaire = fonctionnaire;
        // Maintain bidirectional consistency
        if (fonctionnaire != null && fonctionnaire.getUtilisateur() != this) {
            fonctionnaire.setUtilisateur(this);
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We're converting your single 'role_app' string into a Collection of GrantedAuthority.
        // It's a common practice to prefix roles with "ROLE_" in Spring Security.
        // Convert the role to uppercase for consistency with Spring Security's hasRole() checks.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleApp.toUpperCase()));
    }

    @Override
    public String getPassword(){
     return this.motDePasse;
    }

    @Override
    public String getUsername(){
        return this.nomUtilisateur;
    }

    public void setCreeLe(Timestamp timestamp) {
        this.creeLe = timestamp;
    }
    public Timestamp getCreeLe(){ // This is the getter for 'creeLe'
        return this.creeLe;
    }
}

