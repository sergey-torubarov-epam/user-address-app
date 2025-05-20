// routes/addressRoutes.js

const express = require('express');
const router = express.Router();
const addressService = require('../services/addressService');

// List all addresses
router.get('/', async (req, res) => {
    try {
        const addresses = await addressService.getAllAddresses();
        res.render('address/list', { addresses });
    } catch (error) {
        res.status(500).send('Error fetching addresses');
    }
});

// Show form for creating a new address
router.get('/new', (req, res) => {
    res.render('address/form', { address: {} });
});

// Create a new address
router.post('/', async (req, res) => {
    try {
        const address = req.body;
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        await addressService.createAddress(address);
        req.flash('successMessage', 'Address created successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error creating address');
    }
});

// Show form for editing an address
router.get('/:id/edit', async (req, res) => {
    try {
        const address = await addressService.getAddressById(req.params.id);
        if (address) {
            res.render('address/form', { address });
        } else {
            res.redirect('/addresses');
        }
    } catch (error) {
        res.status(500).send('Error fetching the address');
    }
});

// Update an existing address
router.post('/:id', async (req, res) => {
    try {
        const address = req.body;
        address.id = req.params.id;
        const validationErrors = addressService.validateAddress(address);
        if (validationErrors.length > 0) {
            return res.status(400).render('address/form', { address, errors: validationErrors });
        }
        await addressService.updateAddress(req.params.id, address);
        req.flash('successMessage', 'Address updated successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error updating address');
    }
});

// Delete an address
router.get('/:id/delete', async (req, res) => {
    try {
        await addressService.deleteAddress(req.params.id);
        req.flash('successMessage', 'Address deleted successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send('Error deleting address');
    }
});

module.exports = router;
```

```javascript
// services/addressService.js

const Address = require('../models/Address'); // Assuming you have an Address model

// Fetch all addresses
async function getAllAddresses() {
    try {
        return await Address.find();
    } catch (error) {
        throw new Error('Error fetching addresses');
    }
}

// Fetch an address by ID
async function getAddressById(id) {
    try {
        return await Address.findById(id);
    } catch (error) {
        throw new Error('Error fetching address');
    }
}

// Validate address input
function validateAddress(address) {
    const errors = [];
    if (!address.street || address.street.trim() === '') {
        errors.push('Street is required');
    }
    if (!address.city || address.city.trim() === '') {
        errors.push('City is required');
    }
    if (!address.state || address.state.trim() === '') {
        errors.push('State is required');
    }
    if (!address.zip || address.zip.trim() === '') {
        errors.push('ZIP code is required');
    }
    return errors;
}

// Create a new address
async function createAddress(address) {
    try {
        const newAddress = new Address(address);
        await newAddress.save();
    } catch (error) {
        throw new Error('Error creating address');
    }
}

// Update an existing address
async function updateAddress(id, address) {
    try {
        await Address.findByIdAndUpdate(id, address);
    } catch (error) {
        throw new Error('Error updating address');
    }
}

// Delete an address
async function deleteAddress(id) {
    try {
        await Address.findByIdAndDelete(id);
    } catch (error) {
        throw new Error('Error deleting address');
    }
}

module.exports = {
    getAllAddresses,
    getAddressById,
    validateAddress,
    createAddress,
    updateAddress,
    deleteAddress,
};
```

```javascript
// models/Address.js

const mongoose = require('mongoose');

const addressSchema = new mongoose.Schema({
    street: { type: String, required: true },
    city: { type: String, required: true },
    state: { type: String, required: true },
    zip: { type: String, required: true },
});

module.exports = mongoose.model('Address', addressSchema);
```