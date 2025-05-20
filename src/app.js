const express = require('express');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes');
const sequelize = require('./config/database');

const app = express();
app.use(bodyParser.json());

app.use('/api', userRoutes);

sequelize.sync().then(() => {
    console.log('Database synced');
});

module.exports = app;
