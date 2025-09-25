package com.main.projetstage.repositories;

import com.main.projetstage.models.Contribuable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContribuableRepository extends JpaRepository<Contribuable, Long> {
}
