const request = require('supertest');
const app = require('../app'); // your express app

describe('GET /users', () => {
    it('should return all users', async () => {
        const res = await request(app).get('/users');
        expect(res.statusCode).toEqual(200);
        expect(res.body).toHaveLength(2);
        expect(res.body[0].name).toBe('John');
    });
});
