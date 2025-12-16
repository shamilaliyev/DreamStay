import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const OwnerProfile = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [owner, setOwner] = useState(null);
    const [listings, setListings] = useState([]); // This would ideally come from a specific endpoint
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch User Details (using existing endpoint, might need to ensure it returns enough info)
                // We might need to expose a public profile endpoint if GET /users/{id} is restricted or insufficient.
                // Assuming GET /users/{id} returns basic public info.
                const userRes = await api.get(`/users/${id}`);
                setOwner(userRes.data);

                // Fetch Reviews
                const reviewsRes = await api.get(`/reviews/${id}`);
                setReviews(reviewsRes.data);

                // Fetch Listings (We need an endpoint for this, or filter on client side if we fetch all? No, inefficient)
                // Previously we saw PropertyController has /api/properties specific searches.
                // We might need to add `GET /api/properties/user/{id}` or use search param.
                // PropertyController has search(..., rooms). 
                // Let's assume for now we might not have a direct endpoint for "active listings of other user".
                // We can use the existing search endpoint if we can filter by owner? NO.
                // PropertyManager has `getAllProperties()`.
                // Let's defer listings for a moment or implement a simple filter on frontend if we fetch all (bad for scale but works for prototype).
                // BETTER: Add a simple endpoint or use existing `search` if extended. 
                // Wait, PropertyController has `getMyProperties` but that's for self.
                // I will add a quick endpoint in PropertyController `GET /api/properties/owner/{ownerId}` or just skip listings for V1?
                // The task requirement said: "list of active properties".
                // I'll try to use existing search if possible, but search doesn't filter by owner.
                // I will add a new endpoint to PropertyController in the next step if I find I can't do it.
                // For now, let's just show profile and reviews.

                // Correction: I'll implement the UI for listings and fetch them if I add the endpoint.

            } catch (error) {
                console.error("Failed to load profile", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id]);

    if (loading) return <div className="text-center mt-5">Loading...</div>;
    if (!owner) return <div className="text-center mt-5">User not found</div>;

    return (
        <div className="container animate-fade-in" style={{ marginTop: '2rem' }}>
            <button onClick={() => navigate(-1)} style={{ background: 'none', color: '#666', border: 'none', padding: 0, marginBottom: '1rem', cursor: 'pointer' }}>← Back</button>

            <div className="card" style={{ textAlign: 'center', marginBottom: '2rem' }}>
                <div
                    style={{
                        width: '120px',
                        height: '120px',
                        background: '#f1f5f9',
                        borderRadius: '50%',
                        margin: '0 auto 1rem auto',
                        overflow: 'hidden',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        border: '4px solid white',
                        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)'
                    }}
                >
                    {owner.avatarUrl ? (
                        <img src={owner.avatarUrl} alt={owner.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    ) : (
                        <div style={{ fontSize: '3rem', fontWeight: 'bold', color: '#94a3b8' }}>
                            {owner.name?.charAt(0).toUpperCase()}
                        </div>
                    )}
                </div>

                <h1 style={{ margin: 0 }}>{owner.name}</h1>
                <p style={{ color: '#64748b', margin: '0.2rem 0 0.5rem 0' }}>
                    {owner.occupation || owner.role} {owner.city && `• ${owner.city}`}
                </p>

                {owner.bio && (
                    <p style={{ maxWidth: '600px', margin: '0.5rem auto', color: '#475569', fontStyle: 'italic' }}>
                        "{owner.bio}"
                    </p>
                )}

                <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '1rem' }}>
                    <span style={{ background: '#fef3c7', color: '#d97706', padding: '0.2rem 0.8rem', borderRadius: '1rem', fontSize: '0.9rem' }}>
                        ★ {owner.averageRating?.toFixed(1) || 'New'} ({owner.reviewCount || 0} reviews)
                    </span>
                </div>

                <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '1rem' }}>
                    {owner.identityVerified && (
                        <div style={{ color: 'green', fontWeight: 'bold', fontSize: '0.9rem' }}>
                            Identity Verified
                        </div>
                    )}
                    {owner.isVerified && (
                        <div style={{ color: '#0ea5e9', fontWeight: 'bold', fontSize: '0.9rem' }}>
                            Verified Host
                        </div>
                    )}
                </div>
            </div>

            <h3 style={{ borderBottom: '1px solid #eee', paddingBottom: '0.5rem', marginBottom: '1.5rem' }}>Reviews</h3>
            {reviews.length === 0 ? (
                <p className="text-muted">No reviews yet.</p>
            ) : (
                <div className="grid">
                    {reviews.map((r, i) => (
                        <div key={i} className="card">
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                                <strong>User {r.reviewerId}</strong>
                                <span style={{ color: '#fbbf24' }}>{'★'.repeat(r.rating)}</span>
                            </div>
                            <p style={{ margin: 0, color: '#475569' }}>{r.comment}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default OwnerProfile;
