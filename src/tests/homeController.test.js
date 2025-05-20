const request = require('supertest');
const app = require('../app'); // your express app

describe('GET /', () => {
    it('should redirect to /users', async () => {
        const res = await request(app).get('/');
        expect(res.statusCode).toEqual(302);
        expect(res.header.location).toBe('/users');
    });
});
