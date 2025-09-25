package com.main.projetstage.repositories;

import com.main.projetstage.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByNomUtilisateur(String nom);

    Optional<Utilisateur> findByEmail(String email);
    boolean existsByNomUtilisateur(String nomUtilisateur);
    boolean existsByEmail(String email);
}
