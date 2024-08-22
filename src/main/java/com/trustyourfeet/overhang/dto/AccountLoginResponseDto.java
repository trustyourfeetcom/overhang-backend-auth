package com.trustyourfeet.overhang.dto;

public class AccountLoginResponseDto {
    private final String token;

    public AccountLoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
