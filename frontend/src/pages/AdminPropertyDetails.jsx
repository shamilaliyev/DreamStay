import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const AdminPropertyDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [property, setProperty] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDetails = async () => {
            try {
                // Fetch using the new Admin DTO endpoint
                const res = await api.get(`/admin/properties/${id}`);
                setProperty(res.data);
            } catch (error) {
                alert("Failed to load details: " + (error.response?.data || error.message));
                navigate('/admin');
            } finally {
                setLoading(false);
            }
        };
        fetchDetails();
    }, [id, navigate]);

    const handleVerify = async () => {
        if (!window.confirm("Verify this property? It will become visible to all users.")) return;
        try {
            await api.post(`/admin/properties/${id}/verify`);
            alert("Property Verified!");
            navigate('/admin');
        } catch (error) {
            alert("Verification failed");
        }
    };

    const handleDelete = async () => {
        const reason = prompt("Enter reason for rejection/deletion:");
        if (!reason) return;

        try {
            await api.delete(`/admin/properties/${id}`);
            alert("Property Deleted");
            navigate('/admin');
        } catch (error) {
            alert("Deletion failed");
        }
    };

    if (loading) return <div className="text-center mt-5">Loading...</div>;
    if (!property) return <div className="text-center mt-5">Property not found</div>;

    return (
        <div className="fade-in" style={{ maxWidth: '1000px', margin: '2rem auto', display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
            {/* Left Column: Property Details */}
            <div>
                <button onClick={() => navigate('/admin')} style={{ background: 'none', color: '#666', marginBottom: '1rem', padding: 0 }}>← Back to Dashboard</button>

                <h1 className="title">{property.title}</h1>
                <p className="subtitle">{property.location}</p>

                {/* Gallery */}
                <div style={{ display: 'flex', gap: '1rem', overflowX: 'auto', paddingBottom: '1rem', marginBottom: '2rem' }}>
                    {property.photos && property.photos.length > 0 ? (
                        property.photos.map((photo, index) => (
                            <img key={`p-${index}`} src={photo} alt="" style={{ height: '250px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }} />
                        ))
                    ) : (
                        <div style={{ height: '250px', width: '100%', background: '#f1f5f9', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px' }}>No Photos</div>
                    )}
                    {property.videos && property.videos.map((video, index) => (
                        <video key={`v-${index}`} src={video} controls style={{ height: '250px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }} />
                    ))}
                </div>

                <div className="card" style={{ padding: '2rem' }}>
                    <h3>Description</h3>
                    <p style={{ whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>{property.description || 'No description provided.'}</p>

                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', gap: '1rem', marginTop: '2rem', textAlign: 'center' }}>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">Price</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>${property.price}</div>
                        </div>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">Area</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{property.area ? property.area : 'N/A'} m²</div>
                        </div>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">Floor</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{property.floor}</div>
                        </div>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">Rooms</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{property.rooms}</div>
                        </div>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">To Metro</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{property.distanceToMetro}km</div>
                        </div>
                        <div style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
                            <div className="text-muted text-sm">To Uni</div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{property.distanceToUniversity}km</div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Column: Owner & Action Panel */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                <div className="card">
                    <h3 style={{ borderBottom: '1px solid #eee', paddingBottom: '0.5rem' }}>Listed By</h3>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
                        <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: '#2196f3', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>
                            {property.ownerName?.charAt(0).toUpperCase()}
                        </div>
                        <div>
                            <div style={{ fontWeight: 'bold' }}>{property.ownerName}</div>
                            <div className="text-muted text-sm">{property.ownerEmail}</div>
                        </div>
                    </div>

                    <div style={{ display: 'grid', gap: '0.5rem', fontSize: '0.9rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>Role:</span>
                            <strong>{property.ownerRole}</strong>
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>Account:</span>
                            <span style={{ color: property.isOwnerApproved ? 'green' : 'orange' }}>
                                {property.isOwnerApproved ? 'Approved' : 'Pending'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>ID Verified:</span>
                            <span style={{ color: property.isOwnerIdVerified ? 'green' : 'red' }}>
                                {property.isOwnerIdVerified ? 'Yes' : 'No'}
                            </span>
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>Rating:</span>
                            <strong>{property.ownerRating.toFixed(1)} ⭐</strong>
                        </div>
                    </div>
                </div>

                <div className="card" style={{ border: '1px solid #ddd' }}>
                    <h3>Admin Actions</h3>
                    <p className="text-muted text-sm" style={{ marginBottom: '1rem' }}>
                        Review the property details and owner information above before making a decision.
                    </p>

                    {!property.isVerified && (
                        <button onClick={handleVerify} style={{ width: '100%', marginBottom: '0.5rem', background: '#28a745' }}>
                            ✅ Verify Property
                        </button>
                    )}

                    <button onClick={handleDelete} style={{ width: '100%', background: '#dc3545' }}>
                        ❌ Reject / Delete
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AdminPropertyDetails;
