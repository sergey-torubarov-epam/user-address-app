package com.uams.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uams.model.Address;
import com.uams.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringJUnitExtension.class)
@WebMvcTest(AddressRestController.class)
public class AddressRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

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
        address2.setCountry("United States");
    }

    @Test
    void getAllAddresses_ShouldReturnAllAddresses() throws Exception {
        // Arrange
        List<Address> addresses = Arrays.asList(address1, address2);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        // Act & Assert
        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].addressId").value(1))
                .andExpected(jsonPath("$[0].country").value("United States"))
                .andExpect(jsonPath("$[1].addressId").value(2))
                .andExpected(jsonPath("$[1].country").value("United States"));

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void getAddressById_WithExistingId_ShouldReturnAddress() throws Exception {
        // Arrange
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address1));

        // Act & Assert
        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.addressId").value(1))
                .andExpect(jsonPath("$.street").value("123 Main St"))
                .andExpected(jsonPath("$.country").value("United States"));

        verify(addressService, times(1)).getAddressById(1L);
    }

    @Test
    void getAddressById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(addressService.getAddressById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/addresses/3"))
                .andExpect(status().isNotFound());

        verify(addressService, times(1)).getAddressById(3L);
    }

    @Test
    void createAddress_WithValidData_ShouldReturnCreatedAddress() throws Exception {
        // Arrange
        Address newAddress = new Address();
        newAddress.setStreet("789 Pine St");
        newAddress.setCity("Chicago");
        newAddress.setState("IL");
        newAddress.setPincode("60601");
        newAddress.setCountry("United States");

        Address savedAddress = new Address();
        savedAddress.setAddressId(3L);
        savedAddress.setStreet("789 Pine St");
        savedAddress.setCity("Chicago");
        savedAddress.setState("IL");
        savedAddress.setPincode("60601");
        savedAddress.setCountry("United States");

        when(addressService.saveAddress(any(Address.class))).thenReturn(savedAddress);

        // Act & Assert
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.addressId").value(3))
                .andExpect(jsonPath("$.street").value("789 Pine St"))
                .andExpected(jsonPath("$.country").value("United States"));

        verify(addressService, times(1)).saveAddress(any(Address.class));
    }

    @Test
    void createAddress_WithoutCountry_ShouldReturnCreatedAddress() throws Exception {
        // Arrange
        Address newAddress = new Address();
        newAddress.setStreet("789 Pine St");
        newAddress.setCity("Chicago");
        newAddress.setState("IL");
        newAddress.setPincode("60601");
        // No country set (should be null)

        Address savedAddress = new Address();
        savedAddress.setAddressId(3L);
        savedAddress.setStreet("789 Pine St");
        savedAddress.setCity("Chicago");
        savedAddress.setState("IL");
        savedAddress.setPincode("60601");
        savedAddress.setCountry(null);

        when(addressService.saveAddress(any(Address.class))).thenReturn(savedAddress);

        // Act & Assert
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.addressId").value(3))
                .andExpect(jsonPath("$.street").value("789 Pine St"))
                .andExpected(jsonPath("$.country").isEmpty());

        verify(addressService, times(1)).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_WithExistingId_ShouldReturnUpdatedAddress() throws Exception {
        // Arrange
        Address updatedAddress = new Address();
        updatedAddress.setStreet("Updated Street");
        updatedAddress.setCity("Updated City");
        updatedAddress.setState("UC");
        updatedAddress.setPincode("12345");
        updatedAddress.setCountry("Canada");

        Address savedAddress = new Address();
        savedAddress.setAddressId(1L);
        savedAddress.setStreet("Updated Street");
        savedAddress.setCity("Updated City");
        savedAddress.setState("UC");
        savedAddress.setPincode("12345");
        savedAddress.setCountry("Canada");

        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address1));
        when(addressService.saveAddress(any(Address.class))).thenReturn(savedAddress);

        // Act & Assert
        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.addressId").value(1))
                .andExpect(jsonPath("$.street").value("Updated Street"))
                .andExpected(jsonPath("$.country").value("Canada"));

        verify(addressService, times(1)).getAddressById(1L);
        verify(addressService, times(1)).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        Address updatedAddress = new Address();
        updatedAddress.setStreet("Updated Street");
        updatedAddress.setCity("Updated City");
        updatedAddress.setState("UC");
        updatedAddress.setPincode("12345");
        updatedAddress.setCountry("Canada");

        when(addressService.getAddressById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/addresses/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress)))
                .andExpect(status().isNotFound());

        verify(addressService, times(1)).getAddressById(3L);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void deleteAddress_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address1));
        doNothing().when(addressService).deleteAddress(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isNoContent());

        verify(addressService, times(1)).getAddressById(1L);
        verify(addressService, times(1)).deleteAddress(1L);
    }

    @Test
    void deleteAddress_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(addressService.getAddressById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/addresses/3"))
                .andExpect(status().isNotFound());

        verify(addressService, times(1)).getAddressById(3L);
        verify(addressService, never()).deleteAddress(3L);
    }
}