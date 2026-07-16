package com.vulnweb.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vulnweb.auth.config.AppProperties;
import com.vulnweb.auth.entity.PasswordResetToken;
import com.vulnweb.auth.entity.User;
import com.vulnweb.auth.entity.VerificationToken;
import com.vulnweb.auth.repository.PasswordResetTokenRepository;
import com.vulnweb.auth.repository.VerificationTokenRepository;

@Service
public class TokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AppProperties appProperties;

    public TokenService(VerificationTokenRepository verificationTokenRepository,
                        PasswordResetTokenRepository passwordResetTokenRepository,
                        AppProperties appProperties) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public String createVerificationToken(User user) {
        verificationTokenRepository.deleteByUser(user);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plus(
                appProperties.getAuth().getVerificationTokenExpiryHours(), ChronoUnit.HOURS));

        verificationTokenRepository.save(token);
        return token.getToken();
    }

    @Transactional
    public String createPasswordResetToken(User user) {
        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plus(
                appProperties.getAuth().getPasswordResetTokenExpiryHours(), ChronoUnit.HOURS));

        passwordResetTokenRepository.save(token);
        return token.getToken();
    }

    @Transactional(readOnly = true)
    public VerificationToken findValidVerificationToken(String tokenValue) {
        return verificationTokenRepository.findByToken(tokenValue)
                .filter(token -> !token.isUsed() && !token.isExpired())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public PasswordResetToken findValidPasswordResetToken(String tokenValue) {
        return passwordResetTokenRepository.findByToken(tokenValue)
                .filter(token -> !token.isUsed() && !token.isExpired())
                .orElse(null);
    }

    @Transactional
    public void markVerificationTokenUsed(VerificationToken token) {
        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    @Transactional
    public void markPasswordResetTokenUsed(PasswordResetToken token) {
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }
}
