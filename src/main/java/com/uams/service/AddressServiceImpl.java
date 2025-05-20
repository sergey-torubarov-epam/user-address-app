const AddressRepository = require('../repository/addressRepository');

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
            throw new Error(`Error fetching address with ID ${id}: ${error.message}`);
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
```