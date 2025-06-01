const { DataTypes, Model } = require('sequelize');
const sequelize = require('../config/database');

/**
 * User model representing user data.
 */
class User extends Model {}

User.init(
  {
    /**
     * Unique identifier for each user.
     * Automatically increments with each new user.
     */
    userId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
      field: 'user_id',
    },
    /**
     * Email address of the user.
     * Must be unique and a valid email format.
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
     * Cannot be null.
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
     * Cannot be null.
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
     * Can be null.
     */
    mobileNumber: {
      type: DataTypes.STRING,
      field: 'mobile_number',
    },
    /**
     * Password for user authentication.
     * Cannot be null and must be at least 6 characters long.
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
    timestamps: false,
  }
);

const Address = require('./address'); // Assuming Address model is defined similarly

/**
 * Establishes a many-to-many relationship between User and Address.
 * Creates a junction table named 'user_address' with foreign keys: 'user_id' and 'address_id'.
 */
User.belongsToMany(Address, {
  through: 'user_address',
  foreignKey: 'user_id',
  otherKey: 'address_id',
});
Address.belongsToMany(User, {
  through: 'user_address',
  foreignKey: 'address_id',
  otherKey: 'user_id',
});

module.exports = User;