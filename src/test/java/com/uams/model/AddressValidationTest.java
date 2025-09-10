package com.uams.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Country Field Validation Tests")
public class AddressValidationTest {

    private Validator validator;
    private Address address;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Create a valid address for testing
        address = new Address();
        address.setBuildingName("Test Building");
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setPincode("12345");
    }

    @Test
    @DisplayName("Valid country names should pass validation")
    void testValidCountryNames() {
        // Test valid country names
        String[] validCountries = {
            "United States",
            "United Kingdom", 
            "South Africa",
            "New Zealand",
            "Costa Rica",
            "Bosnia-Herzegovina",
            "Saint-Pierre-et-Miquelon",
            "India",
            "Canada",
            null, // null should be allowed
            ""    // empty string should be allowed
        };

        for (String country : validCountries) {
            address.setCountry(country);
            Set<ConstraintViolation<Address>> violations = validator.validate(address);
            
            // Filter violations to only country field
            long countryViolations = violations.stream()
                .filter(v -> "country".equals(v.getPropertyPath().toString()))
                .count();
                
            assertEquals(0, countryViolations, 
                "Country '" + country + "' should be valid but got violations: " + violations);
        }
    }

    @Test
    @DisplayName("Invalid country names should fail validation")
    void testInvalidCountryNames() {
        // Test invalid country names with numbers
        String[] invalidCountries = {
            "United States 123",
            "Country1",
            "Test@Country",
            "Country#1",
            "Country$",
            "Country%",
            "Country&",
            "Country*",
            "Country+",
            "Country=",
            "Country!",
            "Country?",
            "Country/",
            "Country\\",
            "Country|",
            "Country<>",
            "Country[]",
            "Country{}",
            "Country()",
            "Country\"",
            "Country'",
            "Country;",
            "Country:",
            "Country,",
            "Country."
        };

        for (String country : invalidCountries) {
            address.setCountry(country);
            Set<ConstraintViolation<Address>> violations = validator.validate(address);
            
            // Filter violations to only country field
            long countryViolations = violations.stream()
                .filter(v -> "country".equals(v.getPropertyPath().toString()))
                .count();
                
            assertTrue(countryViolations > 0, 
                "Country '" + country + "' should be invalid but passed validation");
        }
    }

    @Test
    @DisplayName("Country name exceeding 100 characters should fail validation")
    void testCountryNameTooLong() {
        // Create a string longer than 100 characters
        String longCountry = "A".repeat(101);
        address.setCountry(longCountry);
        
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        
        // Filter violations to only country field
        long countryViolations = violations.stream()
            .filter(v -> "country".equals(v.getPropertyPath().toString()))
            .count();
            
        assertTrue(countryViolations > 0, 
            "Country name with 101 characters should fail validation");
    }

    @Test
    @DisplayName("Country name with exactly 100 characters should pass validation")
    void testCountryNameExactly100Characters() {
        // Create a string with exactly 100 characters
        String maxLengthCountry = "A".repeat(100);
        address.setCountry(maxLengthCountry);
        
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        
        // Filter violations to only country field
        long countryViolations = violations.stream()
            .filter(v -> "country".equals(v.getPropertyPath().toString()))
            .count();
            
        assertEquals(0, countryViolations, 
            "Country name with exactly 100 characters should pass validation");
    }

    @Test
    @DisplayName("Country field should be optional")
    void testCountryFieldIsOptional() {
        // Don't set country field (should be null)
        address.setCountry(null);
        
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        
        // Filter violations to only country field
        long countryViolations = violations.stream()
            .filter(v -> "country".equals(v.getPropertyPath().toString()))
            .count();
            
        assertEquals(0, countryViolations, 
            "Country field should be optional (null allowed)");
    }

    @Test
    @DisplayName("Address with country field should maintain all other validations")
    void testOtherFieldValidationsStillWork() {
        address.setCountry("United States");
        address.setStreet(null); // This should cause validation error
        
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        
        // Should have violation for street field
        boolean hasStreetViolation = violations.stream()
            .anyMatch(v -> "street".equals(v.getPropertyPath().toString()));
            
        assertTrue(hasStreetViolation, 
            "Street field validation should still work when country is present");
    }
}