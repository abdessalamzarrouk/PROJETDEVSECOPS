package com.main.projetstage.controllers;

import com.main.projetstage.models.OperationRecette;
import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus; // Import the enum for VALIDE
import com.main.projetstage.models.Contribuable;
import com.main.projetstage.models.PersonneContribuable; // Renamed P_Contribuable
import com.main.projetstage.models.SocieteContribuable;   // Renamed S_Contribuable

import com.main.projetstage.repositories.OperationRecetteRepository;
import com.main.projetstage.repositories.ArticleRecetteRepository;
import com.main.projetstage.repositories.ContribuableRepository; // For fetching any contribuable by ID
import com.main.projetstage.repositories.PersonneContribuableRepository; // For specific Personne_Contribuable ops
import com.main.projetstage.repositories.SocieteContribuableRepository;   // For specific SocieteContribuable ops

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List; // To get all ArticleRecettes for dropdowns

@Controller
@RequestMapping("/operations-recette")
public class OperationRecetteController {

    // Inject Repositories directly
    @Autowired
    private OperationRecetteRepository operationRecetteRepository;

    @Autowired
    private ArticleRecetteRepository articleRecetteRepository;

    @Autowired
    private ContribuableRepository contribuableRepository; // Used for polymorphic finds
    @Autowired
    private PersonneContribuableRepository personneContribuableRepository; // Used for saving/finding specific P_Contribuable
    @Autowired
    private SocieteContribuableRepository societeContribuableRepository;   // Used for saving/finding specific S_Contribuable

    // --- List all Operations (with Pagination) ---
    @GetMapping({"", "/"})
    public String listOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecette> operationsRecettePage = operationRecetteRepository.findAll(pageable);

        model.addAttribute("operationsRecettePage", operationsRecettePage);
        model.addAttribute("currentPage", page);

        return "operations/list-operations"; // Points to src/main/resources/templates/operations/list-operations.html
    }

    // --- Show Form for New Operation ---
    @GetMapping("/new")
    public String showCreateOperationForm(Model model) {
        model.addAttribute("operationRecette", new OperationRecette());
        // Load ONLY ArticleRecettes with status_ar = 'VALIDE' for the dropdown
        model.addAttribute("articlesRecette", articleRecetteRepository.findByStatusAR(ArticleRecetteStatus.VALIDE));
        // No contribuable selected initially for new form
        model.addAttribute("selectedContribuableType", ""); // For JS to pick up
        model.addAttribute("personneContribuable", new PersonneContribuable()); // Empty object for binding
        model.addAttribute("societeContribuable", new SocieteContribuable());   // Empty object for binding

        return "operations/create-edit-operation"; // Points to your create/edit Thymeleaf template
    }

    // --- Show Form for Editing an Existing Operation ---
    @GetMapping("/edit/{numRecette}")
    public String showEditOperationForm(@PathVariable Long numRecette, Model model) {
        Optional<OperationRecette> operationRecetteOptional = operationRecetteRepository.findById(numRecette);

        if (operationRecetteOptional.isEmpty()) {
            return "redirect:/operations-recette?error=OperationNotFound"; // Handle not found
        }

        OperationRecette operationRecette = operationRecetteOptional.get();
        model.addAttribute("operationRecette", operationRecette);
        // Load ONLY ArticleRecettes with status_ar = 'VALIDE' for the dropdown
        // Also ensure that the currently selected article (if it's not VALIDE) is included in the list
        List<ArticleRecette> validatedArticles = articleRecetteRepository.findByStatusAR(ArticleRecetteStatus.VALIDE);
        if (operationRecette.getArticleRecette() != null &&
                !validatedArticles.contains(operationRecette.getArticleRecette())) {
            // If the current article is not in the validated list (e.g., its status changed after creation)
            // Add it so it remains selected in the dropdown.
            validatedArticles.add(operationRecette.getArticleRecette());
        }
        model.addAttribute("articlesRecette", validatedArticles);


        // Determine contribuable type for pre-filling form
        Contribuable currentContribuable = operationRecette.getContribuable();
        if (currentContribuable != null) {
            model.addAttribute("selectedContribuableId", currentContribuable.getIdContribuable()); // For existing search/select

            if (currentContribuable instanceof PersonneContribuable) {
                model.addAttribute("selectedContribuableType", "P");
                model.addAttribute("personneContribuable", (PersonneContribuable) currentContribuable);
                model.addAttribute("societeContribuable", new SocieteContribuable()); // Ensure empty object for other type
            } else if (currentContribuable instanceof SocieteContribuable) {
                model.addAttribute("selectedContribuableType", "S");
                model.addAttribute("societeContribuable", (SocieteContribuable) currentContribuable);
                model.addAttribute("personneContribuable", new PersonneContribuable()); // Ensure empty object for other type
            } else {
                model.addAttribute("selectedContribuableType", ""); // Fallback if type is unknown
                model.addAttribute("personneContribuable", new PersonneContribuable());
                model.addAttribute("societeContribuable", new SocieteContribuable());
            }
        } else {
            model.addAttribute("selectedContribuableType", "");
            model.addAttribute("personneContribuable", new PersonneContribuable());
            model.addAttribute("societeContribuable", new SocieteContribuable());
        }

        return "operations/create-edit-operation";
    }

    // --- Handle Form Submission (Create or Update) ---
    @PostMapping("/save")
    public String saveOperationRecette(@ModelAttribute OperationRecette operationRecette,
                                       @RequestParam("articleRecetteId") Long articleRecetteId,
                                       @RequestParam("contribuableChoice") String contribuableChoice, // "new" or "existing"
                                       @RequestParam(value = "existingContribuableId", required = false) Long existingContribuableId,
                                       @RequestParam(value = "newContribuableType", required = false) String newContribuableType, // "P" or "S" if choice is "new"
                                       // Fields for PersonneContribuable (can be null)
                                       @RequestParam(value = "p_cin", required = false) String p_cin,
                                       @RequestParam(value = "p_nom", required = false) String p_nom,
                                       @RequestParam(value = "p_prenom", required = false) String p_prenom,
                                       @RequestParam(value = "p_adresseAuxiliaire", required = false) String p_adresseAuxiliaire,
                                       // Fields for SocieteContribuable (can be null)
                                       @RequestParam(value = "s_idFiscal", required = false) String s_idFiscal,
                                       @RequestParam(value = "s_raisonSociale", required = false) String s_raisonSociale,
                                       @RequestParam(value = "s_adresseFiscale", required = false) String s_adresseFiscale,
                                       @RequestParam(value = "s_typeOrganisme", required = false) String s_typeOrganisme,
                                       RedirectAttributes redirectAttributes) {

        try {
            // 1. Associate ArticleRecette
            // Re-validate that the selected article is actually 'VALIDE' or is the existing one.
            ArticleRecette articleRecette = articleRecetteRepository.findById(articleRecetteId)
                    .orElseThrow(() -> new IllegalArgumentException("Article Recette non trouvé."));

            // If it's a new operation, or if the article is being changed, ensure it's validated
            if (operationRecette.getNumRecette() == null || // New operation
                    !articleRecette.equals(operationRecette.getArticleRecette())) { // Article is being changed
                if (articleRecette.getStatusAR() != ArticleRecetteStatus.VALIDE) {
                    throw new IllegalArgumentException("Seuls les Articles de Recette validés peuvent être associés à une opération.");
                }
            }
            operationRecette.setArticleRecette(articleRecette);

            // 2. Handle Contribuable Association/Creation
            Contribuable linkedContribuable;
            if ("existing".equals(contribuableChoice) && existingContribuableId != null) {
                // User selected an existing contribuable
                linkedContribuable = contribuableRepository.findById(existingContribuableId)
                        .orElseThrow(() -> new IllegalArgumentException("Contribuable existant non trouvé."));
            } else if ("new".equals(contribuableChoice)) {
                // User is creating a new contribuable based on type
                if ("P".equals(newContribuableType)) {
                    PersonneContribuable pContribuable = new PersonneContribuable(p_cin, p_nom, p_prenom, p_adresseAuxiliaire);
                    linkedContribuable = personneContribuableRepository.save(pContribuable); // Save new PersonneContribuable
                } else if ("S".equals(newContribuableType)) {
                    SocieteContribuable sContribuable = new SocieteContribuable(s_idFiscal, s_raisonSociale, s_adresseFiscale, s_typeOrganisme);
                    linkedContribuable = societeContribuableRepository.save(sContribuable); // Save new SocieteContribuable
                } else {
                    throw new IllegalArgumentException("Type de contribuable non valide fourni pour la création.");
                }
            } else {
                throw new IllegalArgumentException("Choix de contribuable non valide.");
            }
            operationRecette.setContribuable(linkedContribuable);

            // 3. Save the OperationRecette
            operationRecetteRepository.save(operationRecette);
            redirectAttributes.addFlashAttribute("successMessage", "Opération de recette sauvegardée avec succès!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la sauvegarde de l'opération: " + e.getMessage());
            // It might be useful to redirect back to the form with existing data if there's an error
            // For simplicity, we just redirect to list for now.
            return "redirect:/operations-recette/new"; // Or "/operations-recette/edit/" + operationRecette.getNumRecette()
        }
        return "redirect:/operations-recette"; // Redirect to the list page
    }

    // --- Delete an Operation ---
    @PostMapping("/{numRecette}/delete")
    public String deleteOperation(@PathVariable Long numRecette, RedirectAttributes redirectAttributes) {
        if (operationRecetteRepository.existsById(numRecette)) {
            operationRecetteRepository.deleteById(numRecette);
            redirectAttributes.addFlashAttribute("successMessage", "Opération de recette supprimée avec succès!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Opération de recette non trouvée.");
        }
        return "redirect:/operations-recette";
    }

    // --- Show Details Page (Optional, but good practice for "Détails" link) ---
    @GetMapping("/{numRecette}")
    public String showOperationDetails(@PathVariable Long numRecette, Model model) {
        Optional<OperationRecette> operationOptional = operationRecetteRepository.findById(numRecette);

        if (operationOptional.isEmpty()) {
            return "redirect:/operations-recette?error=OperationNotFound"; // Handle not found
        }
        model.addAttribute("operationRecette", operationOptional.get());
        return "operations/details-operation"; // You'd create this Thymeleaf template
    }

    // --- API Endpoint for searching existing Contribuables (useful for dynamic forms) ---
    // This allows fetching contribuables without reloading the entire page
    @GetMapping("/search-contribuables")
    @ResponseBody // Important: returns JSON, not a view name
    public List<Contribuable> searchContribuables(@RequestParam String query) {
        // This is a basic example. In a real app, you'd add more sophisticated search logic
        // For example, searching by CIN, nom, raison sociale, id fiscal
        // You'd need custom methods in your ContribuableRepository for this
        // For now, let's return all.
        return contribuableRepository.findAll(); // Or add custom search methods to repositories
    }
}