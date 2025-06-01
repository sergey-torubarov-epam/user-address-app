
const AddressRepository = require('../repository/addressRepository');

/**
 * AddressService is responsible for managing address related operations,
 * including fetching, saving, and deleting addresses.
 */
class AddressService {
    /**
     * Creates an instance of AddressService.
     * 
     * @param {AddressRepository} addressRepository - The repository used to interact with the data source for address data.
     */
    constructor(addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Retrieves all addresses from the data source.
     *
     * @returns {Promise<Array>} - A promise that resolves to an array of addresses.
     * @throws {Error} - Throws an error if the addresses could not be fetched.
     */
    async getAllAddresses() {
        try {
            return await this.addressRepository.findAll();
        } catch (error) {
            throw new Error(`Error fetching all addresses: ${error.message}`);
        }
    }

    /**
     * Retrieves an address by its ID from the data source.
     *
     * @param {number} id - The ID of the address to retrieve.
     * @returns {Promise<Object>} - A promise that resolves to the address object.
     * @throws {Error} - Throws an error if the address cannot be found or if there is an issue fetching the address.
     */
    async getAddressById(id) {
        try {
            const address = await this.addressRepository.findById(id);
            if (!address) {
                throw new Error(`Address with ID ${id} not found`);
            }
            return address;
        } catch (error) {
            throw new Error(`Error fetching address with ID ${id}: ${error.message}`);
        }
    }

    /**
     * Saves a new address to the data source.
     *
     * @param {Object} address - The address object to be saved.
     * @returns {Promise<Object>} - A promise that resolves to the saved address object.
     * @throws {Error} - Throws an error if the address could not be saved.
     */
    async saveAddress(address) {
        try {
            return await this.addressRepository.save(address);
        } catch (error) {
            throw new Error(`Error saving address: ${error.message}`);
        }
    }

    /**
     * Deletes an address by its ID from the data source.
     *
     * @param {number} id - The ID of the address to delete.
     * @returns {Promise<void>} - A promise that resolves when the address has been deleted.
     * @throws {Error} - Throws an error if the address cannot be found or if there is an issue deleting the address.
     */
    async deleteAddress(id) {
        try {
            const addressExists = await this.addressRepository.findById(id);
            if (!addressExists) {
                throw new Error(`Address with ID ${id} not found`);
            }
            await this.addressRepository.deleteById(id);
        } catch (error) {
            throw new Error(`Error deleting address with ID ${id}: ${error.message}`);
        }
    }
}

module.exports = new AddressService(new AddressRepository());