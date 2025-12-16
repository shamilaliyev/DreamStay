import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const Register = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '', email: '', password: '', role: 'buyer', governmentId: ''
    });

    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [dragActive, setDragActive] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === 'dragenter' || e.type === 'dragover') {
            setDragActive(true);
        } else if (e.type === 'dragleave') {
            setDragActive(false);
        }
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);
        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            const selected = e.dataTransfer.files[0];
            setFile(selected);
            setPreview(URL.createObjectURL(selected));
        }
    };

    const handleFileChange = (e) => {
        const selected = e.target.files[0];
        setFile(selected);
        if (selected) {
            setPreview(URL.createObjectURL(selected));
        } else {
            setPreview(null);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const data = new FormData();
            data.append('name', formData.name);
            data.append('email', formData.email);
            data.append('password', formData.password);
            data.append('role', formData.role);
            data.append('governmentId', formData.governmentId);
            if (file) {
                data.append('file', file);
            }

            // Client-side check for seller/agent file requirement
            if ((formData.role === 'seller' || formData.role === 'agent') && !file) {
                alert('Please upload your ID document.');
                return;
            }

            await api.post('/auth/register', data, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            alert('Registration successful! Please login.');
            navigate('/login');
        } catch (error) {
            alert('Registration failed: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div style={{ maxWidth: '500px', margin: '3rem auto' }} className="card fade-in">
            <h2 className="title" style={{ textAlign: 'center' }}>Create Account</h2>
            <form onSubmit={handleRegister}>
                <div className="grid" style={{ gridTemplateColumns: '1fr', gap: '0.5rem' }}>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Full Name</label>
                        <input name="name" placeholder="John Doe" onChange={handleChange} required />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Email Address</label>
                        <input name="email" type="email" placeholder="john@example.com" onChange={handleChange} required />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Password</label>
                        <input name="password" type="password" placeholder="••••••••" onChange={handleChange} required />
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>I am a...</label>
                        <select name="role" onChange={handleChange}>
                            <option value="buyer">Buyer (Looking for a home)</option>
                            <option value="seller">Seller (Listing a property)</option>
                            <option value="agent">Agent</option>
                        </select>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Government ID Number</label>
                        <input name="governmentId" placeholder="ID Number" onChange={handleChange} required />
                    </div>

                    {(formData.role === 'seller' || formData.role === 'agent') && (
                        <div className="fade-in" style={{ marginTop: '0.5rem' }}>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', color: '#eab308' }}>
                                Upload ID Document (Required) ⚠️
                            </label>

                            <div
                                onClick={() => document.getElementById('reg-id-upload').click()}
                                onDragEnter={handleDrag}
                                onDragLeave={handleDrag}
                                onDragOver={handleDrag}
                                onDrop={handleDrop}
                                style={{
                                    border: `2px dashed ${dragActive ? 'var(--primary)' : '#cbd5e1'}`,
                                    borderRadius: '8px',
                                    padding: '1rem',
                                    textAlign: 'center',
                                    cursor: 'pointer',
                                    background: preview
                                        ? `url(${preview}) center/cover no-repeat`
                                        : (dragActive ? '#f0f9ff' : '#f8fafc'),
                                    height: '150px',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    justifyContent: 'center',
                                    alignItems: 'center',
                                    transition: 'all 0.2s ease',
                                    position: 'relative'
                                }}
                            >
                                {!preview && (
                                    <>
                                        <span style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
                                            {dragActive ? '📂' : '📷'}
                                        </span>
                                        <span style={{ color: '#64748b', fontSize: '0.9rem' }}>
                                            {dragActive ? 'Drop your ID here' : 'Click or Drag & Drop ID / Passport'}
                                        </span>
                                    </>
                                )}
                                {dragActive && (
                                    <div style={{
                                        position: 'absolute',
                                        top: 0,
                                        left: 0,
                                        right: 0,
                                        bottom: 0,
                                        background: 'rgba(255,255,255,0.8)',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        zIndex: 10
                                    }}>
                                        <span style={{ fontSize: '1.2rem', fontWeight: 'bold', color: 'var(--primary)' }}>Drop File Here</span>
                                    </div>
                                )}
                            </div>

                            <input
                                id="reg-id-upload"
                                type="file"
                                onChange={handleFileChange}
                                accept="image/*"
                                required
                                style={{ display: 'none' }}
                            />
                            {preview && (
                                <div style={{ textAlign: 'center', marginTop: '0.5rem' }}>
                                    <button
                                        type="button"
                                        onClick={(e) => { e.stopPropagation(); setFile(null); setPreview(null); }}
                                        style={{ background: '#ef4444', color: 'white', padding: '0.2rem 0.5rem', fontSize: '0.8rem', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                                    >
                                        Remove Image
                                    </button>
                                </div>
                            )}
                        </div>
                    )}

                    <button type="submit" style={{ marginTop: '1rem' }}>Create Account</button>
                </div>
            </form>
            <div style={{ marginTop: '1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
                Already have an account? <a href="/login" style={{ color: 'var(--primary)', fontWeight: '600' }}>Sign In</a>
            </div>
        </div>
    );
};

export default Register;
