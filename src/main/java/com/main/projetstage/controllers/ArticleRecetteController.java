package com.main.projetstage.controllers;

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.ArticleRecetteStatus; // Assuming you have this enum
import com.main.projetstage.models.Bordereau;
import com.main.projetstage.models.PosteComptable;
import com.main.projetstage.models.NatureImpot;
import com.main.projetstage.models.RubriqueBudgetaire;
import com.main.projetstage.repositories.ArticleRecetteRepository;
import com.main.projetstage.repositories.BordereauRepository; // If you need to list bordereaux
import com.main.projetstage.repositories.NatureImpotRepository; // For dropdowns if you ever add an edit feature
import com.main.projetstage.repositories.PosteComptableRepository; // For dropdowns
import com.main.projetstage.repositories.RubriqueBudgetaireRepository; // For dropdowns

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.main.projetstage.services.ArticleRecetteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/articles-recette") // Base URL for this controller
public class ArticleRecetteController {


    private final ArticleRecetteService articleRecetteService;

    @Autowired
    private ArticleRecetteRepository articleRecetteRepository;

    // You might need these if you implement add/edit forms for ArticleRecette later
    @Autowired
    private BordereauRepository bordereauRepository;
    @Autowired
    private PosteComptableRepository posteComptableRepository;
    @Autowired
    private NatureImpotRepository natureImpotRepository;
    @Autowired
    private RubriqueBudgetaireRepository rubriqueBudgetaireRepository;


    /*
     * Displays a paginated list of all ArticleRecette.
     * @param page The current page number (default 0).
     * @param size The number of items per page (default 10).
     * @param model The Spring Model to add attributes to.
     * @return The name of the Thymeleaf template for the list view.
     */

    public ArticleRecetteController(ArticleRecetteService articleRecetteService) {
        this.articleRecetteService = articleRecetteService;
    }
    @GetMapping({"", "/"})
    public String listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long numArticleRecette,
            @RequestParam(required = false) String typeAR,
            @RequestParam(required = false) ArticleRecetteStatus statusAR,
            @RequestParam(required = false) String codeBordereau,
            @RequestParam(required = false) String intitulePoste,
            @RequestParam(required = false) LocalDate dateEmissionStart,
            @RequestParam(required = false) LocalDate dateEmissionEnd,
            @RequestParam(required = false) LocalDate datePECStart,
            @RequestParam(required = false) LocalDate datePECEnd,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Call the service method to get the filtered and paginated articles
        Page<ArticleRecette> articleRecettePage = articleRecetteService.getFilteredArticlesRecette(
                numArticleRecette, typeAR, statusAR, codeBordereau, intitulePoste,
                dateEmissionStart, dateEmissionEnd, datePECStart, datePECEnd, pageable);

        model.addAttribute("articleRecettePage", articleRecettePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        // Add filter parameters back to the model so they persist in the form fields
        model.addAttribute("numArticleRecette", numArticleRecette);
        model.addAttribute("typeAR", typeAR);
        model.addAttribute("statusAR", statusAR);
        model.addAttribute("codeBordereau", codeBordereau);
        model.addAttribute("intitulePoste", intitulePoste);
        model.addAttribute("dateEmissionStart", dateEmissionStart);
        model.addAttribute("dateEmissionEnd", dateEmissionEnd);
        model.addAttribute("datePECStart", datePECStart);
        model.addAttribute("datePECEnd", datePECEnd);

        // Pass all ArticleRecetteStatus enum values to the template for the dropdown
        model.addAttribute("allStatuses", ArticleRecetteStatus.values());

        return "articles/list-articles";
    }

    /**
     * Displays the details of a single ArticleRecette.
     * @param numArticleRecette The ID of the article to display.
     * @param model The Spring Model to add attributes to.
     * @param redirectAttributes For flash messages on redirect.
     * @return The name of the Thymeleaf template for the details view or a redirect.
     */
    @GetMapping("/{numArticleRecette}")
    public String showArticleDetails(@PathVariable Long numArticleRecette, Model model, RedirectAttributes redirectAttributes) {
        Optional<ArticleRecette> articleOptional = articleRecetteRepository.findById(numArticleRecette);

        if (articleOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Article de Recette non trouvé.");
            return "redirect:/articles-recette"; // Redirect to list if not found
        }

        model.addAttribute("articleRecette", articleOptional.get());
        return "articles/details-article"; // Path to your Thymeleaf template
    }


}