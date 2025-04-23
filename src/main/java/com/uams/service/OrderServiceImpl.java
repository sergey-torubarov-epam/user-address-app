package com.uams.service;

import com.uams.model.Order;
import com.uams.model.User;
import com.uams.repository.OrderRepository;
import com.uams.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository; // Needed to associate orders with users

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }

     @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
             throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Order save(Order order, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        order.setUser(user);
        // Default values are handled by @PrePersist in Order entity
        return orderRepository.save(order);
    }

     @Override
    @Transactional
    public Order update(Long orderId, Order orderDetails) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Update fields - User association should generally not be changed here
        // unless specifically required.
        existingOrder.setOrderDate(orderDetails.getOrderDate());
        existingOrder.setStatus(orderDetails.getStatus());
        existingOrder.setShippingDate(orderDetails.getShippingDate());
        existingOrder.setPrice(orderDetails.getPrice());
        existingOrder.setQuantity(orderDetails.getQuantity());
        existingOrder.setProductDescription(orderDetails.getProductDescription());
        // createdAt and user are not updated
        // updatedAt is handled by @PreUpdate

        return orderRepository.save(existingOrder);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
         if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
