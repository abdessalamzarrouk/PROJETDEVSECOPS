package com.main.projetstage.services;

import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurDetailsServices implements UserDetailsService {
    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String nom) throws UsernameNotFoundException {
        // 1. Call the repository method, which should return an Optional<Utilisateur>
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByNomUtilisateur(nom);

        // 2. Use orElseThrow() to either get the Utilisateur or throw UsernameNotFoundException
        //    'utilisateur' here will be a plain Utilisateur object (not an Optional)
        Utilisateur utilisateur = utilisateurOptional.orElseThrow(
                () -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom : " + nom)
        );

        // 3. Return the Utilisateur object. This works because Utilisateur implements UserDetails.
        return utilisateur;
    }

}
