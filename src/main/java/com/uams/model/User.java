const { DataTypes, Model } = require('sequelize');
const sequelize = require('../config/database');

class User extends Model {}

User.init(
  {
    userId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
      field: 'user_id',
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
      validate: {
        notNull: { msg: 'Email is required' },
        isEmail: { msg: 'Please provide a valid email address' },
      },
    },
    firstName: {
      type: DataTypes.STRING,
      allowNull: false,
      field: 'first_name',
      validate: {
        notNull: { msg: 'First name is required' },
      },
    },
    lastName: {
      type: DataTypes.STRING,
      allowNull: false,
      field: 'last_name',
      validate: {
        notNull: { msg: 'Last name is required' },
      },
    },
    mobileNumber: {
      type: DataTypes.STRING,
      field: 'mobile_number',
    },
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

// Define the many-to-many relationship
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