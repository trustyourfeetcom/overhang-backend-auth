package com.overhang.backend.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.overhang.backend.dto.UserLoginRequestDTO;
import com.overhang.backend.dto.UserLoginResponseDTO;
import com.overhang.backend.dto.UserRegistrationRequestDTO;
import com.overhang.backend.dto.UserRegistrationResponseDTO;
import com.overhang.backend.entity.Account;
import com.overhang.backend.exception.InvalidLoginCredentialsException;
import com.overhang.backend.exception.UsernameAlreadyExistsException;
import com.overhang.backend.repository.AccountRepository;
import com.overhang.backend.security.JwtService;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final AccountRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AccountRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserLoginResponseDTO performUserLogin(UserLoginRequestDTO userLoginRequestDTO) {
        Optional<Account> userOptional = userRepository.findByUsername(userLoginRequestDTO.getUsername());

        if (userOptional.isPresent() &&
                passwordEncoder.matches(
                        userLoginRequestDTO.getPassword(),
                        userOptional.get().getPassword())) {
            String token = jwtService.generateToken(userLoginRequestDTO.getUsername());
            return new UserLoginResponseDTO(token);
        }

        throw new InvalidLoginCredentialsException("Invalid username or password");
    }

    public UserRegistrationResponseDTO createUserAccount(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        LOGGER.debug("Attempting to register user: {}", userRegistrationRequestDTO.getUsername());
        if (userRepository.findByUsername(userRegistrationRequestDTO.getUsername()).isPresent()) {
            LOGGER.warn("User registration failed, username already exists: {}",
                    userRegistrationRequestDTO.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Account user = new Account();
        user.setUsername(userRegistrationRequestDTO.getUsername());
        user.setEmail(userRegistrationRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDTO.getPassword()));
        userRepository.save(user);

        LOGGER.info("User registered successfully: {}", userRegistrationRequestDTO.getUsername());

        String token = jwtService.generateToken(userRegistrationRequestDTO.getUsername());
        return new UserRegistrationResponseDTO(token);
    }
}
