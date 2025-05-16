package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Othman;
import com.gestiondesannotateurs.entities.Person;
import com.gestiondesannotateurs.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PersonRepo personRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> account = personRepo.findOneByUserName(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException("bad credentials");
        }
        Person user = account.get();

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
