package com.example.controller;

import com.example.service.JwtValidationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
@CrossOrigin  // Permite chamadas de qualquer origem
public class JwtController {
    
    private final JwtValidationService jwtValidationService;

    public JwtController(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @PostMapping("/validate")
    public boolean validateJwtPost(@RequestBody String jwt) {
        return jwtValidationService.validateJwt(jwt);
    }

    @GetMapping("/validate")
    public boolean validateJwtGet(@RequestParam String jwt) {
        return jwtValidationService.validateJwt(jwt);
    }
}
