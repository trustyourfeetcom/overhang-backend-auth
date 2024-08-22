package com.trustyourfeet.overhang.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trustyourfeet.overhang.dto.AccountLoginRequestDto;
import com.trustyourfeet.overhang.dto.AccountLoginResponseDto;
import com.trustyourfeet.overhang.dto.AccountRegistrationRequestDto;
import com.trustyourfeet.overhang.dto.AccountRegistrationResponseDto;
import com.trustyourfeet.overhang.exception.EmailAlreadyExistsException;
import com.trustyourfeet.overhang.exception.InvalidJwtTokenException;
import com.trustyourfeet.overhang.exception.InvalidLoginCredentialsException;
import com.trustyourfeet.overhang.exception.UsernameAlreadyExistsException;
import com.trustyourfeet.overhang.service.AccountService;
import com.trustyourfeet.overhang.util.ResponseHandler;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<Object> performUserLogin(
            @RequestBody AccountLoginRequestDto request) {
        logger.info("User login attempt for username: {}", request.getUsername());

        AccountLoginResponseDto response = accountService.loginAccount(request);
        logger.info("User login successful for username: {}", request.getUsername());

        return ResponseHandler.generateResponse(HttpStatus.OK,
                "Login successful",
                response,
                null);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> registerAccount(
            @RequestBody AccountRegistrationRequestDto request) {
        logger.info("Account registration attempt for username: {}", request.getUsername());

        AccountRegistrationResponseDto response = accountService.registerAccount(request);
        logger.info("Account registration successful for username: {}", request.getUsername());

        return ResponseHandler.generateResponse(HttpStatus.CREATED,
                "Registration successful",
                response,
                null);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        logger.warn("Email registration failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(
                HttpStatus.CONFLICT,
                "Email registration failed",
                null,
                ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UsernameAlreadyExistsException ex) {
        logger.warn("User registration failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(
                HttpStatus.CONFLICT,
                "User registration failed",
                null,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(
            InvalidLoginCredentialsException ex) {
        logger.warn("User login failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid login credentials",
                null,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<Object> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
        logger.warn("JWT validation failed: {}", ex.getMessage());
        return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,
                "Invalid JWT token",
                null,
                ex.getMessage());
    }
}
