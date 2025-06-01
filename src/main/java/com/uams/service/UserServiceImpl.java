const UserRepository = require('../repository/UserRepository');

/**
 * UserService: This class provides methods to interact with the UserRepository 
 * and encapsulates the business logic for user-related operations.
 */
class UserService {
    constructor() {
        this.userRepository = new UserRepository();
    }

    /**
     * Fetch all users from the repository.
     * @returns {Promise<Array>} A promise that resolves to an array of users.
     * @throws {Error} Throws an error if there is an issue fetching users.
     */
    async getAllUsers() {
        try {
            return await this.userRepository.findAll();
        } catch (error) {
            throw new Error(`Error fetching all users: ${error.message}`);
        }
    }

    /**
     * Fetch a user by their ID.
     * @param {string} id - The ID of the user to fetch.
     * @returns {Promise<Object|null>} A promise that resolves to the user object if found, or null if not found.
     * @throws {Error} Throws an error if there is an issue fetching the user by ID.
     */
    async getUserById(id) {
        try {
            const user = await this.userRepository.findById(id);
            return user ? user : null;
        } catch (error) {
            throw new Error(`Error fetching user by ID: ${error.message}`);
        }
    }

    /**
     * Save a new user or update an existing user in the repository.
     * @param {Object} user - The user object to save.
     * @returns {Promise<Object>} A promise that resolves to the saved user object.
     * @throws {Error} Throws an error if there is an issue saving the user.
     */
    async saveUser(user) {
        try {
            return await this.userRepository.save(user);
        } catch (error) {
            throw new Error(`Error saving user: ${error.message}`);
        }
    }

    /**
     * Delete a user by their ID.
     * @param {string} id - The ID of the user to delete.
     * @returns {Promise<void>} A promise that resolves when the user is deleted.
     * @throws {Error} Throws an error if there is an issue deleting the user.
     */
    async deleteUser(id) {
        try {
            await this.userRepository.deleteById(id);
        } catch (error) {
            throw new Error(`Error deleting user: ${error.message}`);
        }
    }

    /**
     * Check if a user with the given email exists in the repository.
     * @param {string} email - The email to check.
     * @returns {Promise<boolean>} A promise that resolves to true if the email exists, false otherwise.
     * @throws {Error} Throws an error if there is an issue checking the email.
     */
    async existsByEmail(email) {
        try {
            return await this.userRepository.existsByEmail(email);
        } catch (error) {
            throw new Error(`Error checking if email exists: ${error.message}`);
        }
    }

    /**
     * Fetch a user by their email.
     * @param {string} email - The email of the user to fetch.
     * @returns {Promise<Object|null>} A promise that resolves to the user object if found, or null if not found.
     * @throws {Error} Throws an error if there is an issue fetching the user by email.
     */
    async findByEmail(email) {
        try {
            const user = await this.userRepository.findByEmail(email);
            return user ? user : null;
        } catch (error) {
            throw new Error(`Error fetching user by email: ${error.message}`);
        }
    }
}

module.exports = UserService;