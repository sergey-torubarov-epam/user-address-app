const express = require('express');
const router = express.Router();

// Home route - Redirects to /users
router.get('/', (req, res) => {
    res.redirect('/users');
});

module.exports = router;