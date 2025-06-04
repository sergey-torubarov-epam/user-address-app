package com.uams.controller;

import com.uams.model.Address;
import com.uams.model.User;
import com.uams.service.AddressService;
import com.uams.service.UserService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User user;
    private Address address;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setUserId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMobileNumber("1234567890");
        user.setPassword("password");
        user.setAddresses(new HashSet<>());

        address = new Address();
        address.setAddressId(1L);
        address.setBuildingName("Building A");
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setPincode("10001");
        address.setUsers(new HashSet<>());
    }

    @Test
    void listUsers_ShouldAddUsersToModelAndReturnListView() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("user/list"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void showCreateForm_ShouldAddNewUserToModelAndReturnFormView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/form"));
    }

    @Test
    void createUser_WithValidData_ShouldSaveUserAndRedirect() {
        // Arrange
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        // Act
        String viewName = userController.createUser(user, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/users", viewName);
        verify(userService, times(1)).existsByEmail(user.getEmail());
        verify(userService, times(1)).saveUser(user);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createUser_WithExistingEmail_ShouldReturnFormWithError() {
        // Arrange
        when(userService.existsByEmail(anyString())).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.createUser(user, bindingResult, redirectAttributes);

        // Assert
        assertEquals("user/form", viewName);
        verify(userService, times(1)).existsByEmail(user.getEmail());
        verify(bindingResult, times(1)).rejectValue(eq("email"), anyString(), anyString());
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void showEditForm_WithExistingId_ShouldAddUserToModelAndReturnFormView() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/form"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void showEditForm_WithNonExistingId_ShouldRedirectToUsersList() throws Exception {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/users/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).getUserById(99L);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRedirect() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(get("/users/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void viewUserAddresses_WithExistingId_ShouldAddUserAndAddressesToModelAndReturnAddressesView() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(addressService.getAllAddresses()).thenReturn(Arrays.asList(address));

        // Act & Assert
        mockMvc.perform(get("/users/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("addresses"))
                .andExpect(model().attributeExists("allAddresses"))
                .andExpect(model().attributeExists("newAddress"))
                .andExpect(view().name("user/addresses"));

        verify(userService, times(1)).getUserById(1L);
        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void viewUserAddresses_WithNonExistingId_ShouldRedirectToUsersList() throws Exception {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/users/99/addresses"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).getUserById(99L);
        verify(addressService, never()).getAllAddresses();
    }
}