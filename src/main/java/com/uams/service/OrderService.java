package com.uams.service;

import com.uams.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * Finds an order by its ID.
     * @param id The ID of the order.
     * @return An Optional containing the order if found, otherwise empty.
     */
    Optional<Order> findById(Long id);

    /**
     * Finds all orders with pagination and optional filtering/sorting.
     * @param spec Specification for filtering (can be null).
     * @param pageable Pageable object for pagination and sorting.
     * @return A Page of orders.
     */
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

     /**
     * Finds all orders for a specific user with pagination.
     * @param userId The ID of the user.
     * @param pageable Pageable object for pagination and sorting.
     * @return A Page of orders for the specified user.
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Saves an order (creates new or updates existing).
     * Associates the order with a user based on userId.
     * @param order The order object to save.
     * @param userId The ID of the user to associate the order with.
     * @return The saved order.
     * @throws RuntimeException if the user with the given userId is not found.
     */
    Order save(Order order, Long userId);

     /**
     * Updates an existing order.
     * @param orderId The ID of the order to update.
     * @param orderDetails The order object containing updated details.
     * @return The updated order.
     * @throws RuntimeException if the order with the given orderId is not found.
     */
    Order update(Long orderId, Order orderDetails);


    /**
     * Deletes an order by its ID.
     * @param id The ID of the order to delete.
     * @throws RuntimeException if the order with the given id is not found.
     */
    void deleteById(Long id);
}
