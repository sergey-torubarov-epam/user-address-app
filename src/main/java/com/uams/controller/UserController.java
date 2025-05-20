const express = require('express');
const router = express.Router();
const userService = require('../services/userService');
const addressService = require('../services/addressService');

// List all users
router.get('/', async (req, res, next) => {
  try {
    const users = await userService.getAllUsers();
    res.render('user/list', { users });
  } catch (error) {
    next(error);
  }
});

// Show form to create a new user
router.get('/new', (req, res) => {
  res.render('user/form', { user: {} });
});

// Create new user
router.post('/', async (req, res, next) => {
  const userData = req.body;
  try {
    const existingUser = await userService.getUserByEmail(userData.email);
    if (existingUser) {
      return res.render('user/form', {
        user: userData,
        errors: { email: 'Email already exists' },
      });
    }

    await userService.createUser(userData);
    req.flash('successMessage', 'User created successfully!');
    res.redirect('/users');
  } catch (error) {
    next(error);
  }
});

// Show form to edit user
router.get('/:id/edit', async (req, res, next) => {
  const userId = req.params.id;
  try {
    const user = await userService.getUserById(userId);
    if (user) {
      res.render('user/form', { user });
    } else {
      res.redirect('/users');
    }
  } catch (error) {
    next(error);
  }
});

// Update user
router.post('/:id', async (req, res, next) => {
  const userId = req.params.id;
  const userData = req.body;

  try {
    const existingUser = await userService.getUserById(userId);
    if (!existingUser) {
      return res.redirect('/users');
    }

    if (
      existingUser.email !== userData.email &&
      (await userService.getUserByEmail(userData.email))
    ) {
      return res.render('user/form', {
        user: userData,
        errors: { email: 'Email already exists' },
      });
    }

    await userService.updateUser(userId, userData);
    req.flash('successMessage', 'User updated successfully!');
    res.redirect('/users');
  } catch (error) {
    next(error);
  }
});

// Delete user
router.get('/:id/delete', async (req, res, next) => {
  const userId = req.params.id;

  try {
    await userService.deleteUser(userId);
    req.flash('successMessage', 'User deleted successfully!');
    res.redirect('/users');
  } catch (error) {
    next(error);
  }
});

// View user addresses
router.get('/:id/addresses', async (req, res, next) => {
  const userId = req.params.id;

  try {
    const user = await userService.getUserById(userId);
    if (user) {
      const allAddresses = await addressService.getAllAddresses();
      res.render('user/addresses', {
        user,
        addresses: user.addresses,
        allAddresses,
        newAddress: {},
      });
    } else {
      res.redirect('/users');
    }
  } catch (error) {
    next(error);
  }
});

// Add address to user
router.post('/:userId/addresses/add', async (req, res, next) => {
  const userId = req.params.userId;
  const { addressId } = req.body;

  try {
    const user = await userService.getUserById(userId);
    const address = await addressService.getAddressById(addressId);

    if (user && address) {
      await userService.addAddressToUser(userId, addressId);
      req.flash('successMessage', 'Address added to user successfully!');
    }

    res.redirect(`/users/${userId}/addresses`);
  } catch (error) {
    next(error);
  }
});

// Remove address from user
router.get('/:userId/addresses/:addressId/remove', async (req, res, next) => {
  const userId = req.params.userId;
  const addressId = req.params.addressId;

  try {
    const user = await userService.getUserById(userId);
    const address = await addressService.getAddressById(addressId);

    if (user && address) {
      await userService.removeAddressFromUser(userId, addressId);
      req.flash('successMessage', 'Address removed from user successfully!');
    }

    res.redirect(`/users/${userId}/addresses`);
  } catch (error) {
    next(error);
  }
});

module.exports = router;