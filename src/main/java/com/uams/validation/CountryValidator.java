package com.uams.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for Country field to ensure valid country names
 * Implements requirement from EPMCDMETST-14148
 */
public class CountryValidator implements ConstraintValidator<ValidCountry, String> {

    private static final Set<String> VALID_COUNTRIES = new HashSet<>(Arrays.asList(
        "United States", "Canada", "United Kingdom", "Germany", "France", 
        "Italy", "Spain", "Australia", "Japan", "China", "India", "Brazil",
        "Mexico", "Argentina", "South Africa", "Russia", "Netherlands",
        "Belgium", "Switzerland", "Sweden", "Norway", "Denmark", "Finland"
    ));

    @Override
    public void initialize(ValidCountry constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        // Allow null values as country is optional
        if (country == null || country.trim().isEmpty()) {
            return true;
        }
        
        // Validate against predefined list of countries
        return VALID_COUNTRIES.contains(country.trim());
    }
}