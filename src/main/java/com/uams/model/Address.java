const { Model, DataTypes } = require('sequelize');
const sequelize = require('../config/database');

/**
 * Represents an address of a user or a place.
 */
class Address extends Model {}

/**
 * Initializes the Address model with the following fields:
 * - addressId: Primary key, auto-incremented Unique identifier for the address.
 * - buildingName: Nullable string for the building name or number.
 * - street: String for the street name which is required and must not be empty.
 * - city: String for the city name which is required and must not be empty.
 * - state: String for the state name which is required and must not be empty.
 * - pincode: String for the postal code which is required and must not be empty.
 * 
 * No Lombok annotations are available or used in JavaScript/Node.js context.
 */

Address.init(
  {
    /**
     * Primary key, auto-incremented Unique identifier for the address.
     */
    addressId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
    },
    /**
     * Nullable string for the building name or number.
     */
    buildingName: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    /**
     * String for the street name which is required and must not be empty.
     */
    street: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Street is required',
        },
      },
    },
    /**
     * String for the city name which is required and must not be empty.
     */
    city: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'City is required',
        },
      },
    },
    /**
     * String for the state name which is required and must not be empty.
     */
    state: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'State is required',
        },
      },
    },
    /**
     * String for the postal code which is required and must not be empty.
     */
    pincode: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Pincode is required',
        },
      },
    },
  },
  {
    sequelize,
    modelName: 'Address',
    tableName: 'addresses',
    timestamps: false,
  }
);

module.exports = Address;