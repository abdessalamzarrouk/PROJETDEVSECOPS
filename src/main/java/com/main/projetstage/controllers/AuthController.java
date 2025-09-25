package com.main.projetstage.controllers;

import com.main.projetstage.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import com.main.projetstage.repositories.UtilisateurRepository; // Assuming this is your repo path
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;

@Controller
public class AuthController {

    @Autowired
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Custom Login Page ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Refers to src/main/resources/templates/login.html
    }

    // --- Custom Registration Page ---
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Refers to src/main/resources/templates/register.html
    }

    // --- Handle Registration Form Submission ---
    @PostMapping("/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "role_app", defaultValue = "UTILISATEUR_STANDARD") String roleApp, // Default role
            RedirectAttributes redirectAttributes,
            Model model) {

        // 1. Password Confirmation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "register"; // Stay on register page with error
        }

        // 2. Check if username or email already exists
        if (utilisateurRepository.findByNomUtilisateur(username).isPresent()) {
            model.addAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            return "register";
        }
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Cet email est déjà enregistré.");
            return "register";
        }

        // 3. Hash the password
        String hashedPassword = passwordEncoder.encode(password);

        // 4. Create and save the new user
        Utilisateur newUser = new Utilisateur();
        newUser.setNomUtilisateur(username);
        newUser.setEmail(email);
        newUser.setMot_de_passe(hashedPassword); // Save the HASHED password
        newUser.setRoleApp(roleApp); // Set default or chosen role
        newUser.setCree_le(new Timestamp(System.currentTimeMillis())); // Set creation timestamp

        utilisateurRepository.save(newUser);

        // 5. Redirect to login page with success message
        redirectAttributes.addAttribute("success", true);
        return "redirect:/login";
    }

}