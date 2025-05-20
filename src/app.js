const express = require('express');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes');
const addressRoutes = require('./routes/addressRoutes');
const sequelize = require('./config/database');

const app = express();
app.use(bodyParser.json());

app.use('/api/users', userRoutes);
app.use('/api/addresses', addressRoutes);

sequelize.sync().then(() => {
    console.log('Database synced');
});

module.exports = app;