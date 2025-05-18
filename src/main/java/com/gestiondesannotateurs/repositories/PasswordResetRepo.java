package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordRestRepo extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findOneByToken(String token);
}
