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
     * @description This method returns a list of all users in the system.
     * It's useful for admin interfaces or when you need to display all users.
     * @returns {Array} List of all users.
     */
    getAllUsers() {
        return Array.from(users.values());
    }

    /**
     * Retrieves a user by their ID.
     * @description This method finds and returns a specific user based on their unique ID.
     * It's commonly used when you need to fetch details of a particular user.
     * @param {Number} id - User ID.
     * @returns {Object|null} The user object if found, otherwise null.
     */
    getUserById(id) {
        return users.has(id) ? users.get(id) : null;
    }

    /**
     * Saves a user. If the user has an ID, updates the existing user.
     * Otherwise, creates a new user.
     * @description This method handles both creating new users and updating existing ones.
     * It's essential for user registration and profile update functionalities.
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
     * @description This method removes a user from the system based on their ID.
     * It's typically used in account deletion or user management scenarios.
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
     * @description This method is used to verify if an email is already registered in the system.
     * It's crucial for preventing duplicate user registrations and ensuring email uniqueness.
     * @param {String} email - User email.
     * @returns {Boolean} True if a user with the given email exists, otherwise false.
     */
    existsByEmail(email) {
        return Array.from(users.values()).some(user => user.email === email);
    }

    /**
     * Finds a user by their email.
     * @description This method retrieves a user based on their email address.
     * It's commonly used in login processes or when you need to find a user without knowing their ID.
     * @param {String} email - User email.
     * @returns {Object|null} The user object if found, otherwise null.
     */
    findByEmail(email) {
        const user = Array.from(users.values()).find(user => user.email === email);
        return user || null;
    }
}

module.exports = new UserService();