// services/addressService.js

/**
 * AddressService class provides methods to interact with the Address repository.
 * It includes methods to fetch all addresses, fetch an address by its ID,
 * save a new address, and delete an address by its ID.
 */
class AddressService {
    /**
     * Initialize the AddressService with a reference to the AddressRepository.
     * @param {Object} addressRepository - The repository instance for managing address data.
     */
    constructor(addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Retrieves all addresses from the repository.
     * @returns {Promise<Array>} A promise that resolves to an array of address objects.
     * @throws Will throw an error if there's an issue fetching the addresses.
     */
    async getAllAddresses() {
        try {
            return await this.addressRepository.findAll();
        } catch (error) {
            throw new Error(`Error fetching all addresses: ${error.message}`);
        }
    }

    /**
     * Retrieves a specific address by its ID from the repository.
     * @param {number|string} id - The ID of the address to fetch.
     * @returns {Promise<Object>} A promise that resolves to the address object if found.
     * @throws Will throw an error if there's an issue fetching the address or if the address is not found.
     */
    async getAddressById(id) {
        try {
            const address = await this.addressRepository.findById(id);
            if (!address) {
                throw new Error(`Address with ID ${id} not found`);
            }
            return address;
        } catch (error) {
            throw new Error(`Error fetching address by ID ${id}: ${error.message}`);
        }
    }

    /**
     * Saves a new address to the repository.
     * @param {Object} address - The address object to save.
     * @returns {Promise<Object>} A promise that resolves to the saved address object.
     * @throws Will throw an error if there's an issue saving the address.
     */
    async saveAddress(address) {
        try {
            return await this.addressRepository.save(address);
        } catch (error) {
            throw new Error(`Error saving address: ${error.message}`);
        }
    }

    /**
     * Deletes an address from the repository by its ID.
     * @param {number|string} id - The ID of the address to delete.
     * @returns {Promise<void>} A promise that resolves when the deletion is complete.
     * @throws Will throw an error if there's an issue deleting the address or if the address is not found.
     */
    async deleteAddress(id) {
        try {
            const address = await this.addressRepository.findById(id);
            if (!address) {
                throw new Error(`Address with ID ${id} not found`);
            }
            await this.addressRepository.delete(id);
        } catch (error) {
            throw new Error(`Error deleting address with ID ${id}: ${error.message}`);
        }
    }
}

export default AddressService;