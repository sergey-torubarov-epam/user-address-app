const { DataTypes, Model } = require('sequelize');
const sequelize = require('../config/database');

/**
 * User model representing user data in the system.
 * This class extends Sequelize's Model class to define the schema and behavior of the User entity.
 */
class User extends Model {}

User.init(
  {
    /**
     * Unique identifier for each user.
     * Automatically increments with each new user creation.
     * Serves as the primary key in the database.
     */
    userId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
      field: 'user_id',
    },
    /**
     * Email address of the user.
     * Must be unique across all users and conform to a valid email format.
     * Used for user identification and communication.
     */
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
      validate: {
        notNull: { msg: 'Email is required' },
        isEmail: { msg: 'Please provide a valid email address' },
      },
    },
    /**
     * First name of the user.
     * Required field that cannot be null.
     * Represents the user's given name.
     */
    firstName: {
      type: DataTypes.STRING,
      allowNull: false,
      field: 'first_name',
      validate: {
        notNull: { msg: 'First name is required' },
      },
    },
    /**
     * Last name of the user.
     * Required field that cannot be null.
     * Represents the user's family name or surname.
     */
    lastName: {
      type: DataTypes.STRING,
      allowNull: false,
      field: 'last_name',
      validate: {
        notNull: { msg: 'Last name is required' },
      },
    },
    /**
     * Mobile number of the user.
     * Optional field that can be null.
     * Stores the user's contact phone number.
     */
    mobileNumber: {
      type: DataTypes.STRING,
      field: 'mobile_number',
    },
    /**
     * Password for user authentication.
     * Required field that cannot be null.
     * Must be at least 6 characters long for security purposes.
     * Note: In production, passwords should be hashed before storage.
     */
    password: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notNull: { msg: 'Password is required' },
        len: {
          args: [6],
          msg: 'Password must be at least 6 characters long',
        },
      },
    },
  },
  {
    sequelize,
    modelName: 'User',
    tableName: 'users',
    timestamps: false, // Disables automatic timestamp fields (createdAt, updatedAt)
  }
);

const Address = require('./address'); // Importing the Address model

/**
 * Establishes a many-to-many relationship between User and Address.
 * This relationship is implemented using a junction table named 'user_address'.
 * It allows each user to have multiple addresses and each address to be associated with multiple users.
 */
User.belongsToMany(Address, {
  through: 'user_address', // Name of the junction table
  foreignKey: 'user_id', // Foreign key in the junction table referencing User
  otherKey: 'address_id', // Foreign key in the junction table referencing Address
});

/**
 * Reciprocal relationship definition for the many-to-many association.
 * This allows querying the relationship from the Address side as well.
 */
Address.belongsToMany(User, {
  through: 'user_address',
  foreignKey: 'address_id',
  otherKey: 'user_id',
});

module.exports = User;