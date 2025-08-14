package com.uams.controller;

import com.uams.model.Address;
import com.uams.model.Country;
import com.uams.service.AddressService;
import com.uams.service.CountryService;
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
    private CountryService countryService;

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
    private Country country;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();

        country = new Country();
        country.setCountryId(1L);
        country.setCountryName("United States");
        country.setCountryCode("US");

        address = new Address();
        address.setAddressId(1L);
        address.setBuildingName("Building A");
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setPincode("10001");
        address.setCountry(country);
        address.setUsers(new HashSet<>());
    }

    @Test
    void listAddresses_ShouldAddAddressesToModelAndReturnListView() throws Exception {
        // Arrange
        when(addressService.getAllAddresses()).thenReturn(Arrays.asList(address));

        // Act & Assert
        mockMvc.perform(get("/addresses"))
                .andExpected().isOk())
                .andExpect(model().attributeExists("addresses"))
                .andExpect(view().name("address/list"));

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void showCreateForm_ShouldAddNewAddressToModelAndReturnFormView() throws Exception {
        // Arrange
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country));

        // Act & Assert
        mockMvc.perform(get("/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(view().name("address/form"));

        verify(countryService, times(1)).getAllCountries();
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
    void createAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country));

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    void createAddress_WithCountryAndValidData_ShouldSaveAddressAndRedirect() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(country, address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithoutCountry_ShouldStillSaveAddress() {
        // Arrange
        address.setCountry(null);
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
    void showEditForm_WithExistingId_ShouldAddAddressToModelAndReturnFormView() throws Exception {
        // Arrange
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address));
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country));

        // Act & Assert
        mockMvc.perform(get("/addresses/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(view().name("address/form"));

        verify(addressService, times(1)).getAddressById(1L);
        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    void showEditForm_WithNonExistingId_ShouldRedirectToAddressesList() throws Exception {
        // Arrange
        when(addressService.getAddressById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/addresses/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        verify(addressService, times(1)).getAddressById(99L);
        verify(countryService, never()).getAllCountries();
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
    void updateAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country));

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    void updateAddress_WithCountryAndValidData_ShouldUpdateAddressAndRedirect() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        // Act
        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/addresses", viewName);
        assertEquals(1L, address.getAddressId());
        assertEquals(country, address.getCountry());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void updateAddress_WithoutCountry_ShouldStillUpdateAddress() {
        // Arrange
        address.setCountry(null);
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
    void deleteAddress_ShouldDeleteAddressAndRedirect() throws Exception {
        // Arrange
        doNothing().when(addressService).deleteAddress(1L);

        // Act & Assert
        mockMvc.perform(get("/addresses/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        verify(addressService, times(1)).deleteAddress(1L);
    }
}