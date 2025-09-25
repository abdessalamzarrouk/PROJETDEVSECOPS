package com.main.projetstage.controllers;

import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.models.Fonctionnaire; // Assuming you have this model for linking users
import com.main.projetstage.repositories.*; // Import all necessary repositories for counts and CRUD
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional; // For transactional operations
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Arrays;
import java.util.Collections; // For empty sets/lists
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final FonctionnaireRepository fonctionnaireRepository;
    private final PosteComptableRepository posteComptableRepository;
    private final BordereauRepository bordereauRepository;
    private final ArticleRecetteRepository articleRecetteRepository;
    private final OperationRecetteRepository operationRecetteRepository;
    private final ContribuableRepository contribuableRepository;
    private final RubriqueBudgetaireRepository rubriqueBudgetaireRepository;
    private final NatureImpotRepository natureImpotRepository;

    public AdminController(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           FonctionnaireRepository fonctionnaireRepository,
                           PosteComptableRepository posteComptableRepository,
                           BordereauRepository bordereauRepository,
                           ArticleRecetteRepository articleRecetteRepository,
                           OperationRecetteRepository operationRecetteRepository,
                           ContribuableRepository contribuableRepository,
                           RubriqueBudgetaireRepository rubriqueBudgetaireRepository,
                           NatureImpotRepository natureImpotRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.fonctionnaireRepository = fonctionnaireRepository;
        this.posteComptableRepository = posteComptableRepository;
        this.bordereauRepository = bordereauRepository;
        this.articleRecetteRepository = articleRecetteRepository;
        this.operationRecetteRepository = operationRecetteRepository;
        this.contribuableRepository = contribuableRepository;
        this.rubriqueBudgetaireRepository = rubriqueBudgetaireRepository;
        this.natureImpotRepository = natureImpotRepository;
    }

    // Helper to add common model attributes (user info, active menu item)
    private void addCommonAttributes(Model model, Utilisateur principalUser, String activeMenuItem) {
        if (principalUser != null) {
            // Re-fetch user to ensure it's a managed entity and get full details if necessary
            Utilisateur currentUser = utilisateurRepository.findById(principalUser.getIdUtilisateur())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Logged-in user not found in database."));
            model.addAttribute("currentUsername", currentUser.getNomUtilisateur());
            model.addAttribute("currentUserEmail", currentUser.getEmail());
            model.addAttribute("currentUserRole", currentUser.getRoleApp());

            Fonctionnaire fonctionnaire = currentUser.getFonctionnaire();
            if (fonctionnaire != null) {
                String fullName = fonctionnaire.getNomPersonne() + " " + fonctionnaire.getPrenomPersonne();
                model.addAttribute("connectedUserFullName", fullName);
            } else {
                model.addAttribute("connectedUserFullName", currentUser.getNomUtilisateur()); // Fallback to username
            }
        } else {
            model.addAttribute("connectedUserFullName", "Admin (Guest)"); // Fallback for testing or if principal is null
        }
        model.addAttribute("activeMenuItem", activeMenuItem);
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true) // Read-only transaction for dashboard display
    public String adminDashboard(@AuthenticationPrincipal Utilisateur principalUser, Model model) {
        if (principalUser == null) {
            return "redirect:/login"; // Should not happen with @PreAuthorize but good fallback
        }
        addCommonAttributes(model, principalUser, "admin_dashboard");

        // Add counts for admin dashboard summary cards directly from repositories
        model.addAttribute("totalUsersCount", utilisateurRepository.count());
        model.addAttribute("totalFonctionnairesCount", fonctionnaireRepository.count());
        model.addAttribute("totalPostesComptablesCount", posteComptableRepository.count());
        model.addAttribute("totalBordereauxCount", bordereauRepository.count());
        model.addAttribute("totalArticlesRecetteCount", articleRecetteRepository.count());
        model.addAttribute("totalOperationsRecetteCount", operationRecetteRepository.count());
        model.addAttribute("totalContribuablesCount", contribuableRepository.count());
        model.addAttribute("totalRubriquesBudgetairesCount", rubriqueBudgetaireRepository.count());
        model.addAttribute("totalNaturesImpotCount", natureImpotRepository.count());

        return "admin/admin-dashboard";
    }

    // --- User Management Endpoints ---

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public String listUsers(@AuthenticationPrincipal Utilisateur principalUser, Model model) {
        if (principalUser == null) return "redirect:/login";
        addCommonAttributes(model, principalUser, "admin_users");

        List<Utilisateur> users = utilisateurRepository.findAll();
        model.addAttribute("users", users);
        return "admin/list-users";
    }

    @GetMapping("/users/add")
    @Transactional(readOnly = true)
    public String addUserForm(@AuthenticationPrincipal Utilisateur principalUser, Model model) {
        if (principalUser == null) return "redirect:/login";
        addCommonAttributes(model, principalUser, "admin_users");

        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("roles", Arrays.asList("ADMIN", "COMPTABLE", "AGENT", "ORDONNATEUR"));
        model.addAttribute("fonctionnaires", fonctionnaireRepository.findAll());
        return "admin/add-user";
    }

    @PostMapping("/users/add")
    @Transactional
    public String saveUser(@AuthenticationPrincipal Utilisateur principalUser,
                           Utilisateur utilisateur, // Utilisateur object from form binding
                           Long fonctionnaireId,
                           RedirectAttributes redirectAttributes) {
        if (principalUser == null) return "redirect:/login";

        try {
            if (utilisateurRepository.findByNomUtilisateur(utilisateur.getNomUtilisateur()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le nom d'utilisateur '" + utilisateur.getNomUtilisateur() + "' existe déjà.");
                return "redirect:/admin/users/add";
            }
            if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "L'adresse email '" + utilisateur.getEmail() + "' est déjà utilisée.");
                return "redirect:/admin/users/add";
            }

            // *** IMPORTANT: NO PASSWORD GETTING OR SETTING HERE ***
            // The `utilisateur` object's `motDePasse` field will remain null or empty
            // unless set by some other process outside this controller.
            // Spring Security will not be able to authenticate users created this way
            // until their `motDePasse` is populated with an encoded value.

            if (fonctionnaireId != null) {
                Fonctionnaire fonctionnaire = fonctionnaireRepository.findById(fonctionnaireId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnaire non trouvé avec l'ID: " + fonctionnaireId));
                utilisateur.setFonctionnaire(fonctionnaire);
            }

            // Set creation timestamp (as you indicated setCreeLe is good)
            utilisateur.setCreeLe(new Timestamp(System.currentTimeMillis()));
            utilisateurRepository.save(utilisateur);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur ajouté avec succès ! Note: Le mot de passe devra être défini par un autre moyen pour que l'utilisateur puisse se connecter.");
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getReason());
            return "redirect:/admin/users/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    @Transactional(readOnly = true)
    public String editUserForm(@AuthenticationPrincipal Utilisateur principalUser,
                               @PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        if (principalUser == null) return "redirect:/login";
        addCommonAttributes(model, principalUser, "admin_users");

        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);
        if (utilisateurOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé.");
            return "redirect:/admin/users";
        }
        Utilisateur utilisateur = utilisateurOptional.get();
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("roles", Arrays.asList("ADMIN", "COMPTABLE", "AGENT", "ORDONNATEUR"));
        model.addAttribute("fonctionnaires", fonctionnaireRepository.findAll());
        return "admin/edit-user";
    }

    @PostMapping("/users/edit")
    @Transactional
    public String updateUser(@AuthenticationPrincipal Utilisateur principalUser,
                             Utilisateur utilisateur, // This Utilisateur will NOT have motDePasse populated from the form
                             Long fonctionnaireId,
                             RedirectAttributes redirectAttributes) {
        if (principalUser == null) return "redirect:/login";

        try {
            Utilisateur existingUser = utilisateurRepository.findById(utilisateur.getIdUtilisateur())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + utilisateur.getIdUtilisateur()));

            // Check for unique username if changed
            if (!existingUser.getNomUtilisateur().equals(utilisateur.getNomUtilisateur()) &&
                    utilisateurRepository.findByNomUtilisateur(utilisateur.getNomUtilisateur()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le nom d'utilisateur '" + utilisateur.getNomUtilisateur() + "' existe déjà.");
                return "redirect:/admin/users/edit/" + utilisateur.getIdUtilisateur();
            }
            // Check for unique email if changed
            if (!existingUser.getEmail().equals(utilisateur.getEmail()) &&
                    utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "L'adresse email '" + utilisateur.getEmail() + "' est déjà utilisée.");
                return "redirect:/admin/users/edit/" + utilisateur.getIdUtilisateur();
            }

            // Update only the fields that are allowed to be modified from this form
            existingUser.setNomUtilisateur(utilisateur.getNomUtilisateur());
            existingUser.setEmail(utilisateur.getEmail());
            existingUser.setRoleApp(utilisateur.getRoleApp());

            // *** IMPORTANT: NO PASSWORD INTERACTION AT ALL ***
            // The password field in the HTML is completely removed.

            // Handle Fonctionnaire linkage/delinkage
            if (fonctionnaireId != null) {
                Fonctionnaire newFonctionnaire = fonctionnaireRepository.findById(fonctionnaireId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnaire non trouvé avec l'ID: " + fonctionnaireId));
                existingUser.setFonctionnaire(newFonctionnaire);
            } else {
                existingUser.setFonctionnaire(null); // Delink if no fonctionnaireId provided
            }

            utilisateurRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour avec succès !");
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getReason());
            return "redirect:/admin/users/edit/" + utilisateur.getIdUtilisateur();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    @Transactional
    public String deleteUser(@AuthenticationPrincipal Utilisateur principalUser,
                             @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (principalUser == null) return "redirect:/login";

        try {
            if (!utilisateurRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé avec l'ID: " + id);
            } else {
                utilisateurRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès !");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
