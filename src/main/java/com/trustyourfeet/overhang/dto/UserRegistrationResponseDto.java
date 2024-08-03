package com.trustyourfeet.overhang.dto;

public class UserRegistrationResponseDto {
    private final String token;

    public UserRegistrationResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
