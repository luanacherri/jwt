package com.example.controller;

import com.example.controller.dto.JwtValidationResponse;
import com.example.service.JwtValidationService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JwtValidationResponse> validateJwtPost(@RequestBody String jwt) {
        try {
            boolean isValid = jwtValidationService.validateJwt(jwt);
            JwtValidationResponse response = isValid 
                ? JwtValidationResponse.valid() 
                : JwtValidationResponse.invalid("JWT inválido");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(JwtValidationResponse.invalid(e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<JwtValidationResponse> validateJwtGet(@RequestParam String jwt) {
        try {
            boolean isValid = jwtValidationService.validateJwt(jwt);
            JwtValidationResponse response = isValid 
                ? JwtValidationResponse.valid() 
                : JwtValidationResponse.invalid("JWT inválido");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(JwtValidationResponse.invalid(e.getMessage()));
        }
    }
}
