package com.uams.service;

import com.uams.model.Order;
import com.uams.model.User;
import com.uams.repository.OrderRepository;
import com.uams.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Order testOrder;
    private Order testOrder2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        testOrder = new Order();
        testOrder.setId(101L);
        testOrder.setUser(testUser);
        testOrder.setOrderDate(LocalDate.now());
        testOrder.setStatus("Pending");
        testOrder.setPrice(new BigDecimal("50.00"));
        testOrder.setQuantity(2);
        testOrder.setProductDescription("Test Product 1");

        testOrder2 = new Order();
        testOrder2.setId(102L);
        testOrder2.setUser(testUser);
        testOrder2.setOrderDate(LocalDate.now().minusDays(1));
        testOrder2.setStatus("Shipped");
        testOrder2.setPrice(new BigDecimal("25.50"));
        testOrder2.setQuantity(1);
        testOrder2.setProductDescription("Test Product 2");
    }

    @Test
    void findById_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findById(101L)).thenReturn(Optional.of(testOrder));

        Optional<Order> foundOrder = orderService.findById(101L);

        assertTrue(foundOrder.isPresent());
        assertEquals(testOrder.getId(), foundOrder.get().getId());
        verify(orderRepository).findById(101L);
    }

    @Test
    void findById_WhenOrderDoesNotExist_ShouldReturnEmpty() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderService.findById(999L);

        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById(999L);
    }

    @Test
    void findAll_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("orderDate"));
        List<Order> orderList = Arrays.asList(testOrder, testOrder2);
        Page<Order> orderPage = new PageImpl<>(orderList, pageable, orderList.size());

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(null, pageable); // Pass null spec for simplicity

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(testOrder.getId(), result.getContent().get(0).getId());
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

     @Test
    void findByUserId_WhenUserExists_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Order> userOrders = List.of(testOrder);
        Page<Order> orderPage = new PageImpl<>(userOrders, pageable, userOrders.size());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.findByUserId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrder.getId(), result.getContent().get(0).getId());
        verify(userRepository).existsById(1L);
        verify(orderRepository).findByUserId(1L, pageable);
    }

     @Test
    void findByUserId_WhenUserDoesNotExist_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            orderService.findByUserId(99L, pageable);
        });

        verify(userRepository).existsById(99L);
        verify(orderRepository, never()).findByUserId(anyLong(), any(Pageable.class));
    }


    @Test
    void save_WhenUserExists_ShouldSaveAndReturnOrder() {
        Order newOrder = new Order(); // No ID yet
        newOrder.setOrderDate(LocalDate.now());
        newOrder.setStatus("New");
        newOrder.setPrice(new BigDecimal("10.00"));
        newOrder.setQuantity(1);
        newOrder.setProductDescription("New Product");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // Mock the save operation to return the order with an ID and assigned user
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(103L); // Simulate ID generation
            savedOrder.setUser(testUser); // Ensure user is set
            return savedOrder;
        });


        Order savedOrder = orderService.save(newOrder, 1L);

        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getId()); // Check if ID was assigned
        assertEquals(testUser.getId(), savedOrder.getUser().getId());
        assertEquals("New", savedOrder.getStatus());
        verify(userRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void save_WhenUserDoesNotExist_ShouldThrowException() {
        Order newOrder = new Order();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            orderService.save(newOrder, 99L);
        });

        verify(userRepository).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

     @Test
    void update_WhenOrderExists_ShouldUpdateAndReturnOrder() {
        Order updatedDetails = new Order();
        updatedDetails.setStatus("Updated Status");
        updatedDetails.setPrice(new BigDecimal("55.00"));
        // Set other fields as needed for the update
        updatedDetails.setOrderDate(testOrder.getOrderDate());
        updatedDetails.setQuantity(testOrder.getQuantity());
        updatedDetails.setProductDescription(testOrder.getProductDescription());


        when(orderRepository.findById(101L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the modified order

        Order result = orderService.update(101L, updatedDetails);

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Updated Status", result.getStatus());
        assertEquals(0, new BigDecimal("55.00").compareTo(result.getPrice())); // Use compareTo for BigDecimal
        verify(orderRepository).findById(101L);
        verify(orderRepository).save(testOrder); // Verify save was called on the original object after modification
    }

     @Test
    void update_WhenOrderDoesNotExist_ShouldThrowException() {
        Order updatedDetails = new Order();
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            orderService.update(999L, updatedDetails);
        });

        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }


    @Test
    void deleteById_WhenOrderExists_ShouldCallDelete() {
         when(orderRepository.existsById(101L)).thenReturn(true);
         doNothing().when(orderRepository).deleteById(101L); // Use doNothing for void methods

         orderService.deleteById(101L);

         verify(orderRepository).existsById(101L);
         verify(orderRepository).deleteById(101L); // Verify deleteById was called
    }

     @Test
    void deleteById_WhenOrderDoesNotExist_ShouldThrowException() {
         when(orderRepository.existsById(999L)).thenReturn(false);

         assertThrows(EntityNotFoundException.class, () -> {
             orderService.deleteById(999L);
         });

         verify(orderRepository).existsById(999L);
         verify(orderRepository, never()).deleteById(anyLong());
    }
}
