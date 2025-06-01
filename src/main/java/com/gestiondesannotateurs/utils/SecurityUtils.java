package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private PersonRepo personRepo;

    public boolean isOwner(Long incomingPersonId) {
        // Validate input
        if (incomingPersonId == null || incomingPersonId <= 0) {
            return false;
        }

        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get principal
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString(); // Fallback for String or custom principal
        }

        if (username == null || username.isEmpty()) {
            return false;
        }

        try {
            return personRepo.isOwner(username, incomingPersonId);
        } catch (Exception e) {
            return false; // Trigger 403 instead of 500
        }
    }
}