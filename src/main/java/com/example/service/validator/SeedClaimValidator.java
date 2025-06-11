package com.example.service.validator;

/**
 * Validador para a claim Seed
 */
public class SeedClaimValidator implements ClaimValidator {
    
    @Override
    public boolean isValid(Object value) {
        try {
            String seedStr = String.valueOf(value);
            int seed = Integer.parseInt(seedStr);
            return isPrime(seed);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getClaimName() {
        return "Seed";
    }

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
