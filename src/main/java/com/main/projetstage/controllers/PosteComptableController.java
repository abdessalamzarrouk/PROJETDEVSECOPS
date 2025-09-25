package com.main.projetstage.controllers;

import com.main.projetstage.models.PosteComptable;
import com.main.projetstage.repositories.PosteComptableRepository; // Directly using the repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional; // Needed for findById

@Controller
@RequestMapping("/admin/postes-comptables")
public class PosteComptableController {

    private final PosteComptableRepository posteComptableRepository; // Injecting the repository

    @Autowired
    public PosteComptableController(PosteComptableRepository posteComptableRepository) {
        this.posteComptableRepository = posteComptableRepository;
    }

    /**
     * Displays a list of all postes comptables.
     */
    @GetMapping
    public String listPostesComptables(Model model) {
        List<PosteComptable> postesComptables = posteComptableRepository.findAll();
        model.addAttribute("postesComptables", postesComptables);
        model.addAttribute("activeMenuItem", "admin_postes_comptables"); // For sidebar highlighting
        // For connected user full name (example, integrate with Spring Security for actual user)
        model.addAttribute("connectedUserFullName", "Admin User"); // Replace with actual user name
        return "admin/list-postes-comptables";
    }

    /**
     * Displays the form to add a new poste comptable.
     */
    @GetMapping("/add")
    public String addPosteComptableForm(Model model) {
        model.addAttribute("posteComptable", new PosteComptable());
        model.addAttribute("activeMenuItem", "admin_postes_comptables"); // For sidebar highlighting
        model.addAttribute("connectedUserFullName", "Admin User"); // Replace with actual user name
        return "admin/add-poste-comptable";
    }

    /**
     * Handles the submission of the add poste comptable form.
     */
    @PostMapping("/add")
    public String addPosteComptable(@ModelAttribute PosteComptable posteComptable, RedirectAttributes redirectAttributes) {
        try {
            posteComptableRepository.save(posteComptable); // Directly saving via repository
            redirectAttributes.addFlashAttribute("successMessage", "Poste Comptable ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout du Poste Comptable : " + e.getMessage());
        }
        return "redirect:/admin/postes-comptables";
    }

    /**
     * Displays the form to edit an existing poste comptable.
     */
    @GetMapping("/edit/{codePoste}")
    public String editPosteComptableForm(@PathVariable Long codePoste, Model model, RedirectAttributes redirectAttributes) {
        Optional<PosteComptable> posteComptableOptional = posteComptableRepository.findById(codePoste); // Directly finding by ID
        if (posteComptableOptional.isPresent()) {
            model.addAttribute("posteComptable", posteComptableOptional.get());
            model.addAttribute("activeMenuItem", "admin_postes_comptables"); // For sidebar highlighting
            model.addAttribute("connectedUserFullName", "Admin User"); // Replace with actual user name
            return "admin/edit-poste-comptable";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Poste Comptable non trouvé !");
            return "redirect:/admin/postes-comptables";
        }
    }

    /**
     * Handles the submission of the edit poste comptable form.
     */
    @PostMapping("/edit")
    public String editPosteComptable(@ModelAttribute PosteComptable posteComptable, RedirectAttributes redirectAttributes) {
        try {
            // When using save on an entity with an ID, JPA will perform an update.
            posteComptableRepository.save(posteComptable);
            redirectAttributes.addFlashAttribute("successMessage", "Poste Comptable mis à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du Poste Comptable : " + e.getMessage());
        }
        return "redirect:/admin/postes-comptables";
    }

    /**
     * Handles the deletion of a poste comptable.
     */
    @GetMapping("/delete/{codePoste}")
    public String deletePosteComptable(@PathVariable Long codePoste, RedirectAttributes redirectAttributes) {
        try {
            posteComptableRepository.deleteById(codePoste); // Directly deleting by ID
            redirectAttributes.addFlashAttribute("successMessage", "Poste Comptable supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du Poste Comptable : " + e.getMessage());
        }
        return "redirect:/admin/postes-comptables";
    }
}