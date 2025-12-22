import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axiosConfig';

const PropertyDetails = () => {
    const { id } = useParams();
    const [property, setProperty] = useState(null);
    const [message, setMessage] = useState('');
    const [review, setReview] = useState({ rating: 5, comment: '' });
    const user = JSON.parse(localStorage.getItem('user'));

    const [reviews, setReviews] = useState([]);

    // Report state
    const [isReporting, setIsReporting] = useState(false);
    const [reportData, setReportData] = useState({ reason: '', description: '' });

    const handleReport = async (e) => {
        e.preventDefault();
        try {
            await api.post('/reports', {
                reportedPropertyId: property.id,
                reason: reportData.reason,
                description: reportData.description
            });
            alert('Property reported successfully. Admins will review this.');
            setIsReporting(false);
            setReportData({ reason: '', description: '' });
        } catch (error) {
            alert('Failed to report: ' + (error.response?.data?.message || error.message));
        }
    };

    useEffect(() => {
        const fetchProperty = async () => {
            try {
                const response = await api.get(`/properties/${id}`);
                setProperty(response.data);
                // Fetch reviews for the owner
                if (response.data.ownerId) {
                    const reviewRes = await api.get(`/reviews/${response.data.ownerId}`);
                    setReviews(reviewRes.data);
                }
            } catch (error) {
                console.error('Error fetching property value', error);
            }
        };
        fetchProperty();
    }, [id]);

    const handleSendMessage = async (e) => {
        e.preventDefault();
        console.log('Sending message:', { recipientId: property.ownerId, content: message });

        if (!property.ownerId) {
            alert('Error: Property owner ID is missing.');
            return;
        }

        try {
            await api.post('/messages', {
                recipientId: property.ownerId,
                content: message
            }); // Header handled by interceptor
            alert('Message sent!');
            setMessage('');
        } catch (error) {
            console.error('Send failed:', error);
            alert('Failed to send message: ' + (typeof error.response?.data === 'string' ? error.response.data : JSON.stringify(error.response?.data)) || error.message);
        }
    };

    const handleReview = async (e) => {
        e.preventDefault();
        try {
            await api.post('/reviews', {
                targetId: property.ownerId,
                rating: review.rating,
                comment: review.comment
            }, {
                headers: { 'X-User-Id': user.id }
            });
            alert('Review submitted!');
            setReview({ rating: 5, comment: '' });
            // Refresh reviews
            const reviewRes = await api.get(`/reviews/${property.ownerId}`);
            setReviews(reviewRes.data);
        } catch (error) {
            alert('Failed to submit review: ' + (error.response?.data || error.message));
        }
    };

    const handlePayment = async () => {
        if (!confirm(`Confirm payment of $${property.price}?`)) return;
        try {
            await api.post('/payments/mock', {
                propertyId: property.id,
                amount: property.price
            }, {
                headers: { 'X-User-Id': user.id }
            });
            alert('Payment Successful! (Mock Transaction)');
        } catch (error) {
            alert('Payment failed: ' + (error.response?.data || error.message));
        }
    };

    if (!property) return <div className="container" style={{ textAlign: 'center', marginTop: '4rem' }}>Loading...</div>;

    return (
        <div className="animate-fade-in">
            {/* Header / Hero */}
            <div className="card" style={{ marginBottom: '2rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                    <h1 style={{ fontSize: '2rem', margin: 0 }}>{property.title}</h1>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <span style={{ fontSize: '1.5rem', fontWeight: '700', color: 'var(--primary)' }}>${property.price.toLocaleString()}</span>
                        {user && user.role === 'buyer' && (
                            <button onClick={handlePayment} style={{ padding: '0.5rem 1rem', background: '#28a745', border: 'none' }}>Pay Now</button>
                        )}
                    </div>
                </div>
                <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>📍 {property.location}</p>

                {/* Gallery (Photos & Videos) */}
                <div style={{ display: 'flex', gap: '1rem', overflowX: 'auto', margin: '2rem 0', paddingBottom: '1rem' }}>
                    {/* Photos */}
                    {property.photos && property.photos.map((photo, index) => (
                        <img key={`p-${index}`} src={photo} alt="Property" style={{ height: '300px', borderRadius: 'var(--radius)', boxShadow: 'var(--shadow)' }} />
                    ))}
                    {/* Videos */}
                    {property.videos && property.videos.map((video, index) => (
                        <video key={`v-${index}`} src={video} controls style={{ height: '300px', borderRadius: 'var(--radius)', boxShadow: 'var(--shadow)' }} />
                    ))}

                    {(!property.photos?.length && !property.videos?.length) && (
                        <div style={{ height: '300px', width: '100%', background: '#F1F5F9', borderRadius: 'var(--radius)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#94A3B8' }}>
                            No Media Available
                        </div>
                    )}
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
                    <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: 'var(--radius)', textAlign: 'center' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Area</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: '600' }}>{property.area ? `${property.area} m²` : 'N/A'}</div>
                    </div>
                    <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: 'var(--radius)', textAlign: 'center' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Bedrooms</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: '600' }}>{property.rooms}</div>
                    </div>
                    <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: 'var(--radius)', textAlign: 'center' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Floor</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: '600' }}>{property.floor}</div>
                    </div>
                    <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: 'var(--radius)', textAlign: 'center' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>To Metro</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: '600' }}>{property.distanceToMetro} km</div>
                    </div>
                    <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: 'var(--radius)', textAlign: 'center' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>To Uni</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: '600' }}>{property.distanceToUniversity} km</div>
                    </div>
                </div>

                <h3>Description</h3>
                <h3 style={{ lineHeight: '1.6', color: '#334155', whiteSpace: 'pre-wrap' }}>{property.description}</h3>

                {/* Report Property Button */}
                <div style={{ marginTop: '1rem', textAlign: 'right' }}>
                    <button
                        onClick={() => setIsReporting(true)}
                        style={{ background: 'none', border: 'none', color: '#ef4444', fontSize: '0.85rem', cursor: 'pointer', textDecoration: 'underline' }}
                    >
                        Report this Listing
                    </button>
                </div>
            </div>

            {/* Report Modal */}
            {isReporting && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                    <div className="card" style={{ width: '100%', maxWidth: '400px' }}>
                        <h3>Report Property</h3>
                        <form onSubmit={handleReport}>
                            <label style={{ display: 'block', marginBottom: '0.5rem' }}>Reason</label>
                            <select
                                value={reportData.reason}
                                onChange={e => setReportData({ ...reportData, reason: e.target.value })}
                                style={{ width: '100%', marginBottom: '1rem' }}
                                required
                            >
                                <option value="">Select a reason</option>
                                <option value="fake">Fake Listing</option>
                                <option value="inaccurate">Inaccurate Description</option>
                                <option value="spam">Spam / Scam</option>
                                <option value="offensive">Offensive Content</option>
                                <option value="other">Other</option>
                            </select>

                            <label style={{ display: 'block', marginBottom: '0.5rem' }}>Description</label>
                            <textarea
                                value={reportData.description}
                                onChange={e => setReportData({ ...reportData, description: e.target.value })}
                                style={{ width: '100%', minHeight: '80px', marginBottom: '1rem' }}
                                required
                            />

                            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
                                <button type="button" onClick={() => setIsReporting(false)} style={{ background: '#ef4444' }}>Cancel</button>
                                <button type="submit">Submit Report</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Owner Section */}
            <div className="card" style={{ marginBottom: '2rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #E2E8F0', paddingBottom: '1rem', marginBottom: '1rem' }}>
                    <h3>Listed By</h3>
                    <button onClick={() => window.location.href = `/users/${property.ownerId}/profile`} style={{ background: 'none', color: 'var(--primary)', border: 'none', padding: 0 }}>View Profile →</button>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                    <div style={{ width: '60px', height: '60px', borderRadius: '50%', background: '#2196f3', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 'bold' }}>
                        {property.ownerName?.charAt(0).toUpperCase()}
                    </div>
                    <div>
                        <div style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>{property.ownerName}</div>
                        <div className="text-muted" style={{ textTransform: 'capitalize' }}>{property.ownerRole}</div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.25rem' }}>
                            <span style={{ color: '#fbbf24' }}>⭐ {property.ownerRating?.toFixed(1)}</span>
                            <span className="text-muted">({property.ownerReviewCount} reviews)</span>
                        </div>
                        {property.isOwnerVerified && <div style={{ color: 'green', fontSize: '0.85rem', marginTop: '0.25rem' }}>✅ Verified Identity</div>}
                    </div>
                </div>

                <div style={{ marginTop: '1.5rem' }}>
                    {user ? (
                        <button
                            onClick={async () => {
                                // Simple navigation or modal to contact form below
                                document.getElementById('contact-form').scrollIntoView({ behavior: 'smooth' });
                            }}
                            style={{ width: '100%', padding: '0.8rem' }}
                        >
                            Contact Owner
                        </button>
                    ) : (
                        <p className="text-center text-muted"><a href="/login">Log in</a> to contact owner</p>
                    )}
                </div>
            </div>

            {/* Reviews Section */}
            <div className="card" style={{ marginBottom: '2rem' }}>
                <h3 style={{ borderBottom: '1px solid #E2E8F0', paddingBottom: '1rem', marginBottom: '1.5rem' }}>Owner Reviews</h3>
                {reviews.length === 0 ? (
                    <p className="text-muted">No reviews yet for this owner.</p>
                ) : (
                    <div className="grid">
                        {reviews.slice(0, 3).map((r, i) => ( // Show only top 3 here
                            <div key={i} style={{ background: '#f8fafc', padding: '1rem', borderRadius: 'var(--radius)' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                                    <strong>User {r.reviewerId}</strong>
                                    <span style={{ color: '#fbbf24' }}>{'⭐'.repeat(r.rating)}</span>
                                </div>
                                <p style={{ margin: 0, color: '#475569' }}>{r.comment}</p>
                            </div>
                        ))}
                    </div>
                )}
                {reviews.length > 3 && (
                    <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                        <button onClick={() => window.location.href = `/users/${property.ownerId}/profile`} style={{ background: 'none', color: 'var(--primary)', border: 'none' }}>View All Reviews</button>
                    </div>
                )}
            </div>

            <div className="grid">
                {/* Contact Form */}
                <div id="contact-form" className="card">
                    <h3 style={{ borderBottom: '1px solid #E2E8F0', paddingBottom: '1rem', marginBottom: '1.5rem' }}>Contact Owner</h3>
                    {user ? (
                        <form onSubmit={handleSendMessage}>
                            <textarea
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                placeholder={`Hi ${property.ownerName}, I'm interested in ${property.title}...`}
                                required
                                style={{ minHeight: '120px', resize: 'vertical' }}
                            />
                            <button type="submit" style={{ width: '100%' }}>Send Message</button>
                        </form>
                    ) : (
                        <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-muted)' }}>
                            <a href="/login" style={{ color: 'var(--primary)', fontWeight: '600' }}>Log in</a> to send a message.
                        </div>
                    )}
                </div>

                {/* Review Form */}
                <div className="card">
                    <h3 style={{ borderBottom: '1px solid #E2E8F0', paddingBottom: '1rem', marginBottom: '1.5rem' }}>Rate Experience</h3>
                    <p className="text-muted text-sm" style={{ marginBottom: '1rem' }}>
                        You can only rate owners you have contacted properly.
                    </p>
                    {user ? (
                        <form onSubmit={handleReview}>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Rating</label>
                            <select
                                value={review.rating}
                                onChange={(e) => setReview({ ...review, rating: e.target.value })}
                                style={{ marginBottom: '1rem' }}
                            >
                                <option value="5">⭐⭐⭐⭐⭐ (Excellent)</option>
                                <option value="4">⭐⭐⭐⭐ (Good)</option>
                                <option value="3">⭐⭐⭐ (Average)</option>
                                <option value="2">⭐⭐ (Poor)</option>
                                <option value="1">⭐ (Terrible)</option>
                            </select>

                            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Comment</label>
                            <textarea
                                value={review.comment}
                                onChange={(e) => setReview({ ...review, comment: e.target.value })}
                                placeholder="Share your experience working with this owner..."
                                required
                                style={{ minHeight: '120px', resize: 'vertical' }}
                            />
                            <button type="submit" style={{ width: '100%', background: '#fff', color: 'var(--primary)', border: '1px solid var(--primary)' }}>Submit Review</button>
                        </form>
                    ) : (
                        <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-muted)' }}>
                            <a href="/login" style={{ color: 'var(--primary)', fontWeight: '600' }}>Log in</a> to leave a review.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PropertyDetails;
