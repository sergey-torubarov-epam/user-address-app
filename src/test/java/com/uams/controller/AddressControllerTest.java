package com.uams.controller;

import com.uams.model.Address;
import com.uams.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AddressController addressController;

    private MockMvc mockMvc;
    private Address address;
    private Address addressWithoutCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();

        address = new Address();
        address.setAddressId(1L);
        address.setBuildingName("Building A");
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setPincode("10001");
        address.setCountry("United States");
        address.setUsers(new HashSet<>());

        // Address without country for backward compatibility testing
        addressWithoutCountry = new Address();
        addressWithoutCountry.setAddressId(2L);
        addressWithoutCountry.setBuildingName("Building B");
        addressWithoutCountry.setStreet("456 Oak Ave");
        addressWithoutCountry.setCity("Los Angeles");
        addressWithoutCountry.setState("CA");
        addressWithoutCountry.setPincode("90001");
        addressWithoutCountry.setUsers(new HashSet<>());
    }

    @Test
    void listAddresses_ShouldAddAddressesToModelAndReturnListView() throws Exception {
        // Arrange
        when(addressService.getAllAddresses()).thenReturn(Arrays.asList(address, addressWithoutCountry));

        // Act & Assert
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addresses"))
                .andExpect(view().name("address/list"));

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void showCreateForm_ShouldAddNewAddressToModelAndReturnFormView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(view().name("address/form"));
    }

    @Test
    void createAddress_WithValidData_ShouldSaveAddressAndRedirect() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithValidCountryField_ShouldSaveAddressAndRedirect() {
        // Arrange
        address.setCountry("United Kingdom");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals("United Kingdom", address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithCountryContainingHyphens_ShouldSaveAddressAndRedirect() {
        // Arrange
        address.setCountry("Bosnia-Herzegovina");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals("Bosnia-Herzegovina", address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithoutCountryField_ShouldSaveAddressAndRedirect() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(addressWithoutCountry);

        // Act
        String viewName = addressController.createAddress(addressWithoutCountry, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        verify(addressService, times(1)).saveAddress(addressWithoutCountry);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithInvalidCountryContainingNumbers_ShouldReturnFormWithErrors() {
        // Arrange
        address.setCountry("Country123");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("country")).thenReturn(
            new FieldError("address", "country", "Country must contain only letters, spaces, and hyphens"));

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void createAddress_WithInvalidCountryContainingSpecialChars_ShouldReturnFormWithErrors() {
        // Arrange
        address.setCountry("United@States");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("country")).thenReturn(
            new FieldError("address", "country", "Country must contain only letters, spaces, and hyphens"));

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void createAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void showEditForm_WithExistingId_ShouldAddAddressToModelAndReturnFormView() throws Exception {
        // Arrange
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address));

        // Act & Assert
        mockMvc.perform(get("/addresses/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(view().name("address/form"));

        verify(addressService, times(1)).getAddressById(1L);
    }

    @Test
    void showEditForm_WithExistingIdWithoutCountry_ShouldAddAddressToModelAndReturnFormView() throws Exception {
        // Arrange
        when(addressService.getAddressById(2L)).thenReturn(Optional.of(addressWithoutCountry));

        // Act & Assert
        mockMvc.perform(get("/addresses/2/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(view().name("address/form"));

        verify(addressService, times(1)).getAddressById(2L);
    }

    @Test
    void showEditForm_WithNonExistingId_ShouldRedirectToAddressesList() throws Exception {
        // Arrange
        when(addressService.getAddressById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/addresses/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpected(redirectedUrl("/addresses"));

        verify(addressService, times(1)).getAddressById(99L);
    }

    @Test
    void updateAddress_WithValidData_ShouldUpdateAddressAndRedirect() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(1L, address.getAddressId());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void updateAddress_WithValidCountryUpdate_ShouldUpdateAddressAndRedirect() {
        // Arrange
        address.setCountry("Canada");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(1L, address.getAddressId());
        assertEquals("Canada", address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void updateAddress_AddingCountryToExistingAddress_ShouldUpdateAddressAndRedirect() {
        // Arrange
        addressWithoutCountry.setCountry("Australia");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(addressWithoutCountry);

        // Act
        String viewName = addressController.updateAddress(2L, addressWithoutCountry, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(2L, addressWithoutCountry.getAddressId());
        assertEquals("Australia", addressWithoutCountry.getCountry());
        verify(addressService, times(1)).saveAddress(addressWithoutCountry);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void updateAddress_WithInvalidCountryData_ShouldReturnFormWithErrors() {
        // Arrange
        address.setCountry("Invalid@Country");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("country")).thenReturn(
            new FieldError("address", "country", "Country must contain only letters, spaces, and hyphens"));

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void deleteAddress_ShouldDeleteAddressAndRedirect() throws Exception {
        // Arrange
        doNothing().when(addressService).deleteAddress(1L);

        // Act & Assert
        mockMvc.perform(get("/addresses/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpected(redirectedUrl("/addresses"));

        verify(addressService, times(1)).deleteAddress(1L);
    }

    @Test
    void deleteAddress_WithoutCountryField_ShouldDeleteAddressAndRedirect() throws Exception {
        // Arrange
        doNothing().when(addressService).deleteAddress(2L);

        // Act & Assert
        mockMvc.perform(get("/addresses/2/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpected(redirectedUrl("/addresses"));

        verify(addressService, times(1)).deleteAddress(2L);
    }

    @Test
    void createAddress_WithValidCountrySpacesAndHyphens_ShouldSaveAddressAndRedirect() {
        // Arrange
        address.setCountry("United States of America");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals("United States of America", address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithCountryContainingOnlySpaces_ShouldReturnFormWithErrors() {
        // Arrange
        address.setCountry("   ");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("country")).thenReturn(
            new FieldError("address", "country", "Country cannot be empty or contain only spaces"));

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_RemovingCountryFromExistingAddress_ShouldUpdateAddressAndRedirect() {
        // Arrange
        address.setCountry(null);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(1L, address.getAddressId());
        assertEquals(null, address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }
}