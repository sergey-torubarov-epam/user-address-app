const { expect } = require('chai');
const sinon = require('sinon');
const AddressService = require('../../services/addressService');
const AddressRepository = require('../../repositories/addressRepository');

describe('AddressService', () => {
  let addressService;
  let addressRepositoryStub;

  const address1 = {
    addressId: 1,
    buildingName: 'Building A',
    street: '123 Main St',
    city: 'New York',
    state: 'NY',
    pincode: '10001',
  };

  const address2 = {
    addressId: 2,
    buildingName: 'Building B',
    street: '456 Oak Ave',
    city: 'Los Angeles',
    state: 'CA',
    pincode: '90001',
  };

  beforeEach(() => {
    addressRepositoryStub = sinon.createStubInstance(AddressRepository);
    addressService = new AddressService(addressRepositoryStub);
  });

  describe('getAllAddresses', () => {
    it('should return all addresses', async () => {
      // Arrange
      addressRepositoryStub.findAll.resolves([address1, address2]);

      // Act
      const addresses = await addressService.getAllAddresses();

      // Assert
      expect(addresses).to.deep.equal([address1, address2]);
      sinon.assert.calledOnce(addressRepositoryStub.findAll);
    });
  });

  describe('getAddressById', () => {
    it('should return an address when given a valid ID', async () => {
      // Arrange
      addressRepositoryStub.findById.resolves(address1);

      // Act
      const result = await addressService.getAddressById(1);

      // Assert
      expect(result).to.deep.equal(address1);
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.findById, 1);
    });

    it('should return null when given a non-existing ID', async () => {
      // Arrange
      addressRepositoryStub.findById.resolves(null);

      // Act
      const result = await addressService.getAddressById(3);

      // Assert
      expect(result).to.be.null;
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.findById, 3);
    });
  });

  describe('saveAddress', () => {
    it('should save and return the created address', async () => {
      // Arrange
      addressRepositoryStub.save.resolves(address1);

      // Act
      const savedAddress = await addressService.saveAddress(address1);

      // Assert
      expect(savedAddress).to.deep.equal(address1);
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.save, address1);
    });
  });

  describe('deleteAddress', () => {
    it('should delete the address by ID', async () => {
      // Arrange
      addressRepositoryStub.deleteById.resolves();

      // Act
      await addressService.deleteAddress(1);

      // Assert
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.deleteById, 1);
    });
  });
});
```