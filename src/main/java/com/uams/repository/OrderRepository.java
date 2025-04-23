package com.uams.repository;

import com.uams.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // For filtering
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // Find orders by user ID, with pagination
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Find orders by user ID (simple list)
    List<Order> findByUserId(Long userId);

}
