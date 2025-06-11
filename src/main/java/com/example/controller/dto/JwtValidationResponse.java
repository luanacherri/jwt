package com.example.controller.dto;

public class JwtValidationResponse {
    private final boolean valid;
    private final String message;

    public JwtValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public static JwtValidationResponse valid() {
        return new JwtValidationResponse(true, "JWT válido");
    }

    public static JwtValidationResponse invalid(String reason) {
        return new JwtValidationResponse(false, reason);
    }
}
