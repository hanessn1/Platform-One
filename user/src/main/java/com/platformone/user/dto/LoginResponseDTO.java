package com.platformone.user.dto;

public class LoginResponseDTO {
    private String email;
    private String token;
    private String role;

    public LoginResponseDTO(String email, String token, String role) {
        this.email = email;
        this.token = token;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", role=" + role +
                '}';
    }
}