package com.uams.controller;

import com.uams.model.Order;
import com.uams.model.User;
import com.uams.service.OrderService;
import com.uams.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification; // For potential filtering later
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final UserService userService; // To fetch users for dropdown or association

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // List all orders (EPMCDMETST-3355)
    @GetMapping
    public String listOrders(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "orderDate") String sort,
                             @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            // TODO: Implement filtering using Specification based on request parameters if needed
            Specification<Order> spec = null; // Placeholder for potential filtering
            Page<Order> orderPage = orderService.findAll(spec, pageable);

            model.addAttribute("orderPage", orderPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortField", sort);
            model.addAttribute("sortDir", direction);
            model.addAttribute("reverseSortDir", direction.equals("ASC") ? "DESC" : "ASC");

        } catch (Exception e) {
            logger.error("Error listing orders", e);
            model.addAttribute("errorMessage", "Error retrieving orders.");
            // Optionally return a specific error view or add dummy data for the view
             model.addAttribute("orderPage", Page.empty()); // Ensure the view has an empty page object
        }
        return "order/list";
    }

    // Show form for creating a new order (EPMCDMETST-3356)
    @GetMapping("/new")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new Order());
        // Provide list of users to select from in the form
        List<User> users = userService.findAll(); // Assuming userService has findAll()
        model.addAttribute("users", users);
        model.addAttribute("formTitle", "Create New Order");
        return "order/form";
    }

    // Process creation of a new order (EPMCDMETST-3356)
    @PostMapping("/save")
    public String saveOrder(@Valid @ModelAttribute("order") Order order,
                            BindingResult bindingResult,
                            @RequestParam("userId") Long userId, // Get user ID from form
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors creating order: {}", bindingResult.getAllErrors());
             List<User> users = userService.findAll();
             model.addAttribute("users", users);
             model.addAttribute("formTitle", order.getId() == null ? "Create New Order" : "Edit Order");
            return "order/form"; // Return to form with errors
        }
        try {
            String successMessage;
            if (order.getId() == null) {
                 orderService.save(order, userId);
                 successMessage = "Order created successfully!";
                 logger.info("Order created successfully for user ID: {}", userId);
            } else {
                // Note: User association typically shouldn't change on update via this method.
                // If user change is needed, handle it explicitly. The save method associates user on creation.
                // We might need a separate update method in service that doesn't require userId,
                // or fetch the existing order first to preserve the user if not provided.
                // For simplicity here, we assume user doesn't change or the form doesn't allow it.
                // Let's use the update method which takes orderId and details.
                orderService.update(order.getId(), order);
                successMessage = "Order updated successfully!";
                logger.info("Order updated successfully with ID: {}", order.getId());
            }

            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/orders"; // Redirect to the order list
        } catch (EntityNotFoundException e) {
             logger.error("Error saving order: User not found for ID {}", userId, e);
             bindingResult.reject("user", "User not found for the selected ID.");
             List<User> users = userService.findAll();
             model.addAttribute("users", users);
             model.addAttribute("formTitle", order.getId() == null ? "Create New Order" : "Edit Order");
             return "order/form";
        } catch (Exception e) {
            logger.error("Error saving order for user ID: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving order.");
            // Optionally add specific error handling based on exception type
             List<User> users = userService.findAll();
             model.addAttribute("users", users);
             model.addAttribute("formTitle", order.getId() == null ? "Create New Order" : "Edit Order");
             model.addAttribute("errorMessage", "An unexpected error occurred while saving the order.");
            return "order/form"; // Stay on form, show general error
        }
    }

    // Show form for editing an existing order (EPMCDMETST-3357)
    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                model.addAttribute("order", order);
                List<User> users = userService.findAll(); // Provide users for context, though changing user might be restricted
                model.addAttribute("users", users);
                 model.addAttribute("formTitle", "Edit Order #" + id);
                return "order/form";
            } else {
                 logger.warn("Attempted to edit non-existent order with ID: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found with ID: " + id);
                return "redirect:/orders";
            }
        } catch (Exception e) {
             logger.error("Error retrieving order for edit with ID: {}", id, e);
             redirectAttributes.addFlashAttribute("errorMessage", "Error retrieving order for editing.");
             return "redirect:/orders";
        }
    }

    // Delete an order (EPMCDMETST-3358)
    // Using POST for deletion as best practice, could also use DELETE http method with JS confirmation
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully!");
            logger.info("Order deleted successfully with ID: {}", id);
        } catch (EntityNotFoundException e) {
             logger.warn("Attempted to delete non-existent order with ID: {}", id);
             redirectAttributes.addFlashAttribute("errorMessage", "Order not found, could not delete.");
        }
        catch (Exception e) {
            logger.error("Error deleting order with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting order.");
            // Consider data integrity constraints (e.g., related entities)
            // redirectAttributes.addFlashAttribute("errorMessage", "Error deleting order. It might be associated with other records.");
        }
        return "redirect:/orders";
    }

     // View details of a single order (Optional - Good practice)
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
                return "order/details"; // Need to create order/details.html view
            } else {
                logger.warn("Attempted to view non-existent order with ID: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found with ID: " + id);
                return "redirect:/orders";
            }
        } catch (Exception e) {
            logger.error("Error retrieving order details for ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error retrieving order details.");
            return "redirect:/orders";
        }
    }

}
