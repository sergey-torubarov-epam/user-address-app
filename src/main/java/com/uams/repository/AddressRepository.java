// repositories/addressRepository.js

const { Address } = require('../models'); // Assuming Sequelize is used and Address model is defined with Sequelize

/** 
 * AddressRepository class handles CRUD operations for the Address model.
 */
class AddressRepository {
  /**
   * Find all addresses in the database.
   * @returns {Promise<Array>} A promise that resolves to an array of all addresses.
   */
  async findAll() {
    try {
      return await Address.findAll();
    } catch (error) {
      throw new Error('Error fetching addresses: ' + error.message);
    }
  }

  /**
   * Find an address by its ID.
   * @param {number} id - The ID of the address to retrieve.
   * @returns {Promise<Object>} A promise that resolves to the address object if found.
   */
  async findById(id) {
    try {
      return await Address.findByPk(id);
    } catch (error) {
      throw new Error('Error fetching address by ID: ' + error.message);
    }
  }

  /**
   * Create a new address in the database.
   * @param {Object} addressData - The address data to create.
   * @returns {Promise<Object>} A promise that resolves to the created address object.
   */
  async create(addressData) {
    try {
      return await Address.create(addressData);
    } catch (error) {
      throw new Error('Error creating address: ' + error.message);
    }
  }

  /**
   * Update an existing address by its ID.
   * @param {number} id - The ID of the address to update.
   * @param {Object} addressData - The new data to update the address with.
   * @returns {Promise<Object>} A promise that resolves to the updated address object.
   */
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

  /**
   * Delete an existing address by its ID.
   * @param {number} id - The ID of the address to delete.
   * @returns {Promise<Object>} A promise that resolves to a success message upon deletion.
   */
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