package com.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtValidationResponse {
    private final boolean valid;
    private final String type;
    private final String title;
    private final String detail;
    private final String instance;

    private JwtValidationResponse(Builder builder) {
        this.valid = builder.valid;
        this.type = builder.type;
        this.title = builder.title;
        this.detail = builder.detail;
        this.instance = builder.instance;
    }

    public boolean isValid() {
        return valid;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean valid;
        private String type;
        private String title;
        private String detail;
        private String instance;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public JwtValidationResponse build() {
            return new JwtValidationResponse(this);
        }
    }

    public static JwtValidationResponse valid() {
        return builder()
            .valid(true)
            .type("https://api.validator.com/jwt/valid")
            .title("JWT Válido")
            .build();
    }

    public static JwtValidationResponse invalid(String detail) {
        return builder()
            .valid(false)
            .type("https://api.validator.com/problems/invalid-jwt")
            .title("JWT Inválido")
            .detail(detail)
            .build();
    }
}
