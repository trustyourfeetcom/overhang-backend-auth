package com.overhang.backend.dto;

public class UserLoginResponseDTO {
    private String token;

    // Constructor
    public UserLoginResponseDTO(String token) {
        this.token = token;
    }

    // Getters
    public String getToken() {
        return token;
    }
}
