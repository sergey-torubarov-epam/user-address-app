const swaggerUi = require('swagger-ui-express');
const swaggerJsDoc = require('swagger-jsdoc');

/**
 * Swagger Options for API documentation
 * This configuration sets up the Swagger documentation for the User Address Management System API.
 * It provides details such as API version, title, description, and contact information.
 */
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
  apis: ['./routes/*.js'], // Path to the API route files
};

/**
 * Generates the Swagger specification based on the given options.
 * This object will be used to setup and serve the API documentation.
 */
const swaggerSpec = swaggerJsDoc(swaggerOptions);

/**
 * Sets up the Swagger UI middleware for the Express app.
 * It mounts the API documentation under the /api-docs route.
 *
 * @param {Object} app - The Express application instance.
 */
module.exports = (app) => {
  app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
};