package com.uams.service;

import com.uams.model.Product;
import java.util.List;

/**
 * Service interface for managing Product entities.
 */
public interface ProductService {

    /**
     * Retrieves all products.
     *
     * @return A list of all products.
     */
    List<Product> getAllProducts();

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The product with the specified ID.
     */
    Product getProductById(Long id);

    /**
     * Saves a product (creates a new one or updates an existing one).
     *
     * @param product The product to save.
     * @return The saved product.
     */
    Product saveProduct(Product product);

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     */
    void deleteProduct(Long id);
}