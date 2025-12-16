import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const AdminUserDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUserDetails();
    }, [id]);

    const fetchUserDetails = async () => {
        try {
            const response = await api.get(`/admin/users/${id}`);
            setUser(response.data);
            setLoading(false);
        } catch (error) {
            console.error("Failed to load user details", error);
            alert("Failed to load user details");
            navigate('/admin');
        }
    };

    const verifyUser = async () => {
        if (!window.confirm("Are you sure you want to verify this user?")) return;
        try {
            await api.post(`/admin/users/${id}/verify`);
            alert('User Verified');
            fetchUserDetails(); // Refresh to see updated status
        } catch (e) {
            alert('Action failed: ' + e.message);
        }
    };

    const rejectUser = async () => {
        if (!window.confirm("Are you sure you want to REJECT this user?")) return;
        try {
            await api.post(`/admin/users/${id}/reject`);
            alert('User Rejected');
            navigate('/admin');
        } catch (e) {
            alert('Action failed: ' + e.message);
        }
    };

    if (loading) return <div className="container mt-4">Loading details...</div>;
    if (!user) return <div className="container mt-4">User not found</div>;

    return (
        <div className="container fade-in">
            <button onClick={() => navigate('/admin')} className="btn-secondary mb-4">← Back to Dashboard</button>

            <div className="card">
                <div className="d-flex justify-content-between align-items-start flex-wrap gap-3">

                    <div className="d-flex align-items-center gap-3">
                        <div className="avatar-lg bg-light-gray text-gray-500 border-white shadow-md">
                            {user.avatarUrl ? (
                                <img src={user.avatarUrl} alt={user.name} className="w-100 h-100 object-cover" />
                            ) : (
                                <span>{user.name?.charAt(0).toUpperCase()}</span>
                            )}
                        </div>
                        <div>
                            <h1 className="text-2xl font-semibold text-primary mb-1">{user.name}</h1>
                            <div className="d-flex gap-2">
                                <span className="badge badge-info">{user.role}</span>
                                <span className={`badge ${user.isVerified ? 'badge-success' : 'badge-warning'}`}>
                                    {user.isVerified ? 'Verified' : 'Pending Verification'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <div className="d-flex gap-2">
                        {!user.isVerified && (
                            <button onClick={verifyUser} className="btn-success">Verify User</button>
                        )}
                        <button onClick={rejectUser} className="btn-danger">Reject User</button>
                    </div>
                </div>

                <hr className="my-4 border-t border-gray-200" />

                <div className="grid-2">
                    <div>
                        <h4 className="label text-lg mb-3">Contact Info</h4>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Phone:</strong> {user.phoneNumber || 'N/A'}</p>
                        <p><strong>City:</strong> {user.city || 'N/A'}</p>
                    </div>
                    <div>
                        <h4 className="label text-lg mb-3">Professional</h4>
                        <p><strong>Occupation:</strong> {user.occupation || 'N/A'}</p>
                        <p><strong>Bio:</strong> <span className="text-muted">{user.bio || 'No bio provided.'}</span></p>
                    </div>
                </div>

                <div className="mt-4 p-4 bg-light-blue rounded-md">
                    <h3 className="text-xl font-semibold mb-3">Identity Verification</h3>
                    <div className="grid-2">
                        <div>
                            <h4 className="label">Status</h4>
                            <p className="mt-2">
                                <strong>ID Verified: </strong>
                                {user.idVerified ? <span className="text-success">Yes</span> : <span style={{ color: user.idDocumentPath ? 'orange' : 'red' }}>{user.idDocumentPath ? 'Pending Review' : 'Not Uploaded'}</span>}
                            </p>
                            <p>
                                <strong>Government ID: </strong>
                                {user.governmentId || 'Not provided'}
                            </p>
                        </div>
                        {(user.idDocumentPath || user.governmentId) && (
                            <div>
                                <h4 className="label">Document</h4>
                                {user.idDocumentPath ? (
                                    <SecureImage url={`/admin/users/${user.id}/id-document`} />
                                ) : (
                                    <p className="text-muted">No document image uploaded.</p>
                                )}
                            </div>
                        )}
                    </div>

                    {!user.idVerified && user.idDocumentPath && (
                        <div className="mt-4 p-3 bg-yellow-100 rounded-sm text-yellow-800">
                            <p className="m-0">
                                ⚠️ Review the document above. If it matches the user details, click "Verify User" at the top to approve them.
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

// Helper component reused (or should be exported from AdminDashboard/components)
const SecureImage = ({ url }) => {
    const [src, setSrc] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchImage = async () => {
            try {
                const response = await api.get(url, { responseType: 'blob' });
                const blobUrl = URL.createObjectURL(response.data);
                setSrc(blobUrl);
            } catch (err) {
                console.error("Failed to load secure image", err);
            } finally {
                setLoading(false);
            }
        };
        fetchImage();
    }, [url]);

    if (loading) return <div style={{ height: '200px', background: '#e2e8f0', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '4px' }}>Loading ID Preview...</div>;
    if (!src) return <div style={{ color: 'red', fontSize: '0.9rem' }}>Error loading image</div>;

    return (
        <div style={{ position: 'relative' }}>
            <img
                src={src}
                alt="Secure Content"
                style={{ maxWidth: '100%', maxHeight: '300px', borderRadius: '4px', border: '1px solid #cbd5e1', cursor: 'pointer' }}
                onClick={() => window.open(src, '_blank')}
            />
            <div style={{ fontSize: '0.8rem', color: '#64748b', marginTop: '0.25rem' }}>Click to view full size</div>
        </div>

    );
};

export default AdminUserDetails;
