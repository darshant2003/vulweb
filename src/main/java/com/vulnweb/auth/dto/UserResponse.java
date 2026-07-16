package com.vulnweb.auth.dto;

import java.util.UUID;

import com.vulnweb.auth.entity.User;

public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;

    public UserResponse() {
    }

    public UserResponse(UUID id, String firstName, String lastName, String email, boolean emailVerified) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEmailVerified());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
