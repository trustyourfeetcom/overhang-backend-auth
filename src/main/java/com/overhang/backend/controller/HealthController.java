package com.overhang.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/liveness")
    public String checkLiveness() {
        // Implement any specific liveness checks here
        return "Liveness check passed";
    }

    @GetMapping("/readiness")
    public String checkReadiness() {
        // Implement any specific readiness checks here
        return "Readiness check passed";
    }
}
