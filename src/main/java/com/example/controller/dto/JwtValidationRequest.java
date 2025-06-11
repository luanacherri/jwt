package com.example.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class JwtValidationRequest {
    @NotBlank(message = "O JWT não pode estar vazio")
    private String jwt;

    public JwtValidationRequest() {
    }

    public JwtValidationRequest(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
