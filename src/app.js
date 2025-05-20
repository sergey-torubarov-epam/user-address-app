const express = require('express');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes');
const addressRoutes = require('./routes/addressRoutes');
const homeRoutes = require('./routes/homeRoutes');
const sequelize = require('./config/database');

const app = express();
app.use(bodyParser.json());

app.use('/api', userRoutes);
app.use('/api', addressRoutes);
app.use('/', homeRoutes);

sequelize.sync().then(() => {
    console.log('Database synced');
});

module.exports = app;