package com.trustyourfeet.overhang.dto;

public class AccountRegistrationResponseDto {
    private final String token;

    public AccountRegistrationResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
