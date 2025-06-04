package com.uams.service;

import com.uams.model.User;
import com.uams.repository.UserRepository;
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
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUserId(1L);
        user1.setEmail("john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setMobileNumber("1234567890");
        user1.setPassword("password");

        user2 = new User();
        user2.setUserId(2L);
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setMobileNumber("0987654321");
        user2.setPassword("password");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
        assertEquals(user2.getEmail(), users.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user1.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(3L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user1);

        // Act
        User savedUser = userService.saveUser(user1);

        // Assert
        assertNotNull(savedUser);
        assertEquals(user1.getEmail(), savedUser.getEmail());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void deleteUser_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail("john@example.com");

        // Assert
        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("john@example.com");
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean exists = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail("nonexistent@example.com");
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user1));

        // Act
        Optional<User> result = userService.findByEmail("john@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user1.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}