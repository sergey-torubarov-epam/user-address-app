const swaggerUi = require('swagger-ui-express');
const swaggerJsDoc = require('swagger-jsdoc');

const swaggerOptions = {
  swaggerDefinition: {
    openapi: '3.0.0',
    info: {
      title: 'User Address Management System API',
      description: 'API documentation for managing users and their addresses',
      version: '1.0',
      contact: {
        name: 'UAMS Team',
        email: 'support@uams.com',
      },
    },
  },
  apis: ['./routes/*.js'], // Update this to the correct path where your route files are located
};

const swaggerSpec = swaggerJsDoc(swaggerOptions);

module.exports = (app) => {
  app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
};