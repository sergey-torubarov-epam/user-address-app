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

describe('GET /api/users', () => {
    it('should return all users', async () => {
        const res = await request(app).get('/api/users');
        expect(res.statusCode).toEqual(200);
        expect(res.body.length).toBeGreaterThan(0);
    });
});

describe('POST /api/users', () => {
    it('should create a user', async () => {
        const res = await request(app)
            .post('/api/users')
            .send({ name: 'John Doe', email: 'john.doe@example.com' });
        expect(res.statusCode).toEqual(201);
        expect(res.body.name).toBe('John Doe');
    });
});