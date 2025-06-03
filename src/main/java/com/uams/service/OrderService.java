package com.uams.service;

import com.uams.model.Order;
import java.util.List;

/**
 * Service interface for managing Order entities.
 */
public interface OrderService {

    /**
     * Retrieves all orders.
     *
     * @return A list of all orders.
     */
    List<Order> getAllOrders();

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The order with the specified ID.
     */
    Order getOrderById(Long id);

    /**
     * Saves an order (creates a new one or updates an existing one).
     *
     * @param order The order to save.
     * @return The saved order.
     */
    Order saveOrder(Order order);

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     */
    void deleteOrder(Long id);
}