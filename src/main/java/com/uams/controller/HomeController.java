const express = require('express');
const router = express.Router();

/**
 * HomeController
 * This controller manages the home route of the application.
 * It currently has one method - home - which redirects the root URL to the /users URL.
 */

// Home route - Redirects to /users
router.get('/', (req, res) => {
    // This method redirects the root URL to /users when accessed.
    res.redirect('/users');
});

module.exports = router;