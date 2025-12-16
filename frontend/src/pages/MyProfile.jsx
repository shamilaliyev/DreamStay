import React, { useState, useEffect, useRef } from 'react';
import api from '../api/axiosConfig';

const MyProfile = () => {
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({});
    const [avatarPreview, setAvatarPreview] = useState(null);
    const fileInputRef = useRef(null);

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const response = await api.get('/profile/me');
            const data = response.data || {};
            setUser(data);
            setFormData({
                name: data.name || '',
                bio: data.bio || '',
                phoneNumber: data.phoneNumber || '',
                city: data.city || '',
                occupation: data.occupation || ''
            });
            setLoading(false);
        } catch (error) {
            console.error("Failed to fetch profile", error);
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        try {
            const response = await api.put('/profile/me', formData);
            setUser(response.data);
            setIsEditing(false);
            alert("Profile updated successfully!");
        } catch (error) {
            alert("Failed to update profile: " + (error.response?.data || error.message));
        }
    };

    const handleAvatarChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Preview
        setAvatarPreview(URL.createObjectURL(file));

        // Upload immediately
        try {
            const data = new FormData();
            data.append('file', file);
            const response = await api.post('/profile/avatar', data, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            // Update user avatar URL from response or refresh
            if (response.data.avatarUrl) {
                setUser(prev => ({ ...prev, avatarUrl: response.data.avatarUrl }));
            } else {
                fetchProfile();
            }
        } catch (error) {
            alert("Failed to upload avatar: " + (error.response?.data || error.message));
        }
    };

    if (loading) return <div className="text-center mt-5">Loading...</div>;
    if (!user) return <div className="text-center mt-5">Failed to load profile. Please try logging in again.</div>;

    return (
        <div className="container animate-fade-in" style={{ marginTop: '2rem', maxWidth: '800px', paddingBottom: '4rem' }}>
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                    <h1 className="title" style={{ margin: 0 }}>My Profile</h1>
                    <button
                        onClick={() => isEditing ? handleSave() : setIsEditing(true)}
                        style={{
                            background: isEditing ? '#22c55e' : 'var(--primary)',
                            color: 'white',
                            padding: '0.5rem 1.5rem',
                            borderRadius: '6px',
                            border: 'none',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                            fontWeight: '600',
                            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                        }}
                    >
                        {isEditing ? (
                            <>
                                <span>Save Changes</span>
                                <span style={{ fontSize: '1.2rem' }}>✓</span>
                            </>
                        ) : (
                            <>
                                <span>Edit Profile</span>
                                <span style={{ fontSize: '1.2rem' }}>✎</span>
                            </>
                        )}
                    </button>
                </div>

                {isEditing && (
                    <div style={{ background: '#eff6ff', borderLeft: '4px solid #3b82f6', padding: '1rem', marginBottom: '2rem', borderRadius: '4px' }}>
                        <p style={{ margin: 0, color: '#1e40af' }}>
                            <strong>Editing Mode:</strong> You can now update your details below. Click "Save Changes" when you're done.
                        </p>
                    </div>
                )}

                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '2rem' }}>
                    <div
                        style={{
                            width: '120px',
                            height: '120px',
                            borderRadius: '50%',
                            overflow: 'hidden',
                            border: `4px solid ${isEditing ? 'var(--primary)' : 'white'}`,
                            boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                            position: 'relative',
                            cursor: isEditing ? 'pointer' : 'default',
                            background: '#f1f5f9',
                            transition: 'border-color 0.3s'
                        }}
                        onClick={() => isEditing && fileInputRef.current?.click()}
                        title={isEditing ? "Click to change photo" : ""}
                    >
                        {(avatarPreview || user.avatarUrl) ? (
                            <img
                                src={avatarPreview || user.avatarUrl}
                                alt="Profile"
                                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                            />
                        ) : (
                            <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '3rem', color: '#94a3b8' }}>
                                {user.name?.charAt(0).toUpperCase()}
                            </div>
                        )}

                        {isEditing && (
                            <div style={{
                                position: 'absolute',
                                bottom: 0, left: 0, right: 0,
                                background: 'rgba(0,0,0,0.6)',
                                color: 'white',
                                fontSize: '0.75rem',
                                textAlign: 'center',
                                padding: '0.3rem',
                                backdropFilter: 'blur(2px)'
                            }}>
                                📷 Change
                            </div>
                        )}
                    </div>
                    <input
                        ref={fileInputRef}
                        type="file"
                        accept="image/*"
                        onChange={handleAvatarChange}
                        style={{ display: 'none' }}
                        disabled={!isEditing}
                    />

                    <h2 style={{ marginTop: '1rem', marginBottom: '0.2rem' }}>{user.name}</h2>
                    <span className="badge" style={{ background: '#e0f2fe', color: '#0369a1' }}>{user.role}</span>
                </div>

                <div className="grid">
                    <div>
                        <label className="label">Full Name</label>
                        <input
                            name="name"
                            value={formData.name || ''}
                            onChange={handleChange}
                            disabled={!isEditing}
                            className="input"
                            style={{ borderColor: isEditing ? '#94a3b8' : 'transparent' }}
                        />
                    </div>
                    <div>
                        <label className="label">Email <span style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 'normal' }}>(Cannot be changed)</span></label>
                        <input
                            value={user.email}
                            disabled
                            className="input"
                            style={{ background: '#f8fafc', color: '#64748b', cursor: 'not-allowed' }}
                        />
                    </div>

                    <div style={{ gridColumn: '1 / -1' }}>
                        <label className="label">Bio</label>
                        <textarea
                            name="bio"
                            value={formData.bio || ''}
                            onChange={handleChange}
                            disabled={!isEditing}
                            className="input"
                            rows="3"
                            placeholder={isEditing ? "Tell us about yourself..." : "No bio provided."}
                            style={{ borderColor: isEditing ? '#94a3b8' : 'transparent' }}
                        />
                    </div>

                    <div>
                        <label className="label">Phone Number</label>
                        <input
                            name="phoneNumber"
                            value={formData.phoneNumber || ''}
                            onChange={(e) => {
                                let val = e.target.value.replace(/\D/g, '');
                                if (val.startsWith('994')) val = val.substring(3); // Strip prefix if re-typed
                                if (val.length > 9) val = val.substring(0, 9); // Max length check (2+3+2+2 = 9 digits)

                                let formatted = '';
                                if (val.length > 0) formatted += '+994 ';
                                if (val.length > 0) formatted += val.substring(0, 2);
                                if (val.length >= 3) formatted += ' ' + val.substring(2, 5);
                                if (val.length >= 6) formatted += ' ' + val.substring(5, 7);
                                if (val.length >= 8) formatted += ' ' + val.substring(7, 9);

                                setFormData({ ...formData, phoneNumber: formatted });
                            }}
                            disabled={!isEditing}
                            className="input"
                            placeholder="+994 50 123 45 67"
                            style={{ borderColor: isEditing ? '#94a3b8' : 'transparent' }}
                        />
                    </div>

                    <div>
                        <label className="label">City</label>
                        <input
                            name="city"
                            value={formData.city || ''}
                            onChange={handleChange}
                            disabled={!isEditing}
                            className="input"
                            placeholder="Baku"
                            style={{ borderColor: isEditing ? '#94a3b8' : 'transparent' }}
                        />
                    </div>

                    <div>
                        <label className="label">Occupation</label>
                        <input
                            name="occupation"
                            value={formData.occupation || ''}
                            onChange={handleChange}
                            disabled={!isEditing}
                            className="input"
                            placeholder="Software Engineer"
                            style={{ borderColor: isEditing ? '#94a3b8' : 'transparent' }}
                        />
                    </div>
                </div>

                <div style={{ marginTop: '2rem', paddingTop: '1rem', borderTop: '1px solid #e2e8f0' }}>
                    <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>Verification Status</h3>
                    <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: user.emailVerified ? 'green' : 'orange', background: user.emailVerified ? '#dcfce7' : '#ffedd5', padding: '0.25rem 0.75rem', borderRadius: '99px', fontSize: '0.85rem', fontWeight: '600' }}>
                            {user.emailVerified ? '✓ Verified' : '⚠ Pending'} Email
                        </div>
                        {(user.role === 'seller' || user.role === 'agent') && (
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: user.idVerified ? 'green' : 'orange', background: user.idVerified ? '#dcfce7' : '#ffedd5', padding: '0.25rem 0.75rem', borderRadius: '99px', fontSize: '0.85rem', fontWeight: '600' }}>
                                {user.idVerified ? '✓ Verified' : '⚠ Pending'} ID
                            </div>
                        )}
                    </div>
                </div>

                {isEditing && (
                    <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                        <button
                            onClick={() => { setIsEditing(false); setFormData({ ...user }); }} // Reset functionality
                            style={{ background: '#94a3b8', padding: '0.75rem 1.5rem', color: 'white', borderRadius: '6px', border: 'none', fontWeight: '600' }}
                        >
                            Cancel
                        </button>
                        <button
                            onClick={handleSave}
                            style={{ background: '#22c55e', padding: '0.75rem 2rem', color: 'white', borderRadius: '6px', border: 'none', fontWeight: '600', boxShadow: '0 4px 6px -1px rgba(34, 197, 94, 0.3)' }}
                        >
                            Save Changes
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MyProfile;
