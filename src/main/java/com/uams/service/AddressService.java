package com.uams.service;

import com.uams.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<Address> getAllAddresses();
    Optional<Address> getAddressById(Long id);
    Address saveAddress(Address address);
    void deleteAddress(Long id);
}