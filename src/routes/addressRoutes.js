const express = require('express');
const router = express.Router();
const addressController = require('../controllers/addressController');

router.get('/addresses', addressController.listAddresses);
router.get('/addresses/new', addressController.showCreateForm);
router.post('/addresses', addressController.createAddress);
router.get('/addresses/:id/edit', addressController.showEditForm);
router.post('/addresses/:id', addressController.updateAddress);
router.get('/addresses/:id/delete', addressController.deleteAddress);

module.exports = router;