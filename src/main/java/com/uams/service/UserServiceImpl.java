# New code content starts from this line #
const UserRepository = require('../repository/UserRepository');

class UserService {
    constructor() {
        this.userRepository = new UserRepository();
    }

    async getAllUsers() {
        try {
            return await this.userRepository.findAll();
        } catch (error) {
            throw new Error(`Error fetching all users: ${error.message}`);
        }
    }

    async getUserById(id) {
        try {
            const user = await this.userRepository.findById(id);
            return user ? user : null;
        } catch (error) {
            throw new Error(`Error fetching user by ID: ${error.message}`);
        }
    }

    async saveUser(user) {
        try {
            return await this.userRepository.save(user);
        } catch (error) {
            throw new Error(`Error saving user: ${error.message}`);
        }
    }

    async deleteUser(id) {
        try {
            await this.userRepository.deleteById(id);
        } catch (error) {
            throw new Error(`Error deleting user: ${error.message}`);
        }
    }

    async existsByEmail(email) {
        try {
            return await this.userRepository.existsByEmail(email);
        } catch (error) {
            throw new Error(`Error checking if email exists: ${error.message}`);
        }
    }

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
# New code content ends this line #