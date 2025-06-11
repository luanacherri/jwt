package com.example.service.validator;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DefaultClaimValidatorFactory implements ClaimValidatorFactory {
    @Override
    public List<ClaimValidator> createValidators() {
        return List.of(
            new NameClaimValidator(),
            new RoleClaimValidator(),
            new SeedClaimValidator()
        );
    }
}
