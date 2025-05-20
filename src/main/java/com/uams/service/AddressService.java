// services/addressService.js

class AddressService {
    constructor(addressRepository) {
        this.addressRepository = addressRepository;
    }

    async getAllAddresses() {
        try {
            return await this.addressRepository.findAll();
        } catch (error) {
            throw new Error(`Error fetching all addresses: ${error.message}`);
        }
    }

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

    async saveAddress(address) {
        try {
            return await this.addressRepository.save(address);
        } catch (error) {
            throw new Error(`Error saving address: ${error.message}`);
        }
    }

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