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

describe('GET /api/addresses', () => {
    it('should return all addresses', async () => {
        const res = await request(app).get('/api/addresses');
        expect(res.statusCode).toEqual(200);
        expect(res.body.length).toBeGreaterThan(0);
    });
});

describe('POST /api/addresses', () => {
    it('should create an address', async () => {
        const res = await request(app)
            .post('/api/addresses')
            .send({ street: '123 Main St', city: 'Anytown', state: 'CA', zip: '12345', userId: 1 });
        expect(res.statusCode).toEqual(201);
        expect(res.body.street).toBe('123 Main St');
    });
});

// Other tests...