package com.uams.controller;

import com.uams.model.Order;
import com.uams.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling Order-related HTTP requests.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Handles GET request to display all orders.
     */
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list";
    }

    /**
     * Handles GET request to display the form for creating a new order.
     */
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order/form";
    }

    /**
     * Handles POST request to create a new order.
     */
    @PostMapping
    public String createOrder(@ModelAttribute Order order) {
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    /**
     * Handles GET request to display the form for editing an existing order.
     */
    @GetMapping("/{id}/edit")
    public String editOrderForm(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "order/form";
    }

    /**
     * Handles POST request to update an existing order.
     */
    @PostMapping("/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order order) {
        order.setOrderId(id);
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    /**
     * Handles GET request to delete an order.
     */
    @GetMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}