package com.overhang.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.overhang.backend.dto.UserLoginRequestDTO;
import com.overhang.backend.dto.UserLoginResponseDTO;
import com.overhang.backend.dto.UserRegistrationRequestDTO;
import com.overhang.backend.dto.UserRegistrationResponseDTO;
import com.overhang.backend.exception.InvalidJwtTokenException;
import com.overhang.backend.exception.InvalidLoginCredentialsException;
import com.overhang.backend.exception.UsernameAlreadyExistsException;
import com.overhang.backend.service.AuthService;
import com.overhang.backend.util.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<UserLoginResponseDTO>> performUserLogin(
            @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        LOGGER.info("User login attempt for username: {}", userLoginRequestDTO.getUsername());
        UserLoginResponseDTO loginResponseDTO = authService.performUserLogin(userLoginRequestDTO);
        LOGGER.info("User login successful for username: {}", userLoginRequestDTO.getUsername());

        ApiResponse<UserLoginResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                loginResponseDTO, null);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<UserRegistrationResponseDTO>> createUserAccount(
            @RequestBody UserRegistrationRequestDTO userCreationRequestDTO) {
        LOGGER.info("Account registration attempt for username: {}", userCreationRequestDTO.getUsername());
        UserRegistrationResponseDTO userCreationResponseDTO = authService.createUserAccount(userCreationRequestDTO);
        LOGGER.info("Account registration successful for username: {}", userCreationRequestDTO.getUsername());

        ApiResponse<UserRegistrationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Registration successful",
                userCreationResponseDTO,
                null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Exceptions

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
