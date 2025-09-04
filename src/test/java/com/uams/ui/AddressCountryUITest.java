package com.uams.ui;

import com.uams.model.Address;
import com.uams.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// UI tests don't need database configuration
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UI/UX tests for Country field functionality
 * Implements requirement from EPMCDMETST-14154
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.annotation.DirtiesContext
public class AddressCountryUITest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Test
    public void testAddressListDisplaysCountryColumn() throws Exception {
        // Setup test data
        Address address1 = createTestAddress(1L, "United States");
        Address address2 = createTestAddress(2L, "Canada");
        
        when(addressService.getAllAddresses()).thenReturn(Arrays.asList(address1, address2));

        // Test address list page
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Country")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("United States")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Canada")));
    }

    @Test
    public void testAddressFormContainsCountryField() throws Exception {
        // Test new address form
        mockMvc.perform(get("/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Country")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"country\"")));
    }

    @Test
    public void testAddressEditFormDisplaysCountryValue() throws Exception {
        // Setup test data
        Address address = createTestAddress(1L, "Germany");
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(address));

        // Test edit address form
        mockMvc.perform(get("/addresses/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Germany")));
    }

    @Test
    public void testAddressFormSubmissionWithCountry() throws Exception {
        // Setup mock service
        Address savedAddress = createTestAddress(1L, "France");
        when(addressService.saveAddress(any(Address.class))).thenReturn(savedAddress);

        // Test form submission with country
        mockMvc.perform(post("/addresses")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("pincode", "12345")
                .param("country", "France"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));
    }

    @Test
    public void testAddressFormSubmissionWithoutCountry() throws Exception {
        // Setup mock service
        Address savedAddress = createTestAddress(1L, null);
        when(addressService.saveAddress(any(Address.class))).thenReturn(savedAddress);

        // Test form submission without country (should be allowed)
        mockMvc.perform(post("/addresses")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("pincode", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));
    }

    @Test
    public void testAddressFormValidationWithInvalidCountry() throws Exception {
        // Test form submission with invalid country
        mockMvc.perform(post("/addresses")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("pincode", "12345")
                .param("country", "InvalidCountryName"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid country name")));
    }

    private Address createTestAddress(Long id, String country) {
        Address address = new Address();
        address.setAddressId(id);
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setPincode("12345");
        address.setCountry(country);
        return address;
    }
}