package com.uams.service;

import com.uams.model.Order;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order saveOrder(Order order);
    void deleteOrder(Long id);
}