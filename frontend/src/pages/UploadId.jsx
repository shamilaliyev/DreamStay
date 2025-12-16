import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const UploadId = () => {
    const navigate = useNavigate();
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [status, setStatus] = useState('loading'); // loading, pending, verified, unverified
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        // Check current status
        const checkStatus = async () => {
            try {
                const userStr = localStorage.getItem('user');
                if (!userStr) {
                    navigate('/login');
                    return;
                }
                const userId = JSON.parse(userStr).id;
                const response = await api.get(`/users/${userId}`);
                const user = response.data;

                if (user.idVerified && user.isApproved) {
                    setStatus('verified');
                } else if (user.idDocumentPath) {
                    setStatus('pending');
                } else {
                    setStatus('unverified');
                }
            } catch (err) {
                console.error("Failed to fetch status", err);
                setStatus('unverified');
            }
        };
        checkStatus();
    }, [navigate]);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
            setPreview(URL.createObjectURL(selectedFile));
            setError('');
        }
    };

    const handleUpload = async (e) => {
        e.preventDefault();
        if (!file) {
            setError('Please select a file');
            return;
        }

        setUploading(true);
        const formData = new FormData();
        formData.append('file', file);

        try {
            await api.post('/verification/upload-id', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            setStatus('pending');
            alert('ID uploaded successfully! Please wait for admin approval.');
        } catch (err) {
            console.error("Upload failed", err);
            setError(err.response?.data || 'Upload failed. Please try again.');
        } finally {
            setUploading(false);
        }
    };

    if (status === 'loading') return <div className="text-center mt-5">Loading...</div>;

    if (status === 'verified') {
        return (
            <div className="container" style={{ marginTop: '2rem', textAlign: 'center' }}>
                <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem' }}>
                    <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>✅</div>
                    <h2>Identity Verified</h2>
                    <p className="text-muted">Your identity has been verified. You can now post properties.</p>
                    <button onClick={() => navigate('/add-property')} style={{ marginTop: '1rem' }}>Post a Property</button>
                </div>
            </div>
        );
    }

    if (status === 'pending') {
        return (
            <div className="container" style={{ marginTop: '2rem', textAlign: 'center' }}>
                <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem' }}>
                    <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>⏳</div>
                    <h2>Verification Pending</h2>
                    <p className="text-muted">You have uploaded your ID. Logic is reviewing it now.</p>
                    <p className="text-sm">This usually takes 24 hours.</p>
                    <button onClick={() => setStatus('unverified')} style={{ marginTop: '1rem', background: 'none', color: '#666', border: '1px solid #ccc' }}>Re-upload Document</button>
                </div>
            </div>
        );
    }

    return (
        <div className="container animate-fade-in" style={{ marginTop: '2rem', maxWidth: '600px' }}>
            <h1 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Verify Identity</h1>
            <div className="card">
                <div style={{ background: '#f0f9ff', padding: '1rem', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #bae6fd' }}>
                    <h3 style={{ margin: '0 0 0.5rem 0', color: '#0369a1' }}>Safety First 🛡️</h3>
                    <p style={{ margin: 0, color: '#0c4a6e' }}>
                        To keep our community safe, all sellers and agents must verify their identity before posting properties.
                        Buyers do <strong>not</strong> need to verify.
                    </p>
                </div>

                <form onSubmit={handleUpload}>
                    <div style={{ marginBottom: '1.5rem' }}>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Upload Government ID</label>
                        <div style={{ border: '2px dashed #cbd5e1', borderRadius: '8px', padding: '2rem', textAlign: 'center', cursor: 'pointer', background: preview ? `url(${preview}) center/cover no-repeat` : '#f8fafc', height: '200px', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }} onClick={() => document.getElementById('id-upload').click()}>
                            {!preview && (
                                <>
                                    <span style={{ fontSize: '2rem', display: 'block', marginBottom: '0.5rem' }}>📷</span>
                                    <span className="text-muted">Click to upload Passport or ID Card</span>
                                </>
                            )}
                        </div>
                        <input
                            id="id-upload"
                            type="file"
                            accept="image/*"
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                        />
                    </div>

                    {error && <div className="text-red-500 text-sm mb-4">{error}</div>}

                    <button
                        type="submit"
                        disabled={uploading || !file}
                        style={{ width: '100%', padding: '1rem', opacity: uploading ? 0.7 : 1 }}
                    >
                        {uploading ? 'Uploading...' : 'Submit for Verification'}
                    </button>

                    <p className="text-xs text-center text-muted mt-4">
                        Your ID is stored securely and only visible to authorized admins.
                    </p>
                </form>
            </div>
        </div>
    );
};

export default UploadId;
