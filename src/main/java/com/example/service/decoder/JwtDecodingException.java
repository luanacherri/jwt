package com.example.service.decoder;

import com.example.service.exception.JwtValidationException;

public class JwtDecodingException extends JwtValidationException {
    public JwtDecodingException(String message) {
        super(message);
    }

    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
