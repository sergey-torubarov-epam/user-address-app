const { Model, DataTypes } = require('sequelize');
const sequelize = require('../config/database');

class Address extends Model {}

Address.init(
  {
    addressId: {
      type: DataTypes.BIGINT,
      autoIncrement: true,
      primaryKey: true,
    },
    buildingName: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    street: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Street is required',
        },
      },
    },
    city: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'City is required',
        },
      },
    },
    state: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'State is required',
        },
      },
    },
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