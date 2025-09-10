package com.uams.integration;

import com.uams.model.Address;
import com.uams.repository.AddressRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("Address Country Field Integration Tests")
public class AddressCountryFieldIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        addressRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /addresses - Create address with country field")
    void testCreateAddressWithCountry() throws Exception {
        mockMvc.perform(post("/addresses")
                .param("buildingName", "Test Building")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("country", "United States")
                .param("pincode", "12345")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        // Verify address was saved with country
        assertEquals(1, addressRepository.count());
        Address savedAddress = addressRepository.findAll().get(0);
        assertEquals("United States", savedAddress.getCountry());
    }

    @Test
    @DisplayName("POST /addresses - Create address without country field (backward compatibility)")
    void testCreateAddressWithoutCountry() throws Exception {
        mockMvc.perform(post("/addresses")
                .param("buildingName", "Test Building")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("pincode", "12345")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        // Verify address was saved without country (null)
        assertEquals(1, addressRepository.count());
        Address savedAddress = addressRepository.findAll().get(0);
        assertNull(savedAddress.getCountry());
    }

    @Test
    @DisplayName("POST /addresses - Validate country field with invalid characters")
    void testCreateAddressWithInvalidCountry() throws Exception {
        mockMvc.perform(post("/addresses")
                .param("buildingName", "Test Building")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("country", "United States 123") // Invalid: contains numbers
                .param("pincode", "12345")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()) // Should return to form with validation errors
                .andExpect(view().name("address/form"));

        // Verify address was not saved
        assertEquals(0, addressRepository.count());
    }

    @Test
    @DisplayName("GET /addresses - Display addresses with country field")
    void testListAddressesWithCountry() throws Exception {
        // Create test address with country
        Address address = new Address();
        address.setBuildingName("Test Building");
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Canada");
        address.setPincode("12345");
        addressRepository.save(address);

        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/list"))
                .andExpect(model().attributeExists("addresses"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Canada")));
    }

    @Test
    @DisplayName("PUT /addresses/{id} - Update address with country field")
    void testUpdateAddressWithCountry() throws Exception {
        // Create initial address without country
        Address address = new Address();
        address.setBuildingName("Test Building");
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setPincode("12345");
        Address savedAddress = addressRepository.save(address);

        // Update address with country
        mockMvc.perform(post("/addresses/" + savedAddress.getAddressId())
                .param("buildingName", "Updated Building")
                .param("street", "123 Test Street")
                .param("city", "Test City")
                .param("state", "Test State")
                .param("country", "United Kingdom")
                .param("pincode", "12345")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addresses"));

        // Verify address was updated with country
        Address updatedAddress = addressRepository.findById(savedAddress.getAddressId()).orElse(null);
        assertNotNull(updatedAddress);
        assertEquals("United Kingdom", updatedAddress.getCountry());
        assertEquals("Updated Building", updatedAddress.getBuildingName());
    }

    @Test
    @DisplayName("GET /addresses/new - Show create form with country field")
    void testShowCreateFormWithCountryField() throws Exception {
        mockMvc.perform(get("/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("address/form"))
                .andExpect(model().attributeExists("address"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Country")));
    }

    @Test
    @DisplayName("GET /addresses/{id}/edit - Show edit form with country field populated")
    void testShowEditFormWithCountryField() throws Exception {
        // Create address with country
        Address address = new Address();
        address.setBuildingName("Test Building");
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Australia");
        address.setPincode("12345");
        Address savedAddress = addressRepository.save(address);

        mockMvc.perform(get("/addresses/" + savedAddress.getAddressId() + "/edit"))
                .andExpect(status().isOk())
                .andExpected(view().name("address/form"))
                .andExpect(model().attributeExists("address"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Australia")));
    }

    @Test
    @DisplayName("Country field validation - Accept valid country names")
    void testCountryFieldValidation() throws Exception {
        String[] validCountries = {
            "United States",
            "United Kingdom",
            "South Africa",
            "New Zealand",
            "Costa Rica",
            "Bosnia-Herzegovina"
        };

        for (String country : validCountries) {
            // Clear repository for each test
            addressRepository.deleteAll();
            
            mockMvc.perform(post("/addresses")
                    .param("buildingName", "Test Building")
                    .param("street", "123 Test Street")
                    .param("city", "Test City")
                    .param("state", "Test State")
                    .param("country", country)
                    .param("pincode", "12345")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/addresses"));

            // Verify address was saved
            assertEquals(1, addressRepository.count(), 
                "Country '" + country + "' should be valid");
            
            Address savedAddress = addressRepository.findAll().get(0);
            assertEquals(country, savedAddress.getCountry());
        }
    }
}