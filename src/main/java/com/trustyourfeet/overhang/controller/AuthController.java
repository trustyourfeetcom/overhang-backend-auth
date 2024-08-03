package com.trustyourfeet.overhang.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trustyourfeet.overhang.dto.UserLoginRequestDto;
import com.trustyourfeet.overhang.dto.UserLoginResponseDto;
import com.trustyourfeet.overhang.dto.UserRegistrationRequestDto;
import com.trustyourfeet.overhang.dto.UserRegistrationResponseDto;
import com.trustyourfeet.overhang.exception.InvalidJwtTokenException;
import com.trustyourfeet.overhang.exception.InvalidLoginCredentialsException;
import com.trustyourfeet.overhang.exception.UsernameAlreadyExistsException;
import com.trustyourfeet.overhang.service.AuthService;
import com.trustyourfeet.overhang.util.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> performUserLogin(
            @RequestBody UserLoginRequestDto userLoginRequestDTO) {
        LOGGER.info("User login attempt for username: {}", userLoginRequestDTO.getUsername());
        UserLoginResponseDto loginResponseDTO = authService.performUserLogin(userLoginRequestDTO);
        LOGGER.info("User login successful for username: {}", userLoginRequestDTO.getUsername());

        ApiResponse<UserLoginResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                loginResponseDTO, null);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<UserRegistrationResponseDto>> createUserAccount(
            @RequestBody UserRegistrationRequestDto userCreationRequestDTO) {
        LOGGER.info("Account registration attempt for username: {}", userCreationRequestDTO.getUsername());
        UserRegistrationResponseDto userCreationResponseDTO = authService.createUserAccount(userCreationRequestDTO);
        LOGGER.info("Account registration successful for username: {}", userCreationRequestDTO.getUsername());

        ApiResponse<UserRegistrationResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Registration successful",
                userCreationResponseDTO,
                null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(UsernameAlreadyExistsException ex) {
        LOGGER.warn("User registration failed: {}", ex.getMessage());

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.CONFLICT.value(),
                "User registration failed",
                null,
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(InvalidLoginCredentialsException ex) {
        LOGGER.warn("User login failed: {}", ex.getMessage());

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid login credentials",
                null,
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
        LOGGER.warn("JWT validation failed: {}", ex.getMessage());

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid JWT token",
                null,
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
