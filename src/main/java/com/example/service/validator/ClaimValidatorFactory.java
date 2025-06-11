package com.example.service.validator;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface ClaimValidatorFactory {
    List<ClaimValidator> createValidators();
}
