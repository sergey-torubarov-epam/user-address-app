package com.uams.controller;

import com.uams.model.Address;
import com.uams.service.AddressService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/addresses")
@Tag(name = "Address Management", description = "APIs for managing addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(
        summary = "List all addresses",
        description = "Returns a page displaying all addresses in the system"
    )
    @GetMapping
    public String listAddresses(Model model) {
        List<Address> addresses = addressService.getAllAddresses();
        model.addAttribute("addresses", addresses);
        return "address/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("address", new Address());
        return "address/form";
    }

    @Operation(
        summary = "Create new address",
        description = "Creates a new address with the provided details"
    )
    @PostMapping
    public String createAddress(@Valid @ModelAttribute("address") Address address, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "address/form";
        }
        
        addressService.saveAddress(address);
        redirectAttributes.addFlashAttribute("successMessage", "Address created successfully!");
        return "redirect:/addresses";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Address> addressOpt = addressService.getAddressById(id);
        if (addressOpt.isPresent()) {
            model.addAttribute("address", addressOpt.get());
            return "address/form";
        }
        return "redirect:/addresses";
    }

    @Operation(
        summary = "Update address",
        description = "Updates an existing address's information"
    )
    @PostMapping("/{id}")
    public String updateAddress(@Parameter(description = "ID of the address to update") @PathVariable Long id, 
                              @Valid @ModelAttribute("address") Address address, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "address/form";
        }
        
        address.setAddressId(id);
        addressService.saveAddress(address);
        redirectAttributes.addFlashAttribute("successMessage", "Address updated successfully!");
        return "redirect:/addresses";
    }

    @Operation(
        summary = "Delete address",
        description = "Deletes an address from the system"
    )
    @GetMapping("/{id}/delete")
    public String deleteAddress(@Parameter(description = "ID of the address to delete") @PathVariable Long id, RedirectAttributes redirectAttributes) {
        addressService.deleteAddress(id);
        redirectAttributes.addFlashAttribute("successMessage", "Address deleted successfully!");
        return "redirect:/addresses";
    }
}