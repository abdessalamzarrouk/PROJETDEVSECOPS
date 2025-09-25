package com.main.projetstage.repositories;

import com.main.projetstage.models.SocieteContribuable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocieteContribuableRepository extends JpaRepository<SocieteContribuable, Long> {
    Optional<SocieteContribuable> findByIdFiscal(String id);
}
