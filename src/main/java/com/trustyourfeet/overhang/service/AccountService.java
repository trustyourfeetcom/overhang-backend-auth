package com.trustyourfeet.overhang.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trustyourfeet.overhang.dto.AccountLoginRequestDto;
import com.trustyourfeet.overhang.dto.AccountLoginResponseDto;
import com.trustyourfeet.overhang.dto.AccountRegistrationRequestDto;
import com.trustyourfeet.overhang.dto.AccountRegistrationResponseDto;
import com.trustyourfeet.overhang.entity.Account;
import com.trustyourfeet.overhang.event.AccountRegistrationEvent;
import com.trustyourfeet.overhang.exception.EmailAlreadyExistsException;
import com.trustyourfeet.overhang.exception.InvalidLoginCredentialsException;
import com.trustyourfeet.overhang.exception.UsernameAlreadyExistsException;
import com.trustyourfeet.overhang.repository.AccountRepository;
import com.trustyourfeet.overhang.security.JwtService;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, JwtService jwtService,
            KafkaProducerService kafkaProducerService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountLoginResponseDto loginAccount(AccountLoginRequestDto request) {
        Optional<Account> accountOptional = accountRepository.findByUsername(request.getUsername());

        if (accountOptional.isPresent() &&
                passwordEncoder.matches(
                        request.getPassword(),
                        accountOptional.get().getPassword())) {
            String token = jwtService.generateToken(request.getUsername());
            return new AccountLoginResponseDto(token);
        }

        throw new InvalidLoginCredentialsException("Invalid username or password");
    }

    public AccountRegistrationResponseDto registerAccount(AccountRegistrationRequestDto request) {
        logger.debug("Attempting to register account: {}", request.getUsername());

        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Account registration failed, email already exists: {}",
                    request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("Account registration failed, username already exists: {}",
                    request.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);

        AccountRegistrationEvent accountRegistrationEvent = new AccountRegistrationEvent(
                account.getId(),
                account.getUsername(),
                account.getEmail());
        kafkaProducerService.sendRegistrationEvent(accountRegistrationEvent);

        String token = jwtService.generateToken(request.getUsername());

        logger.info("User registered successfully: {}", request.getUsername());
        return new AccountRegistrationResponseDto(token);
    }
}
