package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepo extends JpaRepository<Person,Long> {
    Optional<Person> findOneByUserName(String username);
    Optional<Person> findOneByAccountCreationToken(String token);

    Long countByActive(Boolean active); // active users in general
    Long countByRole(String role);
    Long countByRoleAndActive(String role, Boolean active);

    @Query("SELECT EXISTS (" +
            "SELECT p FROM Person p " +
            "WHERE p.id = :incomingId AND p.userName = :username)")
    boolean isOwner(@Param("username")  String username, @Param("incomingId") Long incomingId);


}
