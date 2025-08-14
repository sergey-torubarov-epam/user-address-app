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
    private Address addressWithoutCountry;

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
        address2.setCountry("USA");

        addressWithoutCountry = new Address();
        addressWithoutCountry.setAddressId(3L);
        addressWithoutCountry.setBuildingName("Building C");
        addressWithoutCountry.setStreet("789 Pine Rd");
        addressWithoutCountry.setCity("Miami");
        addressWithoutCountry.setState("FL");
        addressWithoutCountry.setPincode("33101");
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
    void getAllAddresses_WithCountryAndWithoutCountry_ShouldReturnAllAddresses() {
        // Arrange
        when(addressRepository.findAll()).thenReturn(Arrays.asList(address1, addressWithoutCountry));

        // Act
        List<Address> addresses = addressService.getAllAddresses();

        // Assert
        assertEquals(2, addresses.size());
        assertEquals("USA", addresses.get(0).getCountry());
        assertNull(addresses.get(1).getCountry());
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
    void getAddressById_WithExistingIdAndCountry_ShouldReturnAddressWithCountry() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));

        // Act
        Optional<Address> result = addressService.getAddressById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("USA", result.get().getCountry());
        assertEquals("123 Main St", result.get().getStreet());
        assertEquals("New York", result.get().getCity());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAddressById_WithExistingIdWithoutCountry_ShouldReturnAddressWithoutCountry() {
        // Arrange
        when(addressRepository.findById(3L)).thenReturn(Optional.of(addressWithoutCountry));

        // Act
        Optional<Address> result = addressService.getAddressById(3L);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().getCountry());
        assertEquals("789 Pine Rd", result.get().getStreet());
        assertEquals("Miami", result.get().getCity());
        verify(addressRepository, times(1)).findById(3L);
    }

    @Test
    void getAddressById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Address> result = addressService.getAddressById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(99L);
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
    void saveAddress_WithCountry_ShouldReturnSavedAddressWithCountry() {
        // Arrange
        Address addressWithCountry = new Address();
        addressWithCountry.setBuildingName("International Building");
        addressWithCountry.setStreet("100 Global St");
        addressWithCountry.setCity("London");
        addressWithCountry.setState("England");
        addressWithCountry.setPincode("SW1A 1AA");
        addressWithCountry.setCountry("United Kingdom");

        when(addressRepository.save(any(Address.class))).thenReturn(addressWithCountry);

        // Act
        Address savedAddress = addressService.saveAddress(addressWithCountry);

        // Assert
        assertNotNull(savedAddress);
        assertEquals("United Kingdom", savedAddress.getCountry());
        assertEquals("100 Global St", savedAddress.getStreet());
        assertEquals("London", savedAddress.getCity());
        verify(addressRepository, times(1)).save(addressWithCountry);
    }

    @Test
    void saveAddress_WithoutCountry_ShouldReturnSavedAddressWithoutCountry() {
        // Arrange
        when(addressRepository.save(any(Address.class))).thenReturn(addressWithoutCountry);

        // Act
        Address savedAddress = addressService.saveAddress(addressWithoutCountry);

        // Assert
        assertNotNull(savedAddress);
        assertNull(savedAddress.getCountry());
        assertEquals("789 Pine Rd", savedAddress.getStreet());
        assertEquals("Miami", savedAddress.getCity());
        verify(addressRepository, times(1)).save(addressWithoutCountry);
    }

    @Test
    void saveAddress_WithNullCountry_ShouldSaveSuccessfully() {
        // Arrange
        Address addressWithNullCountry = new Address();
        addressWithNullCountry.setBuildingName("Test Building");
        addressWithNullCountry.setStreet("Test Street");
        addressWithNullCountry.setCity("Test City");
        addressWithNullCountry.setState("Test State");
        addressWithNullCountry.setPincode("12345");
        addressWithNullCountry.setCountry(null);

        when(addressRepository.save(any(Address.class))).thenReturn(addressWithNullCountry);

        // Act
        Address savedAddress = addressService.saveAddress(addressWithNullCountry);

        // Assert
        assertNotNull(savedAddress);
        assertNull(savedAddress.getCountry());
        assertEquals("Test Street", savedAddress.getStreet());
        verify(addressRepository, times(1)).save(addressWithNullCountry);
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