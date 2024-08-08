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
import com.trustyourfeet.overhang.util.ResponseHandler;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<Object> performUserLogin(
            @RequestBody UserLoginRequestDto userLoginRequestDTO) {
        LOGGER.info("User login attempt for username: {}", userLoginRequestDTO.getUsername());

        UserLoginResponseDto loginResponseDTO = authService.performUserLogin(userLoginRequestDTO);
        LOGGER.info("User login successful for username: {}", userLoginRequestDTO.getUsername());

        return ResponseHandler.generateResponse(HttpStatus.OK,
                "Login successful",
                loginResponseDTO,
                null);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> createUserAccount(
            @RequestBody UserRegistrationRequestDto userCreationRequestDTO) {
        LOGGER.info("Account registration attempt for username: {}", userCreationRequestDTO.getUsername());

        UserRegistrationResponseDto userCreationResponseDTO = authService.createUserAccount(userCreationRequestDTO);
        LOGGER.info("Account registration successful for username: {}", userCreationRequestDTO.getUsername());

        return ResponseHandler.generateResponse(HttpStatus.CREATED,
                "Registration successful",
                userCreationResponseDTO,
                null);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UsernameAlreadyExistsException ex) {
        LOGGER.warn("User registration failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(
                HttpStatus.CONFLICT,
                "User registration failed",
                null,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(
            InvalidLoginCredentialsException ex) {
        LOGGER.warn("User login failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid login credentials",
                null,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<Object> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
        LOGGER.warn("JWT validation failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,
                "Invalid JWT token",
                null,
                ex.getMessage());
    }
}
