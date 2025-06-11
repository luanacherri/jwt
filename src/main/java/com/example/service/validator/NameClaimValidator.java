package com.example.service.validator;

/**
 * Validador para a claim Name
 */
public class NameClaimValidator implements ClaimValidator {
    private static final int MAX_LENGTH = 256;

    @Override
    public boolean isValid(Object value) {
        if (!(value instanceof String name)) {
            return false;
        }
        return name != null &&
               name.length() <= MAX_LENGTH &&
               !name.matches(".*\\d.*");
    }

    @Override
    public String getClaimName() {
        return "Name";
    }
}
