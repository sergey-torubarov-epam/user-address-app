const request = require('supertest');
const app = require('../app'); // your express app

describe('GET /addresses', () => {
    it('should return all addresses', async () => {
        const res = await request(app).get('/addresses');
        expect(res.statusCode).toEqual(200);
        expect(res.body).toHaveLength(2);
        expect(res.body[0].street).toBe('123 Main St');
    });
});

// Other tests...
