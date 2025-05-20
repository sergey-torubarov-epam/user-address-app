// repositories/userRepository.js

const { User } = require('../models'); // Adjust the path if necessary

const findByEmail = async (email) => {
  try {
    return await User.findOne({ where: { email } }); // Sequelize query
  } catch (error) {
    throw new Error(`Error finding user by email: ${error.message}`);
  }
};

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