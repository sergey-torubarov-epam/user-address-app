package com.uams.integration;

import com.uams.model.Address;
import com.uams.repository.AddressRepository;
import com.uams.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressCountryIntegrationTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void saveAddress_WithCountry_ShouldPersistCountryField() {
        // Arrange
        Address address = new Address();
        address.setStreet("123 Integration Test St");
        address.setCity("Test City");
        address.setState("TS");
        address.setPincode("12345");
        address.setCountry("United States");

        // Act
        Address savedAddress = addressService.saveAddress(address);

        // Assert
        assertNotNull(savedAddress.getAddressId());
        assertEquals("United States", savedAddress.getCountry());

        // Verify persistence
        Optional<Address> retrievedAddress = addressService.getAddressById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals("United States", retrievedAddress.get().getCountry());
    }

    @Test
    void saveAddress_WithoutCountry_ShouldPersistNullCountry() {
        // Arrange
        Address address = new Address();
        address.setStreet("456 No Country St");
        address.setCity("Test City");
        address.setState("TS");
        address.setPincode("54321");
        // No country set (should be null)

        // Act
        Address savedAddress = addressService.saveAddress(address);

        // Assert
        assertNotNull(savedAddress.getAddressId());
        assertNull(savedAddress.getCountry());

        // Verify persistence
        Optional<Address> retrievedAddress = addressService.getAddressById(savedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertNull(retrievedAddress.get().getCountry());
    }

    @Test
    void updateAddress_AddCountry_ShouldUpdateCountryField() {
        // Arrange - Create address without country
        Address address = new Address();
        address.setStreet("789 Update Test St");
        address.setCity("Test City");
        address.setState("TS");
        address.setPincode("67890");
        Address savedAddress = addressService.saveAddress(address);
        assertNull(savedAddress.getCountry());

        // Act - Update to add country
        savedAddress.setCountry("Canada");
        Address updatedAddress = addressService.saveAddress(savedAddress);

        // Assert
        assertEquals("Canada", updatedAddress.getCountry());

        // Verify persistence
        Optional<Address> retrievedAddress = addressService.getAddressById(updatedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertEquals("Canada", retrievedAddress.get().getCountry());
    }

    @Test
    void updateAddress_RemoveCountry_ShouldSetCountryToNull() {
        // Arrange - Create address with country
        Address address = new Address();
        address.setStreet("321 Remove Country St");
        address.setCity("Test City");
        address.setState("TS");
        address.setPincode("09876");
        address.setCountry("Germany");
        Address savedAddress = addressService.saveAddress(address);
        assertEquals("Germany", savedAddress.getCountry());

        // Act - Update to remove country
        savedAddress.setCountry(null);
        Address updatedAddress = addressService.saveAddress(savedAddress);

        // Assert
        assertNull(updatedAddress.getCountry());

        // Verify persistence
        Optional<Address> retrievedAddress = addressService.getAddressById(updatedAddress.getAddressId());
        assertTrue(retrievedAddress.isPresent());
        assertNull(retrievedAddress.get().getCountry());
    }

    @Test
    void findAllAddresses_ShouldIncludeCountryField() {
        // Arrange - Create addresses with and without country
        Address address1 = new Address();
        address1.setStreet("111 With Country St");
        address1.setCity("Test City");
        address1.setState("TS");
        address1.setPincode("11111");
        address1.setCountry("France");

        Address address2 = new Address();
        address2.setStreet("222 Without Country St");
        address2.setCity("Test City");
        address2.setState("TS");
        address2.setPincode("22222");
        // No country

        addressService.saveAddress(address1);
        addressService.saveAddress(address2);

        // Act
        var addresses = addressService.getAllAddresses();

        // Assert
        assertTrue(addresses.size() >= 2);
        
        // Find our test addresses
        Optional<Address> foundAddress1 = addresses.stream()
                .filter(a -> "111 With Country St".equals(a.getStreet()))
                .findFirst();
        Optional<Address> foundAddress2 = addresses.stream()
                .filter(a -> "222 Without Country St".equals(a.getStreet()))
                .findFirst();

        assertTrue(foundAddress1.isPresent());
        assertEquals("France", foundAddress1.get().getCountry());

        assertTrue(foundAddress2.isPresent());
        assertNull(foundAddress2.get().getCountry());
    }
}