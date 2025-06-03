package com.uams.repository;

import com.uams.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Add custom query methods if needed
}