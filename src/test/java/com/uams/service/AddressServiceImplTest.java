package com.uams.service;

import com.uams.model.Address;
import com.uams.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address1;
    private Address address2;

    @BeforeEach
    void setUp() {
        address1 = new Address();
        address1.setAddressId(1L);
        address1.setBuildingName("Building A");
        address1.setStreet("123 Main St");
        address1.setCity("New York");
        address1.setState("NY");
        address1.setPincode("10001");
        address1.setCountry("United States");

        address2 = new Address();
        address2.setAddressId(2L);
        address2.setBuildingName("Building B");
        address2.setStreet("456 Oak Ave");
        address2.setCity("Los Angeles");
        address2.setState("CA");
        address2.setPincode("90001");
        address2.setCountry("Canada");
    }

    @Test
    void getAllAddresses_ShouldReturnAllAddresses() {
        // Arrange
        when(addressRepository.findAll()).thenReturn(Arrays.asList(address1, address2));

        // Act
        List<Address> addresses = addressService.getAllAddresses();

        // Assert
        assertEquals(2, addresses.size());
        assertEquals(address1.getStreet(), addresses.get(0).getStreet());
        assertEquals(address2.getStreet(), addresses.get(1).getStreet());
        assertEquals(address1.getCountry(), addresses.get(0).getCountry());
        assertEquals(address2.getCountry(), addresses.get(1).getCountry());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void getAddressById_WithExistingId_ShouldReturnAddress() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));

        // Act
        Optional<Address> result = addressService.getAddressById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(address1.getStreet(), result.get().getStreet());
        assertEquals(address1.getCountry(), result.get().getCountry());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAddressById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(addressRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Address> result = addressService.getAddressById(3L);

        // Assert
        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(3L);
    }

    @Test
    void saveAddress_ShouldReturnSavedAddress() {
        // Arrange
        when(addressRepository.save(any(Address.class))).thenReturn(address1);

        // Act
        Address savedAddress = addressService.saveAddress(address1);

        // Assert
        assertNotNull(savedAddress);
        assertEquals(address1.getStreet(), savedAddress.getStreet());
        assertEquals(address1.getCountry(), savedAddress.getCountry());
        verify(addressRepository, times(1)).save(address1);
    }

    @Test
    void deleteAddress_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(addressRepository).deleteById(1L);

        // Act
        addressService.deleteAddress(1L);

        // Assert
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void saveAddress_WithCountryField_ShouldReturnAddressWithCountry() {
        // Arrange
        Address addressWithCountry = new Address();
        addressWithCountry.setAddressId(3L);
        addressWithCountry.setBuildingName("Building C");
        addressWithCountry.setStreet("789 Pine St");
        addressWithCountry.setCity("Chicago");
        addressWithCountry.setState("IL");
        addressWithCountry.setPincode("60601");
        addressWithCountry.setCountry("United States");

        when(addressRepository.save(any(Address.class))).thenReturn(addressWithCountry);

        // Act
        Address savedAddress = addressService.saveAddress(addressWithCountry);

        // Assert
        assertNotNull(savedAddress);
        assertEquals("United States", savedAddress.getCountry());
        assertEquals("789 Pine St", savedAddress.getStreet());
        assertEquals("Chicago", savedAddress.getCity());
        verify(addressRepository, times(1)).save(addressWithCountry);
    }

    @Test
    void getAddressById_ShouldReturnAddressWithAllFieldsIncludingCountry() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));

        // Act
        Optional<Address> result = addressService.getAddressById(1L);

        // Assert
        assertTrue(result.isPresent());
        Address retrievedAddress = result.get();
        assertEquals("123 Main St", retrievedAddress.getStreet());
        assertEquals("New York", retrievedAddress.getCity());
        assertEquals("NY", retrievedAddress.getState());
        assertEquals("10001", retrievedAddress.getPincode());
        assertEquals("United States", retrievedAddress.getCountry());
        assertEquals("Building A", retrievedAddress.getBuildingName());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAllAddresses_ShouldReturnAddressesWithValidCountryFields() {
        // Arrange
        when(addressRepository.findAll()).thenReturn(Arrays.asList(address1, address2));

        // Act
        List<Address> addresses = addressService.getAllAddresses();

        // Assert
        assertEquals(2, addresses.size());
        
        Address firstAddress = addresses.get(0);
        assertNotNull(firstAddress.getCountry());
        assertEquals("United States", firstAddress.getCountry());
        
        Address secondAddress = addresses.get(1);
        assertNotNull(secondAddress.getCountry());
        assertEquals("Canada", secondAddress.getCountry());
        
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void saveAddress_WithNullCountry_ShouldHandleGracefully() {
        // Arrange
        Address addressWithNullCountry = new Address();
        addressWithNullCountry.setAddressId(4L);
        addressWithNullCountry.setBuildingName("Building D");
        addressWithNullCountry.setStreet("321 Elm St");
        addressWithNullCountry.setCity("Miami");
        addressWithNullCountry.setState("FL");
        addressWithNullCountry.setPincode("33101");
        addressWithNullCountry.setCountry(null);

        when(addressRepository.save(any(Address.class))).thenReturn(addressWithNullCountry);

        // Act
        Address savedAddress = addressService.saveAddress(addressWithNullCountry);

        // Assert
        assertNotNull(savedAddress);
        assertNull(savedAddress.getCountry());
        assertEquals("321 Elm St", savedAddress.getStreet());
        verify(addressRepository, times(1)).save(addressWithNullCountry);
    }
}