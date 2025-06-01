// services/userService.js

const users = new Map(); // Using a Map to simulate a database
let userIdCounter = 1;

/**
 * @class UserService
 * @classdesc Service class for managing users.
 */
class UserService {
    /**
     * Retrieves all users.
     * @returns {Array} List of all users.
     */
    getAllUsers() {
        return Array.from(users.values());
    }

    /**
     * Retrieves a user by their ID.
     * @param {Number} id - User ID.
     * @returns {Object|null} The user object if found, otherwise null.
     */
    getUserById(id) {
        return users.has(id) ? users.get(id) : null;
    }

    /**
     * Saves a user. If the user has an ID, updates the existing user.
     * Otherwise, creates a new user.
     * @param {Object} user - User data.
     * @returns {Object} The saved user.
     * @throws Will throw an error if the user with the specified ID does not exist.
     */
    saveUser(user) {
        if (user.id) {
            if (users.has(user.id)) {
                users.set(user.id, { ...users.get(user.id), ...user });
            } else {
                throw new Error(`User with ID ${user.id} does not exist.`);
            }
        } else {
            user.id = userIdCounter++;
            users.set(user.id, user);
        }
        return user;
    }

    /**
     * Deletes a user by their ID.
     * @param {Number} id - User ID.
     * @throws Will throw an error if the user with the specified ID does not exist.
     */
    deleteUser(id) {
        if (!users.has(id)) {
            throw new Error(`User with ID ${id} does not exist.`);
        }
        users.delete(id);
    }

    /**
     * Checks if a user exists by their email.
     * @param {String} email - User email.
     * @returns {Boolean} True if a user with the given email exists, otherwise false.
     */
    existsByEmail(email) {
        return Array.from(users.values()).some(user => user.email === email);
    }

    /**
     * Finds a user by their email.
     * @param {String} email - User email.
     * @returns {Object|null} The user object if found, otherwise null.
     */
    findByEmail(email) {
        const user = Array.from(users.values()).find(user => user.email === email);
        return user || null;
    }
}

module.exports = new UserService();