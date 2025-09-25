package com.main.projetstage.controllers;

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.Bordereau;
import com.main.projetstage.models.Fonctionnaire;
import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.repositories.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final BordereauRepository bordereauRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FonctionnaireRepository fonctionnaireRepository;
    private final ArticleRecetteRepository articleRecetteRepository;
    private final OperationRecetteRepository operationRecetteRepository;
    private final ContribuableRepository contribuableRepository;

    public DashboardController(BordereauRepository bordereauRepository,
                               UtilisateurRepository utilisateurRepository,
                               FonctionnaireRepository fonctionnaireRepository,
                               ArticleRecetteRepository articleRecetteRepository,
                               OperationRecetteRepository operationRecetteRepository,
                               ContribuableRepository contribuableRepository) {
        this.bordereauRepository = bordereauRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.fonctionnaireRepository = fonctionnaireRepository;
        this.articleRecetteRepository =  articleRecetteRepository;
        this.operationRecetteRepository = operationRecetteRepository;
        this.contribuableRepository = contribuableRepository;
    }

    @GetMapping("/comptable/dashboard")
    @Transactional
    public String showDashboard(@AuthenticationPrincipal Utilisateur principalUser, Model model) {
        if (principalUser == null) {
            return "redirect:/login";
        }

        Utilisateur currentUser = utilisateurRepository.findById(principalUser.getIdUtilisateur())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        model.addAttribute("currentUsername", currentUser.getNomUtilisateur());
        model.addAttribute("currentUserEmail", currentUser.getEmail());
        model.addAttribute("currentUserRole", currentUser.getRoleApp());

        List<String> userRoles = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);

        Fonctionnaire fonctionnaire = currentUser.getFonctionnaire();

        if (fonctionnaire != null) {
            String fullName = fonctionnaire.getNomPersonne() + " " + fonctionnaire.getPrenomPersonne();
            model.addAttribute("personFullName", fullName); // Already present, good.
            model.addAttribute("connectedUserFullName", fullName); // New attribute for consistent access
            model.addAttribute("personFunctions", fonctionnaire.getFonctions());
            model.addAttribute("personStartDate", fonctionnaire.getDateDebutService());

            if (fonctionnaire.getPosteComptable() != null) {
                model.addAttribute("personPosteIntitule", fonctionnaire.getPosteComptable().getIntitulePoste());
                model.addAttribute("personPosteVille", fonctionnaire.getPosteComptable().getVillePoste());
            } else {
                model.addAttribute("personPosteIntitule", "Non-assigné");
                model.addAttribute("personPosteVille", "N/A");
            }
        } else {
            model.addAttribute("personFullName", "Non-lié");
            model.addAttribute("connectedUserFullName", currentUser.getNomUtilisateur()); // Fallback to username
            model.addAttribute("personFunctions", Collections.emptySet());
            model.addAttribute("personStartDate", "N/A");
            model.addAttribute("personPosteIntitule", "Non-assigné");
            model.addAttribute("personPosteVille", "N/A");
        }

        model.addAttribute("totalArticlesRecetteCount", articleRecetteRepository.count());
        model.addAttribute("totalOperationsRecetteCount", operationRecetteRepository.count());
        model.addAttribute("totalContribuablesCount", contribuableRepository.count());

        List<Bordereau> bordereaux = bordereauRepository.findAll();
        model.addAttribute("bordereaux", bordereaux);

        long totalBordereauxCount = bordereaux.size();
        double totalGlobalAmount = bordereaux.stream()
                .mapToDouble(Bordereau::getMontantGlobal)
                .sum();

        model.addAttribute("totalBordereauxCount", totalBordereauxCount);
        model.addAttribute("totalGlobalAmount", totalGlobalAmount);

        return "comptable-dashboard";
    }


    @GetMapping("/profile")
    @Transactional
    public String showProfile(@AuthenticationPrincipal Utilisateur principalUser, Model model) {
        if (principalUser == null) {
            return "redirect:/login";
        }

        Utilisateur currentUser = utilisateurRepository.findById(principalUser.getIdUtilisateur())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserRole", currentUser.getRoleApp());
        model.addAttribute("userRoles", currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Fonctionnaire fonctionnaire = currentUser.getFonctionnaire();
        if (fonctionnaire != null) {
            String fullName = fonctionnaire.getNomPersonne() + " " + fonctionnaire.getPrenomPersonne();
            model.addAttribute("fonctionnaire", fonctionnaire);
            model.addAttribute("personFullName", fullName); // Already present, good.
            model.addAttribute("connectedUserFullName", fullName); // New attribute for consistent access
            model.addAttribute("personFunctions", fonctionnaire.getFonctions());
            model.addAttribute("personStartDate", fonctionnaire.getDateDebutService());

            if (fonctionnaire.getPosteComptable() != null) {
                model.addAttribute("personPosteIntitule", fonctionnaire.getPosteComptable().getIntitulePoste());
                model.addAttribute("personPosteVille", fonctionnaire.getPosteComptable().getVillePoste());
            } else {
                model.addAttribute("personPosteIntitule", "Non-assigné");
                model.addAttribute("personPosteVille", "N/A");
            }
        } else {
            model.addAttribute("fonctionnaire", new Fonctionnaire());
            model.addAttribute("personFullName", "Non-lié");
            model.addAttribute("connectedUserFullName", currentUser.getNomUtilisateur()); // Fallback to username
            model.addAttribute("personFunctions", Collections.emptySet());
            model.addAttribute("personStartDate", "N/A");
            model.addAttribute("personPosteIntitule", "N/A");
            model.addAttribute("personPosteVille", "N/A");
        }

        return "profile";
    }

    @PostMapping("/profile")
    @Transactional
    public String updateProfile(@AuthenticationPrincipal Utilisateur principalUser,
                                @ModelAttribute Utilisateur currentUser,
                                @ModelAttribute Fonctionnaire fonctionnaire,
                                Model model) {
        if (principalUser == null) {
            return "redirect:/login";
        }

        Utilisateur existingUser = utilisateurRepository.findById(principalUser.getIdUtilisateur())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for update"));

        existingUser.setEmail(currentUser.getEmail());
        utilisateurRepository.save(existingUser);

        if (existingUser.getFonctionnaire() != null) {
            Fonctionnaire existingFonctionnaire = existingUser.getFonctionnaire();
            existingFonctionnaire.setNomPersonne(fonctionnaire.getNomPersonne());
            existingFonctionnaire.setPrenomPersonne(fonctionnaire.getPrenomPersonne());
            fonctionnaireRepository.save(existingFonctionnaire);
        }

        model.addAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }
}