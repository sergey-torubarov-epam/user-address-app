package com.uams.service;

import java.util.List;

public interface CountryService {
    List<String> getAllCountries();
    boolean isValidCountry(String country);
}