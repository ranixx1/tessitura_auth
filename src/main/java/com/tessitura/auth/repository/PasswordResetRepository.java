package com.tessitura.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tessitura.auth.model.PasswordReset;
import com.tessitura.auth.model.User;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);

    @Query("SELECT p FROM PasswordReset p WHERE p.user = :user AND p.isUsed = false AND p.expireAt > :now")
    Optional<PasswordReset> findActiveTokenByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
