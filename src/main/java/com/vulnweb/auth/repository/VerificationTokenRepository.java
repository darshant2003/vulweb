package com.vulnweb.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vulnweb.auth.entity.User;
import com.vulnweb.auth.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(User user);
}
