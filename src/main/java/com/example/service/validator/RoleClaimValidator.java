package com.example.service.validator;

import java.util.Set;

/**
 * Validador para a claim Role
 */
public class RoleClaimValidator implements ClaimValidator {
    private static final Set<String> VALID_ROLES = Set.of("Admin", "Member", "External");

    @Override
    public boolean isValid(Object value) {
        if (!(value instanceof String role)) {
            return false;
        }
        return VALID_ROLES.contains(role);
    }

    @Override
    public String getClaimName() {
        return "Role";
    }
}
