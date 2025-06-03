package com.uams.controller;

import com.uams.model.Product;
import com.uams.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling Product-related HTTP requests.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handles GET request to display all products.
     */
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product/list";
    }

    /**
     * Handles GET request to display the form for creating a new product.
     */
    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form";
    }

    /**
     * Handles POST request to create a new product.
     */
    @PostMapping
    public String createProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    /**
     * Handles GET request to display the form for editing an existing product.
     */
    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product/form";
    }

    /**
     * Handles POST request to update an existing product.
     */
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setProductId(id);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    /**
     * Handles GET request to delete a product.
     */
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}