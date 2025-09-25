package com.main.projetstage.controllers;

import com.main.projetstage.models.Bordereau;
import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus;
import com.main.projetstage.services.BordereauService;
import com.main.projetstage.services.ArticleRecetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bordereaux") // Toutes les URL de ce contrôleur commencent par /bordereaux
public class BordereauController {

    private final BordereauService bordereauService;
    private final ArticleRecetteService articleRecetteService;

    @Autowired
    public BordereauController(BordereauService bordereauService, ArticleRecetteService articleRecetteService) {
        this.bordereauService = bordereauService;
        this.articleRecetteService = articleRecetteService;
    }

    /**
     * Affiche la liste de tous les bordereaux.
     * URL: GET /bordereaux
     */
    @GetMapping
    public String listerBordereaux(Model model) {
        List<Bordereau> bordereaux = bordereauService.findAllBordereaux();
        model.addAttribute("bordereaux", bordereaux);
        return "bordereaux/listeBordereaux"; // Le nom de votre fichier Thymeleaf (ex: listeBordereaux.html)
    }

    /**
     * Affiche les détails d'un bordereau spécifique.
     * URL: GET /bordereaux/{codeBordereau}
     */
    @GetMapping("/{codeBordereau}")
    public String detailsBordereau(@PathVariable Long codeBordereau, Model model, RedirectAttributes redirectAttributes) {
        Optional<Bordereau> bordereauOptional = bordereauService.findBordereauByCode(codeBordereau);

        if (bordereauOptional.isPresent()) {
            Bordereau bordereau = bordereauOptional.get();
            model.addAttribute("bordereau", bordereau);
            model.addAttribute("articlesRecette", bordereau.getArticlesRecette());
            model.addAttribute("nouveauBordereau", new Bordereau()); // Pour le formulaire de rejet si besoin
            return "bordereaux/detailsBordereau"; // Le nom de votre fichier Thymeleaf (ex: detailsBordereau.html)
        } else {
            redirectAttributes.addFlashAttribute("error", "Bordereau non trouvé avec le code: " + codeBordereau);
            return "redirect:/bordereaux";
        }
    }

    // --- THIS IS THE CRITICAL METHOD FOR VALIDATION ---
    @PostMapping("/{codeBordereau}/valider")
    public String validerBordereau(@PathVariable Long codeBordereau, RedirectAttributes ra) {
        try {
            bordereauService.updateBordereauStatus(codeBordereau, "VALIDE", null);
            ra.addFlashAttribute("success", "Bordereau validé avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors de la validation du bordereau: " + e.getMessage());
            // Log the exception for debugging
            e.printStackTrace();
        }
        return "redirect:/bordereaux/" + codeBordereau; // Redirect back to details page
        // Or return "redirect:/bordereaux"; for the list page
    }

    // --- THIS IS THE CRITICAL METHOD FOR REJECTION ---
    @PostMapping("/{codeBordereau}/rejeter")
    public String rejeterBordereau(@PathVariable Long codeBordereau,
                                   @RequestParam("raisonRejet") String raisonRejet,
                                   RedirectAttributes ra) {
        try {
            if (raisonRejet == null || raisonRejet.trim().isEmpty()) {
                ra.addFlashAttribute("error", "La raison du rejet ne peut pas être vide.");
                return "redirect:/bordereaux/" + codeBordereau;
            }
            bordereauService.updateBordereauStatus(codeBordereau, "REJETE", raisonRejet);
            ra.addFlashAttribute("success", "Bordereau rejeté avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors du rejet du bordereau: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/bordereaux/" + codeBordereau;
    }

    /**
     * Permet d'éditer le statut d'un article de recette individuel.
     * Ceci est un exemple si vous voulez modifier les articles un par un.
     * URL: POST /bordereaux/articles/{numArticleRecette}/updateStatus
     */
    @PostMapping("/articles/{numArticleRecette}/updateStatus")
    public String updateArticleRecetteStatus(@PathVariable Long numArticleRecette,
                                             @RequestParam("status") ArticleRecetteStatus status,
                                             @RequestParam(value = "notes", required = false) String notes,
                                             RedirectAttributes redirectAttributes) {
        try {
            ArticleRecette updatedArticle = articleRecetteService.updateArticleRecetteStatus(numArticleRecette, status, notes);
            redirectAttributes.addFlashAttribute("success", "Statut de l'article de recette " + numArticleRecette + " mis à jour à " + status.name() + ".");

            // Redirige vers les détails du bordereau parent si l'article est trouvé
            if (updatedArticle != null && updatedArticle.getBordereau() != null) {
                return "redirect:/bordereaux/" + updatedArticle.getBordereau().getCodeBordereau();
            } else {
                return "redirect:/bordereaux"; // Redirige vers la liste si le parent n'est pas trouvé
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du statut de l'article " + numArticleRecette + ": " + e.getMessage());
            return "redirect:/bordereaux"; // Ou vers une page d'erreur
        }
    }
}