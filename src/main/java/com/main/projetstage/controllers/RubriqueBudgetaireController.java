package com.main.projetstage.controllers;

import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.services.RubriqueBudgetaireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/rubriques-budgetaires")
public class RubriqueBudgetaireController {

    private final RubriqueBudgetaireService rubriqueBudgetaireService;

    public RubriqueBudgetaireController(RubriqueBudgetaireService rubriqueBudgetaireService) {
        this.rubriqueBudgetaireService = rubriqueBudgetaireService;
    }

    @GetMapping
    public String listRubriquesBudgetaires(
            @RequestParam(value = "chapitre", required = false) Integer numChapitre,
            @RequestParam(value = "article", required = false) Integer numArticle,
            @RequestParam(value = "paragraphe", required = false) Integer numParagraphe,
            @RequestParam(value = "ligne", required = false) Integer ligneRubrique,
            Model model) {

        List<RubriqueBudgetaire> rubriques = rubriqueBudgetaireService.getFilteredRubriquesBudgetaires(
                numChapitre, numArticle, numParagraphe, ligneRubrique);

        model.addAttribute("rubriquesBudgetaires", rubriques);

        // Add the current filter values back to the model so the form can pre-fill them
        model.addAttribute("currentChapitre", numChapitre);
        model.addAttribute("currentArticle", numArticle);
        model.addAttribute("currentParagraphe", numParagraphe);
        model.addAttribute("currentLigne", ligneRubrique);

        return "list-rubriques-budgetaires";
    }
}