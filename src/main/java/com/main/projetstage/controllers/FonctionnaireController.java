package com.main.projetstage.controllers;

import com.main.projetstage.models.Fonctionnaire;
import com.main.projetstage.models.PosteComptable;
import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.repositories.FonctionnaireRepository;
import com.main.projetstage.repositories.PosteComptableRepository;
import com.main.projetstage.repositories.UtilisateurRepository; // Assuming this exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/fonctionnaires") // Base path for fonctionnaire management
public class FonctionnaireController {

    @Autowired
    private FonctionnaireRepository fonctionnaireRepository;

    @Autowired
    private PosteComptableRepository posteComptableRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository; // Needed for linking users

    // Helper to add common model attributes (simplified version)
    private void addCommonAttributes(Model model, String activeMenuItem) {
        // In a real app, you'd get the actual logged-in user's full name via Spring Security
        model.addAttribute("connectedUserFullName", "Admin User"); // Simplified
        model.addAttribute("activeMenuItem", activeMenuItem);
    }

    /**
     * Display a list of all fonctionnaires.
     */
    @GetMapping
    public String listFonctionnaires(Model model) {
        addCommonAttributes(model, "admin_fonctionnaires");
        model.addAttribute("fonctionnaires", fonctionnaireRepository.findAll());
        return "admin/list-fonctionnaires";
    }

    /**
     * Show the form for adding a new fonctionnaire.
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        addCommonAttributes(model, "admin_fonctionnaires");
        model.addAttribute("fonctionnaire", new Fonctionnaire());
        model.addAttribute("posteComptables", posteComptableRepository.findAll());
        // For add, we only want users not yet linked
        model.addAttribute("availableUsers", utilisateurRepository.findAll().stream()
                .filter(u -> u.getFonctionnaire() == null)
                .collect(Collectors.toList()));
        model.addAttribute("fonctionsString", ""); // Initialize for form
        return "admin/add-fonctionnaire";
    }

    /**
     * Process the form submission for adding a new fonctionnaire.
     */
    @PostMapping("/add")
    public String addFonctionnaire(@ModelAttribute("fonctionnaire") Fonctionnaire fonctionnaire,
                                   @RequestParam(value = "posteComptableId", required = false) Long posteComptableId,
                                   @RequestParam(value = "utilisateurId", required = false) Long utilisateurId,
                                   @RequestParam("fonctionsInput") String fonctionsInput,
                                   RedirectAttributes redirectAttributes) {

        // Set functions from comma-separated string
        if (fonctionsInput != null && !fonctionsInput.trim().isEmpty()) {
            Set<String> fonctions = new HashSet<>(Set.of(fonctionsInput.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet()));
            fonctionnaire.setFonctions(fonctions);
        } else {
            fonctionnaire.setFonctions(new HashSet<>());
        }

        // Set PosteComptable
        if (posteComptableId != null) {
            Optional<PosteComptable> posteComptableOptional = posteComptableRepository.findById(posteComptableId);
            posteComptableOptional.ifPresent(fonctionnaire::setPosteComptable);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Le Poste Comptable est obligatoire.");
            return "redirect:/admin/fonctionnaires/add";
        }

        // Link Utilisateur (OneToOne)
        if (utilisateurId != null) {
            Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(utilisateurId);
            if (utilisateurOptional.isPresent()) {
                Utilisateur userToLink = utilisateurOptional.get();
                // Check if user is already linked (simplified check)
                if (userToLink.getFonctionnaire() != null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Cet utilisateur est déjà lié à un fonctionnaire.");
                    return "redirect:/admin/fonctionnaires/add";
                }
                fonctionnaire.setUtilisateur(userToLink);
                userToLink.setFonctionnaire(fonctionnaire); // Bidirectional
                utilisateurRepository.save(userToLink); // Save user to update its fonctionnaire field
            }
        } else {
            fonctionnaire.setUtilisateur(null); // Explicitly set to null if not linked
        }

        fonctionnaireRepository.save(fonctionnaire);
        redirectAttributes.addFlashAttribute("successMessage", "Fonctionnaire ajouté avec succès !");
        return "redirect:/admin/fonctionnaires";
    }

    /**
     * Show the form for editing an existing fonctionnaire.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, "admin_fonctionnaires");
        Optional<Fonctionnaire> fonctionnaireOptional = fonctionnaireRepository.findById(id);
        if (fonctionnaireOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fonctionnaire introuvable.");
            return "redirect:/admin/fonctionnaires";
        }

        Fonctionnaire fonctionnaire = fonctionnaireOptional.get();
        model.addAttribute("fonctionnaire", fonctionnaire);
        model.addAttribute("posteComptables", posteComptableRepository.findAll());

        // For edit, available users include unlinked ones PLUS the currently linked one
        List<Utilisateur> availableUsers = utilisateurRepository.findAll().stream()
                .filter(u -> u.getFonctionnaire() == null || (fonctionnaire.getUtilisateur() != null && u.getIdUtilisateur().equals(fonctionnaire.getUtilisateur().getIdUtilisateur())))
                .collect(Collectors.toList());
        model.addAttribute("availableUsers", availableUsers);


        // Convert Set<String> fonctions back to comma-separated string for the form
        String fonctionsString = String.join(", ", fonctionnaire.getFonctions());
        model.addAttribute("fonctionsString", fonctionsString);
        return "admin/edit-fonctionnaire";
    }

    /**
     * Process the form submission for updating an existing fonctionnaire.
     */
    @PostMapping("/edit")
    public String updateFonctionnaire(@ModelAttribute("fonctionnaire") Fonctionnaire fonctionnaire,
                                      @RequestParam(value = "posteComptableId", required = false) Long posteComptableId,
                                      @RequestParam(value = "utilisateurId", required = false) Long utilisateurId,
                                      @RequestParam("fonctionsInput") String fonctionsInput,
                                      RedirectAttributes redirectAttributes) {

        // Find existing fonctionnaire to preserve relationships not directly bound by the form
        Optional<Fonctionnaire> existingFonctionnaireOpt = fonctionnaireRepository.findById(fonctionnaire.getId());
        if (existingFonctionnaireOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fonctionnaire à modifier introuvable.");
            return "redirect:/admin/fonctionnaires";
        }
        Fonctionnaire existingFonctionnaire = existingFonctionnaireOpt.get();

        // Update basic fields
        existingFonctionnaire.setNomPersonne(fonctionnaire.getNomPersonne());
        existingFonctionnaire.setPrenomPersonne(fonctionnaire.getPrenomPersonne());
        existingFonctionnaire.setDateDebutService(fonctionnaire.getDateDebutService());
        existingFonctionnaire.setDateFinService(fonctionnaire.getDateFinService());

        // Update functions
        if (fonctionsInput != null && !fonctionsInput.trim().isEmpty()) {
            Set<String> fonctions = new HashSet<>(Set.of(fonctionsInput.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet()));
            existingFonctionnaire.setFonctions(fonctions);
        } else {
            existingFonctionnaire.setFonctions(new HashSet<>());
        }

        // Update PosteComptable
        if (posteComptableId != null) {
            Optional<PosteComptable> posteComptableOptional = posteComptableRepository.findById(posteComptableId);
            posteComptableOptional.ifPresent(existingFonctionnaire::setPosteComptable);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Le Poste Comptable est obligatoire.");
            return "redirect:/admin/fonctionnaires/edit/" + fonctionnaire.getId();
        }

        // Handle Utilisateur relationship update
        Utilisateur oldLinkedUser = existingFonctionnaire.getUtilisateur();
        if (oldLinkedUser != null) {
            oldLinkedUser.setFonctionnaire(null); // Unlink old user
            utilisateurRepository.save(oldLinkedUser);
        }

        if (utilisateurId != null) {
            Optional<Utilisateur> newLinkedUserOptional = utilisateurRepository.findById(utilisateurId);
            if (newLinkedUserOptional.isPresent()) {
                Utilisateur newLinkedUser = newLinkedUserOptional.get();
                // Ensure new user isn't linked elsewhere (simple check)
                if (newLinkedUser.getFonctionnaire() == null) {
                    existingFonctionnaire.setUtilisateur(newLinkedUser);
                    newLinkedUser.setFonctionnaire(existingFonctionnaire);
                    utilisateurRepository.save(newLinkedUser);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Le nouvel utilisateur sélectionné est déjà lié à un autre fonctionnaire.");
                    // Re-add the old user link if it existed, otherwise set to null
                    existingFonctionnaire.setUtilisateur(oldLinkedUser);
                    if (oldLinkedUser != null) {
                        oldLinkedUser.setFonctionnaire(existingFonctionnaire);
                        utilisateurRepository.save(oldLinkedUser);
                    }
                    return "redirect:/admin/fonctionnaires/edit/" + fonctionnaire.getId();
                }
            }
        } else {
            existingFonctionnaire.setUtilisateur(null); // No user linked
        }

        fonctionnaireRepository.save(existingFonctionnaire);
        redirectAttributes.addFlashAttribute("successMessage", "Fonctionnaire mis à jour avec succès !");
        return "redirect:/admin/fonctionnaires";
    }

    /**
     * Delete a fonctionnaire.
     */
    @GetMapping("/delete/{id}")
    public String deleteFonctionnaire(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Fonctionnaire> fonctionnaireOptional = fonctionnaireRepository.findById(id);
        if (fonctionnaireOptional.isPresent()) {
            Fonctionnaire fonctionnaireToDelete = fonctionnaireOptional.get();

            // Unlink from Utilisateur first
            Utilisateur linkedUser = fonctionnaireToDelete.getUtilisateur();
            if (linkedUser != null) {
                linkedUser.setFonctionnaire(null);
                utilisateurRepository.save(linkedUser);
            }

            fonctionnaireRepository.delete(fonctionnaireToDelete);
            redirectAttributes.addFlashAttribute("successMessage", "Fonctionnaire supprimé avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Fonctionnaire introuvable.");
        }
        return "redirect:/admin/fonctionnaires";
    }
}