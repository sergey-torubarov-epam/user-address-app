// routes/addressRoutes.js

const express = require('express');
const router = express.Router();
const addressService = require('../services/addressService');

/**
 * GET /addresses
 * List all addresses
 */
router.get('/', async (req, res) => {
    try {
        // Fetch all addresses from the service
        const addresses = await addressService.getAllAddresses();
        // Render the 'address/list' view with the fetched addresses
        res.render('address/list', { addresses });
    } catch (error) {
        // Handle errors by sending a 500 status code
        res.status(500).send('Error fetching addresses');
    }
});

/**
 * GET /addresses/new
 * Show form for creating a new address
 */
router.get('/new', (req, res) => {
    // Render the form for creating a new address with an empty address object
    res.render('address/form', { address: {} });
});

/**
 * POST /addresses
 * Create a new address
 */
router.post('/', async (req, res) => {
    try {
        // Extract the address information from the request body
        const address = req.body;
        // Validate the address
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            // If there are validation errors, render the form again with error messages
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        // Create the address in the service
        await addressService.createAddress(address);
        // Set a flash message for successful creation
        req.flash('successMessage', 'Address created successfully!');
        // Redirect to the list of addresses after successful creation
        res.redirect('/addresses');
    } catch (error) {
        // Handle errors by sending a 500 status code
        res.status(500).send('Error creating address');
    }
});

/**
 * GET /addresses/:id/edit
 * Show form for editing an existing address
 */
router.get('/:id/edit', async (req, res) => {
    try {
        // Fetch the address by ID from the service
        const address = await addressService.getAddressById(req.params.id);
        if (address) {
            // If the address is found, render the form for editing
            res.render('address/form', { address });
        } else {
            // If the address is not found, redirect to the list of addresses
            res.redirect('/addresses');
        }
    } catch (error) {
        // Handle errors by sending a 500 status code
        res.status(500).send('Error fetching the address');
    }
});

/**
 * POST /addresses/:id
 * Update an existing address
 */
router.post('/:id', async (req, res) => {
    try {
        // Extract the address information from the request body
        const address = req.body;
        // Set the ID of the address to be updated
        address.id = req.params.id;
        // Validate the address
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            // If there are validation errors, render the form again with error messages
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        // Update the address in the service
        await addressService.updateAddress(req.params.id, address);
        // Set a flash message for successful update
        req.flash('successMessage', 'Address updated successfully!');
        // Redirect to the list of addresses after successful update
        res.redirect('/addresses');
    } catch (error) {
        // Handle errors by sending a 500 status code
        res.status(500).send('Error updating address');
    }
});

/**
 * GET /addresses/:id/delete
 * Delete an existing address
 */
router.get('/:id/delete', async (req, res) => {
    try {
        // Delete the address by ID from the service
        await addressService.deleteAddress(req.params.id);
        // Set a flash message for successful deletion
        req.flash('successMessage', 'Address deleted successfully!');
        // Redirect to the list of addresses after successful deletion
        res.redirect('/addresses');
    } catch (error) {
        // Handle errors by sending a 500 status code
        res.status(500).send('Error deleting address');
    }
});

module.exports = router;