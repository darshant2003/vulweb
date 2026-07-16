package com.vulnweb.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vulnweb.auth.entity.PasswordResetToken;
import com.vulnweb.auth.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
