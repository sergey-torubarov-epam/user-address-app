const request = require('supertest');
const { expect } = require('chai');
const sinon = require('sinon');
const app = require('../../app'); // Assuming the Express app is exported from app.js
const AddressService = require('../../services/addressService');

describe('AddressController', () => {
    let addressServiceStub;

    const mockAddress = {
        addressId: 1,
        buildingName: 'Building A',
        street: '123 Main St',
        city: 'New York',
        state: 'NY',
        pincode: '10001',
        users: [],
    };

    beforeEach(() => {
        addressServiceStub = sinon.stub(AddressService);
    });

    afterEach(() => {
        sinon.restore();
    });

    describe('GET /addresses', () => {
        // Test the GET /addresses route for retrieving a list of addresses
        it('should return a list of addresses and render the list view', async () => {
            addressServiceStub.getAllAddresses.resolves([mockAddress]);

            const res = await request(app).get('/addresses');

            expect(res.status).to.equal(200);
            expect(res.text).to.include('addresses'); // Assuming the view renders "addresses"
            sinon.assert.calledOnce(addressServiceStub.getAllAddresses);
        });
    });

    describe('GET /addresses/new', () => {
        // Test the GET /addresses/new route for rendering the address creation form
        it('should render the create form view', async () => {
            const res = await request(app).get('/addresses/new');

            expect(res.status).to.equal(200);
            expect(res.text).to.include('address'); // Assuming the view renders "address"
        });
    });

    describe('POST /addresses', () => {
        // Test the POST /addresses route for saving a valid address and redirecting
        it('should save a valid address and redirect', async () => {
            addressServiceStub.saveAddress.resolves(mockAddress);

            const res = await request(app)
                .post('/addresses')
                .send(mockAddress);

            expect(res.status).to.equal(302);
            expect(res.header.location).to.equal('/addresses');
            sinon.assert.calledOnce(addressServiceStub.saveAddress);
        });

        // Test the POST /addresses route to return form with errors for invalid data
        it('should return form with errors for invalid data', async () => {
            const invalidAddress = { ...mockAddress, buildingName: '' }; // Simulate invalid data

            const res = await request(app)
                .post('/addresses')
                .send(invalidAddress);

            expect(res.status).to.equal(400); // Assuming validation error returns 400
            expect(res.text).to.include('address/form'); // Assuming the view renders "address/form"
            sinon.assert.notCalled(addressServiceStub.saveAddress);
        });
    });

    describe('GET /addresses/:id/edit', () => {
        // Test the GET /addresses/:id/edit route for rendering the edit form for an existing address
        it('should render edit form for an existing address', async () => {
            addressServiceStub.getAddressById.resolves(mockAddress);

            const res = await request(app).get('/addresses/1/edit');

            expect(res.status).to.equal(200);
            expect(res.text).to.include('address'); // Assuming the view renders "address"
            sinon.assert.calledOnce(addressServiceStub.getAddressById);
        });

        // Test the GET /addresses/:id/edit route to redirect to the list view for a non-existing address
        it('should redirect to addresses list for a non-existing address', async () => {
            addressServiceStub.getAddressById.resolves(null);

            const res = await request(app).get('/addresses/99/edit');

            expect(res.status).to.equal(302);
            expect(res.header.location).to.equal('/addresses');
            sinon.assert.calledOnce(addressServiceStub.getAddressById);
        });
    });

    describe('POST /addresses/:id', () => {
        // Test the POST /addresses/:id route for updating a valid address and redirecting
        it('should update a valid address and redirect', async () => {
            addressServiceStub.saveAddress.resolves(mockAddress);

            const res = await request(app)
                .post('/addresses/1')
                .send(mockAddress);

            expect(res.status).to.equal(302);
            expect(res.header.location).to.equal('/addresses');
            sinon.assert.calledOnce(addressServiceStub.saveAddress);
        });

        // Test the POST /addresses/:id route to return form with errors for invalid data
        it('should return form with errors for invalid data', async () => {
            const invalidAddress = { ...mockAddress, city: '' }; // Simulate invalid data

            const res = await request(app)
                .post('/addresses/1')
                .send(invalidAddress);

            expect(res.status).to.equal(400); // Assuming validation error returns 400
            expect(res.text).to.include('address/form'); // Assuming the view renders "address/form"
            sinon.assert.notCalled(addressServiceStub.saveAddress);
        });
    });

    describe('GET /addresses/:id/delete', () => {
        // Test the GET /addresses/:id/delete route for deleting an address and redirecting
        it('should delete the address and redirect', async () => {
            addressServiceStub.deleteAddress.resolves();

            const res = await request(app).get('/addresses/1/delete');

            expect(res.status).to.equal(302);
            expect(res.header.location).to.equal('/addresses');
            sinon.assert.calledOnce(addressServiceStub.deleteAddress);
        });
    });
});
