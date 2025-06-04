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
        address1.setCountry("USA");

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
}