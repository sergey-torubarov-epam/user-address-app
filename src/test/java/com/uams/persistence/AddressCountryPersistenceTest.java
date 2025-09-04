package com.uams.persistence;

import com.uams.model.Address;
import com.uams.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Field persistence tests for Country field functionality
 * Implements requirement from EPMCDMETST-14153
 */
@DataJpaTest
@ActiveProfiles("test")
@org.springframework.test.annotation.DirtiesContext
public class AddressCountryPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testCountryFieldPersistence_WithValidCountry() {
        // Create and save address with country
        Address address = createTestAddress();
        address.setCountry("United States");

        Address savedAddress = addressRepository.save(address);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify
        Optional<Address> retrievedAddress = addressRepository.findById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals("United States", retrievedAddress.get().getCountry());
    }

    @Test
    public void testCountryFieldPersistence_WithNullCountry() {
        // Create and save address without country
        Address address = createTestAddress();
        address.setCountry(null);

        Address savedAddress = addressRepository.save(address);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify null is preserved
        Optional<Address> retrievedAddress = addressRepository.findById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertNull(retrievedAddress.get().getCountry());
    }

    @Test
    public void testCountryFieldPersistence_WithEmptyCountry() {
        // Create and save address with empty country
        Address address = createTestAddress();
        address.setCountry("");

        Address savedAddress = addressRepository.save(address);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify empty string is preserved
        Optional<Address> retrievedAddress = addressRepository.findById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals("", retrievedAddress.get().getCountry());
    }

    @Test
    public void testCountryFieldPersistence_WithLongCountryName() {
        // Test with maximum length country name (100 characters)
        String longCountryName = "A".repeat(100);
        
        Address address = createTestAddress();
        address.setCountry(longCountryName);

        Address savedAddress = addressRepository.save(address);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify long name is preserved
        Optional<Address> retrievedAddress = addressRepository.findById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals(longCountryName, retrievedAddress.get().getCountry());
    }

    @Test
    public void testCountryFieldUpdate() {
        // Create and save address
        Address address = createTestAddress();
        address.setCountry("Canada");
        Address savedAddress = addressRepository.save(address);
        entityManager.flush();

        // Update country
        savedAddress.setCountry("Mexico");
        Address updatedAddress = addressRepository.save(savedAddress);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify update
        Optional<Address> retrievedAddress = addressRepository.findById(updatedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals("Mexico", retrievedAddress.get().getCountry());
    }

    @Test
    public void testCountryFieldInQuery() {
        // Create addresses with different countries
        Address address1 = createTestAddress();
        address1.setCountry("United States");
        addressRepository.save(address1);

        Address address2 = createTestAddress();
        address2.setCountry("Canada");
        addressRepository.save(address2);

        Address address3 = createTestAddress();
        address3.setCountry(null);
        addressRepository.save(address3);

        entityManager.flush();

        // Test finding by country (would require custom repository method)
        // For now, just verify all addresses are saved
        assertEquals(3, addressRepository.findAll().size());
    }

    @Test
    public void testDatabaseConstraints() {
        // Test that country field allows NULL as per requirements
        Address address = createTestAddress();
        address.setCountry(null);

        // Should not throw exception
        assertDoesNotThrow(() -> {
            addressRepository.save(address);
            entityManager.flush();
        });
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setPincode("12345");
        return address;
    }
}