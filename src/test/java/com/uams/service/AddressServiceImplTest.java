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
      // Arrange: Mock the address repository to return predefined addresses.
      addressRepositoryStub.findAll.resolves([address1, address2]);

      // Act: Call the service to retrieve all addresses.
      const addresses = await addressService.getAllAddresses();

      // Assert: Verify the service method returns the expected addresses and the repository method was called once.
      expect(addresses).to.deep.equal([address1, address2]);
      sinon.assert.calledOnce(addressRepositoryStub.findAll);
    });
  });

  describe('getAddressById', () => {
    it('should return an address when given a valid ID', async () => {
      // Arrange: Mock the address repository to return address1 for id 1.
      addressRepositoryStub.findById.resolves(address1);

      // Act: Call the service to retrieve the address with id 1.
      const result = await addressService.getAddressById(1);

      // Assert: Verify the service method returns the expected address and the repository method was called once with id 1.
      expect(result).to.deep.equal(address1);
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.findById, 1);
    });

    it('should return null when given a non-existing ID', async () => {
      // Arrange: Mock the address repository to return null for id 3.
      addressRepositoryStub.findById.resolves(null);

      // Act: Call the service to retrieve the address with id 3.
      const result = await addressService.getAddressById(3);

      // Assert: Verify the service method returns null and the repository method was called once with id 3.
      expect(result).to.be.null;
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.findById, 3);
    });
  });

  describe('saveAddress', () => {
    it('should save and return the created address', async () => {
      // Arrange: Mock the address repository to save and return address1.
      addressRepositoryStub.save.resolves(address1);

      // Act: Call the service to save address1.
      const savedAddress = await addressService.saveAddress(address1);

      // Assert: Verify the service method returns the saved address and the repository method was called once with address1.
      expect(savedAddress).to.deep.equal(address1);
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.save, address1);
    });
  });

  describe('deleteAddress', () => {
    it('should delete the address by ID', async () => {
      // Arrange: Mock the address repository to simulate deleting an address by id 1.
      addressRepositoryStub.deleteById.resolves();

      // Act: Call the service to delete the address with id 1.
      await addressService.deleteAddress(1);

      // Assert: Verify the repository method was called once with id 1.
      sinon.assert.calledOnceWithExactly(addressRepositoryStub.deleteById, 1);
    });
  });
});