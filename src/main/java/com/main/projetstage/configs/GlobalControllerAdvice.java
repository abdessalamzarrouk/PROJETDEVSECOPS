package com.main.projetstage.configs;

import com.main.projetstage.models.Fonctionnaire;
import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UtilisateurRepository utilisateurRepository;

    public GlobalControllerAdvice(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @ModelAttribute
    public void addConnectedUserAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {

            String username = authentication.getName(); // This gets the username from UserDetails (which is nomUtilisateur in your case)

            Utilisateur currentUser = utilisateurRepository.findByNomUtilisateur(username).orElse(null);
            // Removed the `if (principal instanceof Utilisateur)` and `findById` logic
            // because `authentication.getName()` always gives the username (nomUtilisateur)
            // and `findByNomUtilisateur` is the most direct way to get the full entity.

            if (currentUser != null) {
                model.addAttribute("currentUsername", currentUser.getNomUtilisateur());
                model.addAttribute("currentUserEmail", currentUser.getEmail());
                model.addAttribute("currentUserRole", currentUser.getRoleApp());

                Fonctionnaire fonctionnaire = currentUser.getFonctionnaire();
                if (fonctionnaire != null) {
                    String fullName = fonctionnaire.getNomPersonne() + " " + fonctionnaire.getPrenomPersonne();
                    model.addAttribute("connectedUserFullName", fullName);
                    // Add other fonctionnaire details if you want them globally available
                    // model.addAttribute("personFunctions", fonctionnaire.getFonctions());
                } else {
                    // Fallback if no Fonctionnaire is linked
                    model.addAttribute("connectedUserFullName", currentUser.getNomUtilisateur());
                }
            } else {
                model.addAttribute("connectedUserFullName", "Utilisateur non trouvé (DB)");
                // Potentially log a warning here, as this indicates a user exists in security context but not DB
            }
        } else {
            model.addAttribute("connectedUserFullName", "Invité");
            model.addAttribute("currentUsername", "Invité");
            model.addAttribute("currentUserEmail", "N/A");
            model.addAttribute("currentUserRole", "N/A");
        }
    }
}