package com.uams.controller;

import com.uams.model.Address;
import com.uams.model.User;
import com.uams.service.AddressService;
import com.uams.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users and their addresses")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @Operation(
        summary = "List all users",
        description = "Returns a page displaying all users in the system"
    )
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/form";
    }

    @Operation(
        summary = "Create new user",
        description = "Creates a new user with the provided details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "User created successfully"
    )
    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        
        if (result.hasErrors()) {
            return "user/form";
        }
        
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/form";
        }
        return "redirect:/users";
    }

    @Operation(
        summary = "Update existing user",
        description = "Updates an existing user's information"
    )
    @PostMapping("/{id}")
    public String updateUser(
        @Parameter(description = "ID of the user to update") @PathVariable Long id,
        @Valid @ModelAttribute("user") User user,
        BindingResult result,
        RedirectAttributes redirectAttributes) {
        Optional<User> existingUserOpt = userService.getUserById(id);
        if (!existingUserOpt.isPresent()) {
            return "redirect:/users";
        }
        
        User existingUser = existingUserOpt.get();
        
        // Check if email is changed and already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        
        if (result.hasErrors()) {
            return "user/form";
        }
        
        // Update user fields
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMobileNumber(user.getMobileNumber());
        
        // Only update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        userService.saveUser(existingUser);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        return "redirect:/users";
    }

    @Operation(
        summary = "Delete user",
        description = "Deletes a user from the system"
    )
    @GetMapping("/{id}/delete")
    public String deleteUser(
        @Parameter(description = "ID of the user to delete") @PathVariable Long id,
        RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/users";
    }
    
    @Operation(
        summary = "View user addresses",
        description = "Displays all addresses associated with a user"
    )
    @GetMapping("/{id}/addresses")
    public String viewUserAddresses(
        @Parameter(description = "ID of the user") @PathVariable Long id,
        Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("addresses", user.getAddresses());
            model.addAttribute("allAddresses", addressService.getAllAddresses());
            model.addAttribute("newAddress", new Address());
            return "user/addresses";
        }
        return "redirect:/users";
    }
    
    @PostMapping("/{userId}/addresses/add")
    public String addAddressToUser(@PathVariable Long userId, 
                                  @RequestParam Long addressId,
                                  RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.getUserById(userId);
        Optional<Address> addressOpt = addressService.getAddressById(addressId);
        
        if (userOpt.isPresent() && addressOpt.isPresent()) {
            User user = userOpt.get();
            Address address = addressOpt.get();
            
            user.addAddress(address);
            userService.saveUser(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Address added to user successfully!");
        }
        
        return "redirect:/users/" + userId + "/addresses";
    }
    
    @GetMapping("/{userId}/addresses/{addressId}/remove")
    public String removeAddressFromUser(@PathVariable Long userId, 
                                       @PathVariable Long addressId,
                                       RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.getUserById(userId);
        Optional<Address> addressOpt = addressService.getAddressById(addressId);
        
        if (userOpt.isPresent() && addressOpt.isPresent()) {
            User user = userOpt.get();
            Address address = addressOpt.get();
            
            user.removeAddress(address);
            userService.saveUser(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Address removed from user successfully!");
        }
        
        return "redirect:/users/" + userId + "/addresses";
    }
}