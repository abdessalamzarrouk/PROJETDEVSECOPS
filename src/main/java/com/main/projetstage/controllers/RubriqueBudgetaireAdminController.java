package com.main.projetstage.controllers;

import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.models.RubriqueBudgetaireId;
import com.main.projetstage.repositories.RubriqueBudgetaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/rubriques-budgetaires") // Distinct path for admin
public class RubriqueBudgetaireAdminController { // Renamed for clarity

    private final RubriqueBudgetaireRepository rubriqueBudgetaireRepository;

    @Autowired
    public RubriqueBudgetaireAdminController(RubriqueBudgetaireRepository rubriqueBudgetaireRepository) {
        this.rubriqueBudgetaireRepository = rubriqueBudgetaireRepository;
    }

    /**
     * Displays a list of all rubriques budgetaires for ADMIN.
     */
    @GetMapping
    public String listRubriquesBudgetaires(Model model) {
        List<RubriqueBudgetaire> rubriquesBudgetaires = rubriqueBudgetaireRepository.findAll();
        model.addAttribute("rubriquesBudgetaires", rubriquesBudgetaires);
        model.addAttribute("activeMenuItem", "admin_rubriques_budgetaires"); // For sidebar highlighting
        model.addAttribute("connectedUserFullName", "Admin User"); // Replace with actual user name
        return "admin/list-rubriques-budgetaires"; // Points to admin-specific template
    }

    /**
     * Displays the form to add a new rubrique budgetaire for ADMIN.
     */
    @GetMapping("/add")
    public String addRubriqueBudgetaireForm(Model model) {
        model.addAttribute("rubriqueBudgetaire", new RubriqueBudgetaire());
        model.addAttribute("activeMenuItem", "admin_rubriques_budgetaires");
        model.addAttribute("connectedUserFullName", "Admin User");
        return "admin/add-rubrique-budgetaire"; // Points to admin-specific template
    }

    /**
     * Handles the submission of the add rubrique budgetaire form for ADMIN.
     */
    @PostMapping("/add")
    public String addRubriqueBudgetaire(
            @RequestParam("numChapitre") int numChapitre,
            @RequestParam("numArticle") int numArticle,
            @RequestParam("numParagraphe") int numParagraphe,
            @RequestParam("ligneRubrique") int ligneRubrique,
            @RequestParam("libelleRubrique") String libelleRubrique,
            RedirectAttributes redirectAttributes) {
        try {
            RubriqueBudgetaireId id = new RubriqueBudgetaireId(numChapitre, numArticle, numParagraphe, ligneRubrique);
            RubriqueBudgetaire rubriqueBudgetaire = new RubriqueBudgetaire(id, libelleRubrique);

            if (rubriqueBudgetaireRepository.findById(id).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Une rubrique budgétaire avec cet identifiant existe déjà !");
                return "redirect:/admin/rubriques-budgetaires/add"; // Redirect to admin add form
            }

            rubriqueBudgetaireRepository.save(rubriqueBudgetaire);
            redirectAttributes.addFlashAttribute("successMessage", "Rubrique Budgétaire ajoutée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout de la Rubrique Budgétaire : " + e.getMessage());
        }
        return "redirect:/admin/rubriques-budgetaires"; // Redirect to admin list
    }

    /**
     * Displays the form to edit an existing rubrique budgetaire for ADMIN.
     */
    @GetMapping("/edit/{numChapitre}/{numArticle}/{numParagraphe}/{ligneRubrique}")
    public String editRubriqueBudgetaireForm(
            @PathVariable int numChapitre,
            @PathVariable int numArticle,
            @PathVariable int numParagraphe,
            @PathVariable int ligneRubrique,
            Model model, RedirectAttributes redirectAttributes) {

        RubriqueBudgetaireId id = new RubriqueBudgetaireId(numChapitre, numArticle, numParagraphe, ligneRubrique);
        Optional<RubriqueBudgetaire> rubriqueBudgetaireOptional = rubriqueBudgetaireRepository.findById(id);

        if (rubriqueBudgetaireOptional.isPresent()) {
            model.addAttribute("rubriqueBudgetaire", rubriqueBudgetaireOptional.get());
            model.addAttribute("activeMenuItem", "admin_rubriques_budgetaires");
            model.addAttribute("connectedUserFullName", "Admin User");
            return "admin/edit-rubrique-budgetaire"; // Points to admin-specific template
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Rubrique Budgétaire non trouvée !");
            return "redirect:/admin/rubriques-budgetaires"; // Redirect to admin list
        }
    }

    /**
     * Handles the submission of the edit rubrique budgetaire form for ADMIN.
     */
    @PostMapping("/edit")
    public String editRubriqueBudgetaire(@ModelAttribute RubriqueBudgetaire rubriqueBudgetaire, RedirectAttributes redirectAttributes) {
        try {
            rubriqueBudgetaireRepository.save(rubriqueBudgetaire);
            redirectAttributes.addFlashAttribute("successMessage", "Rubrique Budgétaire mise à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour de la Rubrique Budgétaire : " + e.getMessage());
        }
        return "redirect:/admin/rubriques-budgetaires"; // Redirect to admin list
    }

    /**
     * Handles the deletion of a rubrique budgetaire for ADMIN.
     */
    @GetMapping("/delete/{numChapitre}/{numArticle}/{numParagraphe}/{ligneRubrique}")
    public String deleteRubriqueBudgetaire(
            @PathVariable int numChapitre,
            @PathVariable int numArticle,
            @PathVariable int numParagraphe,
            @PathVariable int ligneRubrique,
            RedirectAttributes redirectAttributes) {
        try {
            RubriqueBudgetaireId id = new RubriqueBudgetaireId(numChapitre, numArticle, numParagraphe, ligneRubrique);
            rubriqueBudgetaireRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Rubrique Budgétaire supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de la Rubrique Budgétaire : " + e.getMessage());
        }
        return "redirect:/admin/rubriques-budgetaires"; // Redirect to admin list
    }
}