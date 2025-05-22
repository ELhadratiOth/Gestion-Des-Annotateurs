package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepo extends JpaRepository<Person,Long> {
    Optional<Person> findOneByUserName(String username);
    Optional<Person> findOneByAccountCreationToken(String token);

    Long countByActive(Boolean active); // active users in general
    Long countByRole(String role);
    Long countByRoleAndActive(String role, Boolean active);


}
