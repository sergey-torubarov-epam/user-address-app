// repositories/userRepository.js

const { User } = require('../models'); // Adjust the path if necessary

/**
 * User Repository Interface
 * This interface provides methods to interact with the User model.
 * It includes functionalities to find a user by email and to check if an email exists.
 */

/**
 * Finds a user by their email.
 * 
 * @param {string} email - The email of the user to find.
 * @returns {Promise<Object|null>} - Returns the user object if found, otherwise null.
 * @throws {Error} - Throws an error if the operation fails.
 */
const findByEmail = async (email) => {
  try {
    return await User.findOne({ where: { email } }); // Sequelize query
  } catch (error) {
    throw new Error(`Error finding user by email: ${error.message}`);
  }
};

/**
 * Checks if a user with the given email exists.
 * 
 * @param {string} email - The email to check.
 * @returns {Promise<boolean>} - Returns true if the email exists, otherwise false.
 * @throws {Error} - Throws an error if the operation fails.
 */
const existsByEmail = async (email) => {
  try {
    const user = await User.findOne({ where: { email } }); // Sequelize query
    return !!user;
  } catch (error) {
    throw new Error(`Error checking if email exists: ${error.message}`);
  }
};

module.exports = {
  findByEmail,
  existsByEmail,
};