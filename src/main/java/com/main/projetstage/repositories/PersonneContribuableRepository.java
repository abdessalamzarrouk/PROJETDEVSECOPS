package com.main.projetstage.repositories;

import com.main.projetstage.models.PersonneContribuable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonneContribuableRepository extends JpaRepository<PersonneContribuable, Long> {
    Optional<PersonneContribuable> findByCinContribuable(String cin); // Example custom method
}
