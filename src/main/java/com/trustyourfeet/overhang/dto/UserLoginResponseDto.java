package com.trustyourfeet.overhang.dto;

public class UserLoginResponseDto {
    private final String token;

    public UserLoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
