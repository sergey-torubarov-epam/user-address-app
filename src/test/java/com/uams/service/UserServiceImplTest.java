# New file content starts from this line #
const { expect } = require('chai');
const sinon = require('sinon');
const UserService = require('../../services/userService');
const UserRepository = require('../../repositories/userRepository');

describe('UserService', () => {
    let userService;
    let userRepositoryMock;
    let user1;
    let user2;

    beforeEach(() => {
        userRepositoryMock = sinon.createStubInstance(UserRepository);
        userService = new UserService(userRepositoryMock);

        user1 = {
            userId: 1,
            email: 'john@example.com',
            firstName: 'John',
            lastName: 'Doe',
            mobileNumber: '1234567890',
            password: 'password'
        };

        user2 = {
            userId: 2,
            email: 'jane@example.com',
            firstName: 'Jane',
            lastName: 'Smith',
            mobileNumber: '0987654321',
            password: 'password'
        };
    });

    afterEach(() => {
        sinon.restore();
    });

    it('getAllUsers should return all users', async () => {
        // Arrange
        userRepositoryMock.findAll.resolves([user1, user2]);

        // Act
        const users = await userService.getAllUsers();

        // Assert
        expect(users).to.have.lengthOf(2);
        expect(users[0].email).to.equal(user1.email);
        expect(users[1].email).to.equal(user2.email);
        sinon.assert.calledOnce(userRepositoryMock.findAll);
    });

    it('getUserById with an existing ID should return the user', async () => {
        // Arrange
        userRepositoryMock.findById.resolves(user1);

        // Act
        const result = await userService.getUserById(1);

        // Assert
        expect(result).to.not.be.null;
        expect(result.email).to.equal(user1.email);
        sinon.assert.calledOnceWithExactly(userRepositoryMock.findById, 1);
    });

    it('getUserById with a non-existing ID should return null', async () => {
        // Arrange
        userRepositoryMock.findById.resolves(null);

        // Act
        const result = await userService.getUserById(3);

        // Assert
        expect(result).to.be.null;
        sinon.assert.calledOnceWithExactly(userRepositoryMock.findById, 3);
    });

    it('saveUser should save and return the user', async () => {
        // Arrange
        userRepositoryMock.save.resolves(user1);

        // Act
        const savedUser = await userService.saveUser(user1);

        // Assert
        expect(savedUser).to.not.be.null;
        expect(savedUser.email).to.equal(user1.email);
        sinon.assert.calledOnceWithExactly(userRepositoryMock.save, user1);
    });

    it('deleteUser should call repository deleteById', async () => {
        // Arrange
        userRepositoryMock.deleteById.resolves();

        // Act
        await userService.deleteUser(1);

        // Assert
        sinon.assert.calledOnceWithExactly(userRepositoryMock.deleteById, 1);
    });

    it('existsByEmail with an existing email should return true', async () => {
        // Arrange
        userRepositoryMock.existsByEmail.resolves(true);

        // Act
        const exists = await userService.existsByEmail('john@example.com');

        // Assert
        expect(exists).to.be.true;
        sinon.assert.calledOnceWithExactly(userRepositoryMock.existsByEmail, 'john@example.com');
    });

    it('existsByEmail with a non-existing email should return false', async () => {
        // Arrange
        userRepositoryMock.existsByEmail.resolves(false);

        // Act
        const exists = await userService.existsByEmail('nonexistent@example.com');

        // Assert
        expect(exists).to.be.false;
        sinon.assert.calledOnceWithExactly(userRepositoryMock.existsByEmail, 'nonexistent@example.com');
    });

    it('findByEmail with an existing email should return the user', async () => {
        // Arrange
        userRepositoryMock.findByEmail.resolves(user1);

        // Act
        const result = await userService.findByEmail('john@example.com');

        // Assert
        expect(result).to.not.be.null;
        expect(result.email).to.equal(user1.email);
        sinon.assert.calledOnceWithExactly(userRepositoryMock.findByEmail, 'john@example.com');
    });

    it('findByEmail with a non-existing email should return null', async () => {
        // Arrange
        userRepositoryMock.findByEmail.resolves(null);

        // Act
        const result = await userService.findByEmail('nonexistent@example.com');

        // Assert
        expect(result).to.be.null;
        sinon.assert.calledOnceWithExactly(userRepositoryMock.findByEmail, 'nonexistent@example.com');
    });
});
# New file content ends this line #