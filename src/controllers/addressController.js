const Address = require('../models/address');

exports.listAddresses = async (req, res) => {
    try {
        const addresses = await Address.findAll();
        res.render('address/list', { addresses });
    } catch (error) {
        res.status(500).send(error.message);
    }
};

exports.showCreateForm = (req, res) => {
    res.render('address/form', { address: {} });
};

exports.createAddress = async (req, res) => {
    try {
        const { buildingName, street, city, state, pincode } = req.body;
        const address = await Address.create({ buildingName, street, city, state, pincode });
        req.flash('successMessage', 'Address created successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send(error.message);
    }
};

exports.showEditForm = async (req, res) => {
    try {
        const address = await Address.findByPk(req.params.id);
        if (address) {
            res.render('address/form', { address });
        } else {
            res.redirect('/addresses');
        }
    } catch (error) {
        res.status(500).send(error.message);
    }
};

exports.updateAddress = async (req, res) => {
    try {
        const { buildingName, street, city, state, pincode } = req.body;
        await Address.update({ buildingName, street, city, state, pincode }, { where: { addressId: req.params.id } });
        req.flash('successMessage', 'Address updated successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send(error.message);
    }
};

exports.deleteAddress = async (req, res) => {
    try {
        await Address.destroy({ where: { addressId: req.params.id } });
        req.flash('successMessage', 'Address deleted successfully!');
        res.redirect('/addresses');
    } catch (error) {
        res.status(500).send(error.message);
    }
};