package com.main.projetstage.repositories;

import com.main.projetstage.models.Fonctionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FonctionnaireRepository extends JpaRepository<Fonctionnaire, Long> {
    @Query("SELECT f FROM Fonctionnaire f JOIN f.utilisateur u WHERE u.nomUtilisateur = :username")
    Optional<Fonctionnaire> findByUtilisateurUsername(@Param("username") String username);
}