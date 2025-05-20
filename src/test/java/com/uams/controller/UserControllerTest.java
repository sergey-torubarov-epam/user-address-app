const request = require('supertest');
const chai = require('chai');
const sinon = require('sinon');
const expect = chai.expect;
const app = require('../../app'); // Assuming app.js initializes your Express app
const userService = require('../../services/userService');
const addressService = require('../../services/addressService');

describe('UserController', () => {
    let sandbox;
    let user;
    let address;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        user = {
            userId: 1,
            email: 'john@example.com',
            firstName: 'John',
            lastName: 'Doe',
            mobileNumber: '1234567890',
            password: 'password',
            addresses: new Set(),
        };

        address = {
            addressId: 1,
            buildingName: 'Building A',
            street: '123 Main St',
            city: 'New York',
            state: 'NY',
            pincode: '10001',
            users: new Set(),
        };
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('GET /users - should return user list view', async () => {
        sandbox.stub(userService, 'getAllUsers').returns([user]);

        const res = await request(app).get('/users');

        expect(res.status).to.equal(200);
        expect(res.text).to.include('user/list'); // Assuming the view renders this
        sandbox.assert.calledOnce(userService.getAllUsers);
    });

    it('GET /users/new - should return user creation form', async () => {
        const res = await request(app).get('/users/new');

        expect(res.status).to.equal(200);
        expect(res.text).to.include('user/form'); // Assuming the view renders this
    });

    it('POST /users - should create a new user and redirect', async () => {
        sandbox.stub(userService, 'existsByEmail').returns(false);
        sandbox.stub(userService, 'saveUser').returns(user);

        const res = await request(app)
            .post('/users')
            .send(user);

        expect(res.status).to.equal(302);
        expect(res.header.location).to.equal('/users');
        sandbox.assert.calledOnce(userService.existsByEmail);
        sandbox.assert.calledOnce(userService.saveUser);
    });

    it('POST /users - should reject creation for existing email', async () => {
        sandbox.stub(userService, 'existsByEmail').returns(true);

        const res = await request(app)
            .post('/users')
            .send(user);

        expect(res.status).to.equal(200); // Assuming the form view is returned with validation errors
        expect(res.text).to.include('user/form'); // Assuming the view renders this
        sandbox.assert.calledOnce(userService.existsByEmail);
        sandbox.assert.notCalled(userService.saveUser);
    });

    it('GET /users/:id/edit - should return edit form for existing user', async () => {
        sandbox.stub(userService, 'getUserById').returns(Promise.resolve(user));

        const res = await request(app).get('/users/1/edit');

        expect(res.status).to.equal(200);
        expect(res.text).to.include('user/form'); // Assuming the view renders this
        sandbox.assert.calledOnce(userService.getUserById);
    });

    it('GET /users/:id/edit - should redirect if user does not exist', async () => {
        sandbox.stub(userService, 'getUserById').returns(Promise.resolve(null));

        const res = await request(app).get('/users/99/edit');

        expect(res.status).to.equal(302);
        expect(res.header.location).to.equal('/users');
        sandbox.assert.calledOnce(userService.getUserById);
    });

    it('GET /users/:id/delete - should delete user and redirect', async () => {
        sandbox.stub(userService, 'deleteUser').returns(Promise.resolve());

        const res = await request(app).get('/users/1/delete');

        expect(res.status).to.equal(302);
        expect(res.header.location).to.equal('/users');
        sandbox.assert.calledOnce(userService.deleteUser);
    });

    it('GET /users/:id/addresses - should return user addresses view', async () => {
        sandbox.stub(userService, 'getUserById').returns(Promise.resolve(user));
        sandbox.stub(addressService, 'getAllAddresses').returns([address]);

        const res = await request(app).get('/users/1/addresses');

        expect(res.status).to.equal(200);
        expect(res.text).to.include('user/addresses'); // Assuming the view renders this
        sandbox.assert.calledOnce(userService.getUserById);
        sandbox.assert.calledOnce(addressService.getAllAddresses);
    });

    it('GET /users/:id/addresses - should redirect if user does not exist', async () => {
        sandbox.stub(userService, 'getUserById').returns(Promise.resolve(null));

        const res = await request(app).get('/users/99/addresses');

        expect(res.status).to.equal(302);
        expect(res.header.location).to.equal('/users');
        sandbox.assert.calledOnce(userService.getUserById);
        sandbox.assert.notCalled(addressService.getAllAddresses);
    });
});
```