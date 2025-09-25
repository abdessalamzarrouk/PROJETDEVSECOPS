// src/main/java/com/main/projetstage/serviceimpl/BordereauServiceImpl.java

package com.main.projetstage.serviceimpl;

import com.main.projetstage.models.Bordereau;
import com.main.projetstage.models.ArticleRecetteStatus;
import com.main.projetstage.models.Fonctionnaire;
import com.main.projetstage.repositories.BordereauRepository;
import com.main.projetstage.repositories.FonctionnaireRepository;
import com.main.projetstage.services.BordereauService;
import com.main.projetstage.services.ArticleRecetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BordereauServiceImpl implements BordereauService {

    private final BordereauRepository bordereauRepository;
    private final ArticleRecetteService articleRecetteService;
    private final FonctionnaireRepository fonctionnaireRepository;

    @Autowired
    public BordereauServiceImpl(BordereauRepository bordereauRepository,
                                ArticleRecetteService articleRecetteService,
                                FonctionnaireRepository fonctionnaireRepository) {
        this.bordereauRepository = bordereauRepository;
        this.articleRecetteService = articleRecetteService;
        this.fonctionnaireRepository = fonctionnaireRepository;
    }

    @Override
    public List<Bordereau> findAllBordereaux() {
        return bordereauRepository.findAll();
    }

    @Override
    public Optional<Bordereau> findBordereauByCode(Long codeBordereau) {
        return bordereauRepository.findById(codeBordereau);
    }

    @Override
    @Transactional
    public Bordereau saveBordereau(Bordereau bordereau) {
        return bordereauRepository.save(bordereau);
    }

    @Override
    @Transactional
    public void deleteBordereau(Long codeBordereau) {
        bordereauRepository.deleteById(codeBordereau);
    }

    @Override
    @Transactional
    public Bordereau updateBordereauStatus(Long codeBordereau, String status, String raisonRejet) {
        Optional<Bordereau> bordereauOptional = bordereauRepository.findById(codeBordereau);
        if (bordereauOptional.isEmpty()) {
            throw new RuntimeException("Bordereau non trouvé pour la mise à jour du statut: " + codeBordereau);
        }

        Bordereau bordereau = bordereauOptional.get();
        bordereau.setStatus(status);
        bordereau.setRaisonRejet(raisonRejet);

        if ("VALIDE".equals(status)) {
            LocalDate validationDate = LocalDate.now(); // Get current date once
            bordereau.setDatePecBord(validationDate); // Set validation date for bordereau

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && ! "anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                Optional<Fonctionnaire> responsibleFonctionnaire = fonctionnaireRepository.findByUtilisateurUsername(username);

                if (responsibleFonctionnaire.isPresent()) {
                    bordereau.setFonctionnairePec(responsibleFonctionnaire.get());
                    System.out.println("Fonctionnaire PEC set for bordereau " + codeBordereau + ": " + responsibleFonctionnaire.get().getNomPersonne() + " " + responsibleFonctionnaire.get().getPrenomPersonne());
                } else {
                    System.err.println("Error: Authenticated user " + username + " is not linked to a Fonctionnaire. Bordereau " + codeBordereau + " will be validated without a specific Fonctionnaire PEC.");
                }
            } else {
                System.err.println("Warning: No authenticated user found to set as Fonctionnaire PEC for bordereau " + codeBordereau);
            }

            // --- CRITICAL FIX HERE: Call the new method to update status AND datePECAR ---
            articleRecetteService.updateArticlesStatusAndDatePecForBordereau(codeBordereau, ArticleRecetteStatus.VALIDE, validationDate,bordereau.getFonctionnairePec().getPosteComptable());

        } else if ("REJETE".equals(status)) {
            bordereau.setDatePecBord(null);
            bordereau.setFonctionnairePec(null);

            // Keep this as is, as rejected articles don't get a datePECAR
            articleRecetteService.updateArticlesStatusForBordereau(codeBordereau, ArticleRecetteStatus.REJETE);
        } else {
            bordereau.setDatePecBord(null);
            bordereau.setFonctionnairePec(null);
            // Consider if articles should also revert to EN_ATTENTE if bordereau goes back to pending
            // articleRecetteService.updateArticlesStatusForBordereau(codeBordereau, ArticleRecetteStatus.EN_ATTENTE);
        }

        return bordereauRepository.save(bordereau);
    }
}