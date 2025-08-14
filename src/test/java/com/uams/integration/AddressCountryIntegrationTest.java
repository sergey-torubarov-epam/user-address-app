package com.uams.integration;

import com.uams.model.Address;
import com.uams.repository.AddressRepository;
import com.uams.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Country field functionality
 * Implements requirement from EPMCDMETST-14155
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressCountryIntegrationTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private Validator validator;

    @Test
    public void testCountryFieldPersistence() {
        // Create address with country
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setPincode("12345");
        address.setCountry("United States");

        // Save address
        Address savedAddress = addressService.saveAddress(address);
        assertNotNull(savedAddress.getAddressId());

        // Retrieve and verify country field
        Address retrievedAddress = addressService.getAddressById(savedAddress.getAddressId()).orElse(null);
        assertNotNull(retrievedAddress);
        assertEquals("United States", retrievedAddress.getCountry());
    }

    @Test
    public void testCountryFieldValidation_ValidCountry() {
        Address address = createValidAddress();
        address.setCountry("Canada");

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertTrue(violations.isEmpty(), "Valid country should not produce validation errors");
    }

    @Test
    public void testCountryFieldValidation_InvalidCountry() {
        Address address = createValidAddress();
        address.setCountry("InvalidCountryName");

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty(), "Invalid country should produce validation errors");
        
        boolean hasCountryViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
        assertTrue(hasCountryViolation, "Should have country validation violation");
    }

    @Test
    public void testCountryFieldValidation_NullCountry() {
        Address address = createValidAddress();
        address.setCountry(null);

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        boolean hasCountryViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
        assertFalse(hasCountryViolation, "Null country should be allowed");
    }

    @Test
    public void testCountryFieldValidation_EmptyCountry() {
        Address address = createValidAddress();
        address.setCountry("");

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        boolean hasCountryViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
        assertFalse(hasCountryViolation, "Empty country should be allowed");
    }

    @Test
    public void testFormIntegration() {
        // Test that country field integrates correctly with form submission
        Address address = new Address();
        address.setStreet("456 Integration St");
        address.setCity("Integration City");
        address.setState("Integration State");
        address.setPincode("54321");
        address.setCountry("France");

        // Simulate form submission
        Address savedAddress = addressService.saveAddress(address);
        assertNotNull(savedAddress);
        assertEquals("France", savedAddress.getCountry());

        // Verify persistence
        Address retrievedAddress = addressRepository.findById(savedAddress.getAddressId()).orElse(null);
        assertNotNull(retrievedAddress);
        assertEquals("France", retrievedAddress.getCountry());
    }

    private Address createValidAddress() {
        Address address = new Address();
        address.setStreet("123 Valid Street");
        address.setCity("Valid City");
        address.setState("Valid State");
        address.setPincode("12345");
        return address;
    }
}