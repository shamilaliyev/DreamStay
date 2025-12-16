import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/auth/login', { email, password });
            const user = response.data;
            localStorage.setItem('user', JSON.stringify(user));

            // Force reload/navigation to ensure state is picked up
            // Force reload/navigation to ensure state is picked up
            if (user.role === 'admin') {
                window.location.href = '/admin';
            } else if (user.role === 'seller' || user.role === 'agent') {
                if (!user.idVerified) {
                    window.location.href = '/verify-identity';
                } else {
                    window.location.href = '/my-properties';
                }
            } else {
                window.location.href = '/properties';
            }
        } catch (error) {
            alert('Login failed: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '3rem auto' }} className="card fade-in">
            <h2 className="title" style={{ textAlign: 'center' }}>Welcome Back</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Email</label>
                    <input type="email" placeholder="name@example.com" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Password</label>
                    <input type="password" placeholder="••••••••" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <button type="submit" style={{ width: '100%', marginTop: '1rem' }}>Sign In</button>
            </form>
            <div style={{ marginTop: '1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
                Don't have an account? <a href="/register" style={{ color: 'var(--primary)', fontWeight: '600' }}>Register</a>
            </div>
        </div>
    );
};

export default Login;
