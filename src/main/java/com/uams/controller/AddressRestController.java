package com.uams.controller;

import com.uams.model.Address;
import com.uams.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address REST API", description = "REST APIs for managing addresses with country support")
public class AddressRestController {

    private final AddressService addressService;

    @Autowired
    public AddressRestController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(
        summary = "Get all addresses",
        description = "Returns a list of all addresses including country information"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved addresses",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Address.class)
        )
    )
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    @Operation(
        summary = "Get address by ID",
        description = "Returns a specific address by its ID including country information"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved address",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Address.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Address not found"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(
        @Parameter(description = "ID of the address to retrieve") @PathVariable Long id) {
        Optional<Address> addressOpt = addressService.getAddressById(id);
        return addressOpt.map(address -> ResponseEntity.ok(address))
                        .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Create new address",
        description = "Creates a new address with the provided details including optional country"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Address created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Address.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data"
    )
    @PostMapping
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) {
        Address savedAddress = addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    @Operation(
        summary = "Update existing address",
        description = "Updates an existing address including country information"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Address updated successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Address.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Address not found"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
        @Parameter(description = "ID of the address to update") @PathVariable Long id,
        @Valid @RequestBody Address address) {
        
        Optional<Address> existingAddressOpt = addressService.getAddressById(id);
        if (!existingAddressOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        address.setAddressId(id);
        Address updatedAddress = addressService.saveAddress(address);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(
        summary = "Delete address",
        description = "Deletes an address from the system"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Address deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Address not found"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
        @Parameter(description = "ID of the address to delete") @PathVariable Long id) {
        
        Optional<Address> addressOpt = addressService.getAddressById(id);
        if (!addressOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}