// repositories/addressRepository.js

const { Address } = require('../models'); // Assuming Sequelize is used and Address model is defined with Sequelize

class AddressRepository {
  // Find all addresses
  async findAll() {
    try {
      return await Address.findAll();
    } catch (error) {
      throw new Error('Error fetching addresses: ' + error.message);
    }
  }

  // Find address by ID
  async findById(id) {
    try {
      return await Address.findByPk(id);
    } catch (error) {
      throw new Error('Error fetching address by ID: ' + error.message);
    }
  }

  // Create a new address
  async create(addressData) {
    try {
      return await Address.create(addressData);
    } catch (error) {
      throw new Error('Error creating address: ' + error.message);
    }
  }

  // Update an address by ID
  async update(id, addressData) {
    try {
      const address = await Address.findByPk(id);
      if (!address) {
        throw new Error('Address not found');
      }
      return await address.update(addressData);
    } catch (error) {
      throw new Error('Error updating address: ' + error.message);
    }
  }

  // Delete an address by ID
  async delete(id) {
    try {
      const address = await Address.findByPk(id);
      if (!address) {
        throw new Error('Address not found');
      }
      await address.destroy();
      return { message: 'Address deleted successfully' };
    } catch (error) {
      throw new Error('Error deleting address: ' + error.message);
    }
  }
}

module.exports = new AddressRepository();