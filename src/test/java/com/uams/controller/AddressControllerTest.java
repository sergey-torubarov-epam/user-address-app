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
        address.setCountry("USA");
        address.setUsers(new HashSet<>());
    }

    @Test
    void listAddresses_ShouldAddAddressesToModelAndReturnListView() throws Exception {
        when(addressService.getAllAddresses()).thenReturn(Arrays.asList(address));

        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addresses"))
                .andExpect(view().name("address/list"));

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void showCreateForm_ShouldAddNewAddressToModelAndReturnFormView() throws Exception {
        mockMvc.perform(get("/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(view().name("address/form"));
    }

    @Test
    void createAddress_WithValidData_ShouldSaveAddressAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        assertEquals("redirect:/addresses", viewName);
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void createAddress_WithEmptyCountry_ShouldReturnFormWithErrors() {
        address.setCountry("");
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = addressController.createAddress(address, bindingResult, redirectAttributes);

        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void showEditForm_WithExistingId_ShouldAddAddressToModelAndReturnFormView() throws Exception {
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address));

        mockMvc.perform(get("/addresses/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("address"))
                .andExpect(view().name("address/form"));

        verify(addressService, times(1)).getAddressById(1L);
    }

    @Test
    void showEditForm_WithNonExistingId_ShouldRedirectToAddressesList() throws Exception {
        when(addressService.getAddressById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/addresses/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        verify(addressService, times(1)).getAddressById(99L);
    }

    @Test
    void updateAddress_WithValidData_ShouldUpdateAddressAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(addressService.saveAddress(any(Address.class))).thenReturn(address);

        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        assertEquals("redirect:/addresses", viewName);
        assertEquals(1L, address.getAddressId());
        verify(addressService, times(1)).saveAddress(address);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void updateAddress_WithInvalidData_ShouldReturnFormWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_WithInvalidCountry_ShouldReturnFormWithErrors() {
        address.setCountry("");
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = addressController.updateAddress(1L, address, bindingResult, redirectAttributes);

        assertEquals("address/form", viewName);
        verify(addressService, never()).saveAddress(any(Address.class));
    }

    @Test
    void deleteAddress_ShouldDeleteAddressAndRedirect() throws Exception {
        doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(get("/addresses/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        verify(addressService, times(1)).deleteAddress(1L);
    }
}