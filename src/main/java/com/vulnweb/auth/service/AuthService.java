package com.vulnweb.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vulnweb.auth.config.AppProperties;
import com.vulnweb.auth.dto.AuthResponse;
import com.vulnweb.auth.dto.ForgotPasswordRequest;
import com.vulnweb.auth.dto.LoginRequest;
import com.vulnweb.auth.dto.MessageResponse;
import com.vulnweb.auth.dto.ResendVerificationRequest;
import com.vulnweb.auth.dto.ResetPasswordRequest;
import com.vulnweb.auth.dto.SignupRequest;
import com.vulnweb.auth.dto.UserResponse;
import com.vulnweb.auth.entity.PasswordResetToken;
import com.vulnweb.auth.entity.User;
import com.vulnweb.auth.entity.VerificationToken;
import com.vulnweb.auth.exception.AuthException;
import com.vulnweb.auth.exception.ResourceNotFoundException;
import com.vulnweb.auth.repository.UserRepository;
import com.vulnweb.auth.security.JwtService;
import com.vulnweb.auth.security.UserPrincipal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final AppProperties appProperties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       EmailService emailService,
                       TokenService tokenService,
                       AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.appProperties = appProperties;
    }

    @Transactional
    public MessageResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("An account with this email already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(!appProperties.getAuth().isRequireEmailVerification());

        userRepository.save(user);

        if (appProperties.getAuth().isRequireEmailVerification()) {
            String token = tokenService.createVerificationToken(user);
            emailService.sendVerificationEmail(user, token);
            return new MessageResponse("Account created. Please check your email to verify your account.");
        }

        return new MessageResponse("Account created successfully. You can now log in.");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (appProperties.getAuth().isRequireEmailVerification() && !user.isEmailVerified()) {
            throw new AuthException("Please verify your email before logging in");
        }

        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateToken(principal);

        return new AuthResponse(accessToken, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    @Transactional
    public MessageResponse verifyEmail(String token) {
        VerificationToken verificationToken = tokenService.findValidVerificationToken(token);
        if (verificationToken == null) {
            throw new AuthException("Invalid or expired verification token");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenService.markVerificationTokenUsed(verificationToken);

        return new MessageResponse("Email verified successfully. You can now log in.");
    }

    @Transactional
    public MessageResponse resendVerification(ResendVerificationRequest request) {
        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return genericVerificationResponse();
        }

        if (user.isEmailVerified()) {
            throw new AuthException("Email is already verified");
        }

        String token = tokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, token);

        return genericVerificationResponse();
    }

    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String token = tokenService.createPasswordResetToken(user);
            emailService.sendPasswordResetEmail(user, token);
        }

        return genericPasswordResetResponse();
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenService.findValidPasswordResetToken(request.getToken());
        if (resetToken == null) {
            throw new AuthException("Invalid or expired password reset token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenService.markPasswordResetTokenUsed(resetToken);

        return new MessageResponse("Password reset successfully. You can now log in with your new password.");
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.from(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private MessageResponse genericVerificationResponse() {
        return new MessageResponse(
                "If an account with that email exists and is unverified, a verification email has been sent.");
    }

    private MessageResponse genericPasswordResetResponse() {
        return new MessageResponse(
                "If an account with that email exists, a password reset link has been sent.");
    }
}
