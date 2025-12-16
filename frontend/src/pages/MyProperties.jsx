import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

const MyProperties = () => {
    const [properties, setProperties] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMyProps = async () => {
            try {
                const response = await api.get('/properties/my');
                setProperties(response.data);
            } catch (error) {
                console.error("Failed to fetch my properties", error);
            } finally {
                setLoading(false);
            }
        };
        fetchMyProps();
    }, []);

    if (loading) return <div className="text-center mt-5">Loading...</div>;

    return (
        <div className="fade-in">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 className="title">My Listings</h1>
                <Link to="/add-property">
                    <button>+ Add New Property</button>
                </Link>
            </div>

            {properties.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '3rem', background: 'var(--card-bg)', borderRadius: 'var(--radius)' }}>
                    <h3>No properties listed yet.</h3>
                    <p>Start earning by adding your first property!</p>
                </div>
            ) : (
                <div className="grid">
                    {properties.map(property => (
                        <div key={property.id} className="card property-card">
                            <div className="property-image-placeholder" style={{ height: '200px', background: '#ccc' }}>
                                {/* Mock Image */}
                            </div>
                            <div style={{ padding: '1.5rem' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                    <h3 style={{ margin: 0 }}>{property.title}</h3>
                                    <span style={{ color: 'var(--primary)', fontWeight: 'bold' }}>${property.price}</span>
                                </div>
                                <p className="text-muted" style={{ margin: '0.5rem 0' }}>{property.location}</p>
                                <div style={{ display: 'flex', gap: '1rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                                    <span>{property.rooms} Rooms</span>
                                    <span>Verified: {property.isVerified ? 'Yes' : 'No'}</span>
                                </div>
                                <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem' }}>
                                    <Link to={`/properties/${property.id}`} style={{ flex: 1 }}>
                                        <button style={{ width: '100%', background: 'transparent', border: '1px solid var(--border)', color: 'var(--text)' }}>View</button>
                                    </Link>
                                    <Link to={`/edit-property/${property.id}`} style={{ flex: 1 }}>
                                        <button style={{ width: '100%', background: 'var(--primary)', color: 'white', border: '1px solid var(--primary)' }}>Edit</button>
                                    </Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyProperties;
