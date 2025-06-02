package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private TaskToDoRepo taskToDoRepo;

    public boolean isOwner(Long incomingPersonId) {
        if (incomingPersonId == null || incomingPersonId <= 0) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        if (username == null || username.isEmpty()) {
            return false;
        }

        try {
            return personRepo.isOwner(username, incomingPersonId);
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isOwnerOfAtask(Long incomingTaskId) {
        if (incomingTaskId == null || incomingTaskId <= 0) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        if (username == null || username.isEmpty()) {
            return false;
        }

        Optional<TaskToDo> task =  taskToDoRepo.findById(incomingTaskId);

        if (task.isEmpty()) {
            return false;
        }



        try {
            return personRepo.isOwner(username,task.get().getAnnotator().getId() );
        } catch (Exception e) {
            return false;
        }
    }


}