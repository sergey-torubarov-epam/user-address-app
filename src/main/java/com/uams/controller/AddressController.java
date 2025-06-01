// routes/addressRoutes.js

const express = require('express');
const router = express.Router();
const addressService = require('../services/addressService');

// List all addresses
router.get('/', async (req, res) => {
    try {
        // Fetching all addresses from the service and rendering the 'address/list' view with the fetched addresses
        const addresses = await addressService.getAllAddresses();
        res.render('address/list', { addresses });
    } catch (error) {
        res.status(500).send('Error fetching addresses');
    }
});

// Show form for creating a new address
router.get('/new', (req, res) => {
    // Rendering the form for creating a new address with an empty address object
    res.render('address/form', { address: {} });
});

// Create a new address
router.post('/', async (req, res) => {
    try {
        // Getting the address information from the request body
        const address = req.body;
        // Validating the address
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            // Rendering the form with validation errors
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        // Creating the address in the service
        await addressService.createAddress(address);
        req.flash('successMessage', 'Address created successfully!');
        // Redirecting to the list of addresses after successful creation
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error creating address');
    }
});

// Show form for editing an address
router.get('/:id/edit', async (req, res) => {
    try {
        // Fetching the address by ID from the service
        const address = await addressService.getAddressById(req.params.id);
        if (address) {
            // Rendering the form for editing the address with the fetched address data
            res.render('address/form', { address });
        } else {
            // Redirecting to the list of addresses if the address is not found
            res.redirect('/addresses');
        }
    } catch (error) {
        res.status(500).send('Error fetching the address');
    }
});

// Update an existing address
router.post('/:id', async (req, res) => {
    try {
        // Getting the address information from the request body and setting its ID
        const address = req.body;
        address.id = req.params.id;
        // Validating the address
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            // Rendering the form with validation errors
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        // Updating the address in the service
        await addressService.updateAddress(req.params.id, address);
        req.flash('successMessage', 'Address updated successfully!');
        // Redirecting to the list of addresses after successful update
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error updating address');
    }
});

// Delete an address
router.get('/:id/delete', async (req, res) => {
    try {
        // Deleting the address by ID from the service
        await addressService.deleteAddress(req.params.id);
        req.flash('successMessage', 'Address deleted successfully!');
        // Redirecting to the list of addresses after successful deletion
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error deleting address');
    }
});

module.exports = router;