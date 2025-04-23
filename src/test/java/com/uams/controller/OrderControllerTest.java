package com.uams.controller;

import com.uams.model.Order;
import com.uams.model.User;
import com.uams.service.OrderService;
import com.uams.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class) // Test only the OrderController layer
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Use MockBean for Spring Boot context integration
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Order testOrder1;
    private Order testOrder2;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("Another User");
        userList = Arrays.asList(testUser, testUser2);


        testOrder1 = new Order();
        testOrder1.setId(101L);
        testOrder1.setUser(testUser);
        testOrder1.setOrderDate(LocalDate.now());
        testOrder1.setStatus("Pending");
        testOrder1.setPrice(new BigDecimal("50.00"));
        testOrder1.setQuantity(2);
        testOrder1.setProductDescription("Test Product 1");

        testOrder2 = new Order();
        testOrder2.setId(102L);
        testOrder2.setUser(testUser2); // Different user
        testOrder2.setOrderDate(LocalDate.now().minusDays(1));
        testOrder2.setStatus("Shipped");
        testOrder2.setPrice(new BigDecimal("25.50"));
        testOrder2.setQuantity(1);
        testOrder2.setProductDescription("Test Product 2");
    }

    @Test
    void listOrders_ShouldReturnOrderListPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "orderDate"));
        List<Order> orders = Arrays.asList(testOrder1, testOrder2);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        given(orderService.findAll(any(), any(Pageable.class))).willReturn(orderPage);

        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "orderDate")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/list"))
                .andExpect(model().attributeExists("orderPage"))
                .andExpect(model().attribute("orderPage", hasProperty("content", hasSize(2))))
                .andExpect(model().attribute("orderPage", hasProperty("content", contains(testOrder1, testOrder2))));

        verify(orderService).findAll(any(), any(Pageable.class));
    }

    @Test
    void showCreateOrderForm_ShouldReturnFormViewWithNewOrderAndUsers() throws Exception {
        given(userService.findAll()).willReturn(userList);

        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/form"))
                .andExpect(model().attributeExists("order", "users", "formTitle"))
                .andExpect(model().attribute("order", instanceOf(Order.class)))
                .andExpect(model().attribute("users", hasSize(2)))
                .andExpect(model().attribute("formTitle", "Create New Order"));

        verify(userService).findAll();
    }

    @Test
    void saveOrder_CreateNew_WhenValid_ShouldRedirectToList() throws Exception {
        // Mock the service save method
        given(orderService.save(any(Order.class), eq(1L))).willReturn(testOrder1); // Return the saved order

        mockMvc.perform(post("/orders/save")
                        .param("userId", "1")
                        .param("orderDate", "2023-10-27")
                        .param("status", "Pending")
                        .param("price", "50.00")
                        .param("quantity", "2")
                        .param("productDescription", "Test Product 1")
                        // Add other required fields as params
                )
                .andExpect(status().is3xxRedirection()) // Expect redirect
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(orderService).save(any(Order.class), eq(1L));
    }

     @Test
    void saveOrder_CreateNew_WhenUserNotFound_ShouldReturnFormWithError() throws Exception {
        // Mock the service save method to throw exception
        given(orderService.save(any(Order.class), eq(99L))).willThrow(new EntityNotFoundException("User not found"));
        given(userService.findAll()).willReturn(userList); // Need users for the form redisplay

        mockMvc.perform(post("/orders/save")
                        .param("userId", "99") // Non-existent user
                        .param("orderDate", "2023-10-27")
                        .param("status", "Pending")
                        .param("price", "50.00")
                        .param("quantity", "2")
                        .param("productDescription", "Test Product 1")
                )
                .andExpect(status().isOk()) // Stays on the same page
                .andExpect(view().name("order/form"))
                .andExpect(model().attributeExists("order", "users", "formTitle"))
                .andExpect(model().attributeHasFieldErrors("order", "user")) // Check for binding result error on 'user' (or global)
                .andExpect(model().attribute("users", hasSize(2)));


        verify(orderService).save(any(Order.class), eq(99L));
        verify(userService).findAll(); // Verify users were fetched again for the form
    }

    @Test
    void saveOrder_CreateNew_WhenValidationFails_ShouldReturnFormWithErrors() throws Exception {
         given(userService.findAll()).willReturn(userList); // Need users for the form redisplay

        mockMvc.perform(post("/orders/save")
                        .param("userId", "1")
                        .param("orderDate", "") // Invalid - empty date
                        .param("status", "Pending")
                        .param("price", "50.00")
                        .param("quantity", "0") // Invalid - quantity < 1
                        .param("productDescription", "") // Invalid - empty description
                )
                .andExpect(status().isOk())
                .andExpect(view().name("order/form"))
                .andExpect(model().attributeExists("order", "users", "formTitle"))
                .andExpect(model().attributeHasFieldErrors("order", "orderDate", "quantity", "productDescription"));

        verify(orderService, never()).save(any(Order.class), anyLong()); // Save should not be called
        verify(userService).findAll();
    }


     @Test
    void saveOrder_Update_WhenValid_ShouldRedirectToList() throws Exception {
        // Mock the service update method
        given(orderService.update(eq(101L), any(Order.class))).willReturn(testOrder1); // Return the updated order

        mockMvc.perform(post("/orders/save")
                        .param("id", "101") // Include ID for update
                        .param("userId", "1") // User ID might still be needed depending on form design
                        .param("orderDate", testOrder1.getOrderDate().toString())
                        .param("status", "Updated Status") // Changed field
                        .param("price", testOrder1.getPrice().toString())
                        .param("quantity", testOrder1.getQuantity().toString())
                        .param("productDescription", testOrder1.getProductDescription())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(orderService).update(eq(101L), any(Order.class));
    }


    @Test
    void showEditOrderForm_WhenOrderExists_ShouldReturnFormViewWithOrder() throws Exception {
        given(orderService.findById(101L)).willReturn(Optional.of(testOrder1));
        given(userService.findAll()).willReturn(userList);

        mockMvc.perform(get("/orders/edit/{id}", 101L))
                .andExpect(status().isOk())
                .andExpect(view().name("order/form"))
                .andExpect(model().attributeExists("order", "users", "formTitle"))
                .andExpect(model().attribute("order", hasProperty("id", equalTo(101L))))
                .andExpect(model().attribute("users", hasSize(2)))
                .andExpect(model().attribute("formTitle", "Edit Order #101"));

        verify(orderService).findById(101L);
        verify(userService).findAll();
    }

    @Test
    void showEditOrderForm_WhenOrderDoesNotExist_ShouldRedirectToListWithError() throws Exception {
        given(orderService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/orders/edit/{id}", 999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(orderService).findById(999L);
        verify(userService, never()).findAll(); // Should not fetch users if order not found
    }

    @Test
    void deleteOrder_WhenOrderExists_ShouldRedirectToListWithSuccess() throws Exception {
        // Mock the service delete method
        doNothing().when(orderService).deleteById(101L); // Use doNothing for void methods

        mockMvc.perform(post("/orders/delete/{id}", 101L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(orderService).deleteById(101L);
    }

     @Test
    void deleteOrder_WhenOrderDoesNotExist_ShouldRedirectToListWithError() throws Exception {
        // Mock the service delete method to throw exception
        doThrow(new EntityNotFoundException("Order not found")).when(orderService).deleteById(999L);

        mockMvc.perform(post("/orders/delete/{id}", 999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(orderService).deleteById(999L);
    }

     @Test
    void viewOrder_WhenOrderExists_ShouldReturnDetailsView() throws Exception {
        given(orderService.findById(101L)).willReturn(Optional.of(testOrder1));

        mockMvc.perform(get("/orders/{id}", 101L))
                .andExpect(status().isOk())
                .andExpect(view().name("order/details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("order", hasProperty("id", equalTo(101L))));

        verify(orderService).findById(101L);
    }

     @Test
    void viewOrder_WhenOrderDoesNotExist_ShouldRedirectToListWithError() throws Exception {
        given(orderService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/orders/{id}", 999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(orderService).findById(999L);
    }
}
