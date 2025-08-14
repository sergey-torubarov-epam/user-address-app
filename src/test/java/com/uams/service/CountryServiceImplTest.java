package com.uams.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CountryServiceImplTest {

    private CountryServiceImpl countryService;

    @BeforeEach
    void setUp() {
        countryService = new CountryServiceImpl();
    }

    @Test
    void getAllCountries_ShouldReturnListOfCountries() {
        // Act
        List<String> countries = countryService.getAllCountries();

        // Assert
        assertNotNull(countries);
        assertFalse(countries.isEmpty());
        assertTrue(countries.contains("United States"));
        assertTrue(countries.contains("Canada"));
        assertTrue(countries.contains("United Kingdom"));
        assertTrue(countries.contains("Germany"));
        assertTrue(countries.contains("France"));
    }

    @Test
    void getAllCountries_ShouldReturnConsistentList() {
        // Act
        List<String> countries1 = countryService.getAllCountries();
        List<String> countries2 = countryService.getAllCountries();

        // Assert
        assertEquals(countries1.size(), countries2.size());
        assertTrue(countries1.containsAll(countries2));
    }

    @Test
    void isValidCountry_WithValidCountry_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(countryService.isValidCountry("United States"));
        assertTrue(countryService.isValidCountry("Canada"));
        assertTrue(countryService.isValidCountry("United Kingdom"));
        assertTrue(countryService.isValidCountry("Germany"));
    }

    @Test
    void isValidCountry_WithInvalidCountry_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(countryService.isValidCountry("Invalid Country"));
        assertFalse(countryService.isValidCountry("Fake Nation"));
        assertFalse(countryService.isValidCountry("Test Country"));
    }

    @Test
    void isValidCountry_WithNullCountry_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(countryService.isValidCountry(null));
    }

    @Test
    void isValidCountry_WithEmptyCountry_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(countryService.isValidCountry(""));
        assertTrue(countryService.isValidCountry("   "));
    }

    @Test
    void isValidCountry_WithCaseSensitivity_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(countryService.isValidCountry("united states")); // lowercase
        assertFalse(countryService.isValidCountry("UNITED STATES")); // uppercase
        assertFalse(countryService.isValidCountry("United states")); // mixed case
    }

    @Test
    void getAllCountries_ShouldContainExpectedCountries() {
        // Arrange
        List<String> countries = countryService.getAllCountries();

        // Assert - Test for some key countries
        assertTrue(countries.contains("Afghanistan"));
        assertTrue(countries.contains("Zimbabwe"));
        assertTrue(countries.contains("Brazil"));
        assertTrue(countries.contains("India"));
        assertTrue(countries.contains("China"));
        assertTrue(countries.contains("Japan"));
        assertTrue(countries.contains("Australia"));
    }
}