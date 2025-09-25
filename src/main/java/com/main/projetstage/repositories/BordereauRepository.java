package com.main.projetstage.repositories;

import com.main.projetstage.models.Bordereau;
import com.main.projetstage.models.Fonctionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BordereauRepository extends JpaRepository<Bordereau, Long> {
    Optional<Bordereau> getAllByCodeBordereau(Long codeBordereau);

    Optional<Bordereau> findBordereauByCodeBordereau(Long codeBordereau);

    void removeBordereauByCodeBordereau(Long codeBordereau);

    List<Bordereau> findAll();

}
