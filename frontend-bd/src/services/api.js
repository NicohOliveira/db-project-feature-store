import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/bd2026',
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
});

export default api;
