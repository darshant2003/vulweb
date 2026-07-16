package com.vulnweb.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Frontend frontend = new Frontend();
    private Auth auth = new Auth();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Frontend getFrontend() {
        return frontend;
    }

    public void setFrontend(Frontend frontend) {
        this.frontend = frontend;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Jwt {
        private String secret;
        private long expirationMs;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Frontend {
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Auth {
        private boolean requireEmailVerification = true;
        private int verificationTokenExpiryHours = 24;
        private int passwordResetTokenExpiryHours = 1;

        public boolean isRequireEmailVerification() {
            return requireEmailVerification;
        }

        public void setRequireEmailVerification(boolean requireEmailVerification) {
            this.requireEmailVerification = requireEmailVerification;
        }

        public int getVerificationTokenExpiryHours() {
            return verificationTokenExpiryHours;
        }

        public void setVerificationTokenExpiryHours(int verificationTokenExpiryHours) {
            this.verificationTokenExpiryHours = verificationTokenExpiryHours;
        }

        public int getPasswordResetTokenExpiryHours() {
            return passwordResetTokenExpiryHours;
        }

        public void setPasswordResetTokenExpiryHours(int passwordResetTokenExpiryHours) {
            this.passwordResetTokenExpiryHours = passwordResetTokenExpiryHours;
        }
    }
}
