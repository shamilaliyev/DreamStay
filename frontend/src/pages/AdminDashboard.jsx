import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [stats, setStats] = useState({ users: 0, props: 0 });
    const [activeTab, setActiveTab] = useState('unverified_users');
    const [data, setData] = useState([]);

    // Data Loading
    useEffect(() => {
        loadData();
    }, [activeTab]);

    const loadData = async () => {
        try {
            let endpoint = '';
            switch (activeTab) {
                case 'unverified_users': endpoint = '/admin/users/unverified'; break;
                case 'all_users': endpoint = '/admin/users'; break;
                case 'unverified_admins': endpoint = '/admin/admins/unverified'; break;
                case 'unverified_props': endpoint = '/admin/properties/unverified'; break;
                case 'all_props': endpoint = '/admin/properties'; break;
                case 'pending_id_verification': endpoint = '/admin/users/id-pending'; break;
                default: return;
            }
            const res = await api.get(endpoint);
            setData(res.data);
        } catch (error) {
            console.error("Failed to load data", error);
        }
    };

    // Actions
    const verifyUser = async (id) => {
        try {
            await api.post(`/admin/users/${id}/verify`);
            alert('User Verified');
            loadData();
        } catch (e) { alert('Action failed'); }
    };

    const rejectUser = async (id) => {
        try {
            await api.post(`/admin/users/${id}/reject`);
            alert('User Rejected');
            loadData();
        } catch (e) { alert('Action failed'); }
    };

    const verifyAdmin = async (id) => {
        try {
            await api.post(`/admin/admins/${id}/verify`);
            alert('Admin Verified');
            loadData();
        } catch (e) { alert('Action failed'); }
    };

    const verifyProperty = async (id) => {
        try {
            await api.post(`/admin/properties/${id}/verify`);
            alert('Property Verified');
            loadData();
        } catch (e) { alert('Action failed'); }
    };

    const deleteProperty = async (id) => {
        if (!window.confirm('Are you sure you want to delete this property?')) return;
        try {
            await api.delete(`/admin/properties/${id}`);
            alert('Property Deleted');
            loadData();
        } catch (e) { alert('Action failed'); }
    };

    // New action for ID verification
    const verifyUserId = async (id) => {
        try {
            // Assuming this endpoint verifies the ID and also approves the user if not already.
            // If separate, adjust endpoint and logic.
            await api.post(`/admin/users/${id}/verify-id`); // Assuming a specific endpoint for ID verification
            alert('User ID Verified & Approved');
            loadData(); // Reload data to remove the verified user from the list
        } catch (e) {
            console.error("Failed to verify user ID", e);
            alert('Action failed');
        }
    };

    // Render Helpers
    const renderTabs = () => (
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '2rem', borderBottom: '1px solid var(--border)' }}>
            {[
                { id: 'unverified_users', label: 'Unverified Users' },
                { id: 'all_users', label: 'All Users' },
                { id: 'unverified_admins', label: 'Pending Admins' },
                { id: 'unverified_props', label: 'Unverified Properties' },
                { id: 'all_props', label: 'All Properties' },
                { id: 'pending_id_verification', label: 'Identity Verification' }, // New tab
            ].map(tab => (
                <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    style={{
                        background: 'none',
                        color: activeTab === tab.id ? 'var(--primary)' : 'var(--text)',
                        borderBottom: activeTab === tab.id ? '2px solid var(--primary)' : 'none',
                        padding: '1rem',
                        borderRadius: '0',
                        fontWeight: activeTab === tab.id ? 'bold' : 'normal'
                    }}
                >
                    {tab.label}
                </button>
            ))}
        </div>
    );

    const renderContent = () => {
        if (data.length === 0) return <p className="text-muted">No items found.</p>;

        return (
            <div className="grid">
                {data.map(item => (
                    <div key={item.id} className="card">
                        {/* User Card (for unverified_users, all_users, unverified_admins) */}
                        {(activeTab.includes('users') || activeTab.includes('admins')) && activeTab !== 'pending_id_verification' && (
                            <>
                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                    <h3>{item.name}</h3>
                                    <span style={{ fontSize: '0.8rem', padding: '0.2rem 0.5rem', background: item.isVerified ? '#d4edda' : '#fff3cd', color: item.isVerified ? '#155724' : '#856404', borderRadius: '4px' }}>
                                        {item.isVerified ? 'Verified' : 'Pending'}
                                    </span>
                                </div>
                                <p className="text-muted">{item.email}</p>
                                <p>Role: <strong>{item.role}</strong></p>
                                {item.governmentId && <p>Gov ID: {item.governmentId}</p>}
                                <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                                    <button onClick={() => navigate(`/admin/users/${item.id}`)} style={{ background: '#3b82f6' }}>View Details</button>
                                    {!item.isVerified && activeTab !== 'unverified_admins' && (
                                        <button onClick={() => verifyUser(item.id)}>Verify</button>
                                    )}
                                    {!item.isVerified && activeTab === 'unverified_admins' && (
                                        <button onClick={() => verifyAdmin(item.id)}>Verify Admin</button>
                                    )}

                                    {/* Prevent rejecting Main Admin (ID 1) */}
                                    {item.id !== 1 && (
                                        <button className="secondary" onClick={() => rejectUser(item.id)}>Reject</button>
                                    )}
                                </div>
                            </>
                        )}

                        {/* ID Verification Card */}
                        {activeTab === 'pending_id_verification' && (
                            <>
                                <h3>{item.name} ({item.role})</h3>
                                <p className="text-muted">{item.email}</p>

                                <div style={{ margin: '1rem 0', background: '#f8fafc', padding: '1rem', borderRadius: '4px' }}>
                                    <p style={{ fontWeight: '500', marginBottom: '0.5rem' }}>ID Document:</p>
                                    <SecureImage url={`/admin/users/${item.id}/id-document`} />
                                </div>

                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <button onClick={() => verifyUserId(item.id)} style={{ flex: 1 }}>Verify ID & Approve</button>
                                    <button className="secondary" onClick={() => rejectUser(item.id)} style={{ flex: 1 }}>Reject</button>
                                </div>
                            </>
                        )}

                        {/* Property Card */}
                        {activeTab.includes('props') && (
                            <>
                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                    <h3>{item.title}</h3>
                                    <span style={{ fontSize: '0.8rem', padding: '0.2rem 0.5rem', background: item.isVerified ? '#d4edda' : '#fff3cd', color: item.isVerified ? '#155724' : '#856404', borderRadius: '4px' }}>
                                        {item.isVerified ? 'Verified' : 'Pending'}
                                    </span>
                                </div>
                                <p className="text-muted">{item.location}</p>
                                <p>${item.price}</p>

                                {/* Show Owner Summary if available (new DTO) */}
                                {item.ownerName && (
                                    <div style={{ fontSize: '0.85rem', color: '#666', margin: '0.5rem 0', background: '#f8f9fa', padding: '0.5rem', borderRadius: '4px' }}>
                                        Listed by: <strong>{item.ownerName}</strong> ({item.ownerRole})
                                        {item.isOwnerApproved ? <span style={{ color: 'green', marginLeft: '5px' }}>Queries</span> : <span style={{ color: 'orange', marginLeft: '5px' }}>Pending User</span>}
                                    </div>
                                )}

                                <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem' }}>
                                    <button onClick={() => window.location.href = `/admin/properties/${item.id}`} style={{ flex: 1, background: '#17a2b8' }}>View Details</button>

                                    {!item.isVerified && (
                                        <button onClick={() => verifyProperty(item.id)} style={{ flex: 1 }}>Quick Verify</button>
                                    )}
                                    <button style={{ background: '#dc3545', color: 'white', border: 'none' }} onClick={() => deleteProperty(item.id)}>Delete</button>
                                </div>
                            </>
                        )}
                    </div>
                ))}
            </div>
        );
    };

    return (
        <div className="fade-in">
            <h1 className="title">Admin Dashboard</h1>
            {renderTabs()}
            {renderContent()}
        </div>
    );
};

// Helper component to fetch image with auth headers
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

    if (loading) return <div style={{ height: '200px', background: '#f8fafc', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>Loading ID...</div>;
    if (!src) return <div style={{ height: '50px', color: 'red' }}>Error loading image</div>;

    return (
        <img
            src={src}
            alt="Secure Content"
            style={{ maxWidth: '100%', maxHeight: '300px', borderRadius: '4px', border: '1px solid #e2e8f0', cursor: 'pointer' }}
            onClick={() => window.open(src, '_blank')}
        />
    );
};

export default AdminDashboard;
