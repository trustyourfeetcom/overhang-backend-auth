package com.overhang.backend.dto;

public class UserRegistrationResponseDTO {
    private String token;

    // Constructor
    public UserRegistrationResponseDTO(String token) {
        this.token = token;
    }

    // Getters
    public String getToken() {
        return token;
    }
}
