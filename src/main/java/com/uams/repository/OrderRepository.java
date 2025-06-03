package com.uams.repository;

import com.uams.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Order entities.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Add custom query methods if needed
}