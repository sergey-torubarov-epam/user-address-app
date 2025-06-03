const { Model, DataTypes } = require('sequelize');
const sequelize = require('../config/database');

/**
 * Represents an address of a user or a place.
 * This model is used to store and manage address information in the database.
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
 * 
 * This model definition uses Sequelize ORM to map the Address entity to the 'addresses' table in the database.
 */

Address.init(
  {
    /**
     * Primary key, auto-incremented Unique identifier for the address.
     * This field serves as the main identifier for each address record.
     */
    addressId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
    },
    /**
     * Nullable string for the building name or number.
     * This field is optional and can be left empty if not applicable.
     */
    buildingName: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    /**
     * String for the street name which is required and must not be empty.
     * This field is mandatory and represents the street part of the address.
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
     * This field is mandatory and represents the city part of the address.
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
     * This field is mandatory and represents the state or province part of the address.
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
     * This field is mandatory and represents the postal or ZIP code of the address.
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
    timestamps: false, // Indicates that this model doesn't use createdAt and updatedAt fields
  }
);

// Export the Address model to be used in other parts of the application
module.exports = Address;