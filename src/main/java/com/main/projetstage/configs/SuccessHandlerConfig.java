package com.main.projetstage.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class SuccessHandlerConfig extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/default-dashboard"; // Fallback URL

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            System.out.println("AUTHORITY: " + authorityName);
            if (authorityName.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authorityName.equals("ROLE_COMPTABLE")) {
                redirectUrl = "/comptable/dashboard";
                break;
            } else if (authorityName.equals("ROLE_USER")) { // Or a more general user role
                redirectUrl = "/user/dashboard";
                break;
            }
        }

        // Use the superclass method to perform the actual redirection
        // This also handles clearing authentication attributes from the session if needed
        super.setDefaultTargetUrl(redirectUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}