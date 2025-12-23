import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';
import MediaUploader from '../components/MediaUploader';
import MapComponent from '../components/MapComponent';

const EditProperty = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [step, setStep] = useState(1); // Step 1: Details, Step 2: Media
    const [propertyData, setPropertyData] = useState(null);
    const [loading, setLoading] = useState(true);

    const [formData, setFormData] = useState({
        title: '',
        location: '',
        price: '',
        rooms: '',
        floor: '',
        description: '',
        distanceToMetro: '',
        distanceToUniversity: '',
        area: '',
        latitude: '',
        longitude: ''
    });

    useEffect(() => {
        const fetchProperty = async () => {
            try {
                const res = await api.get(`/properties/${id}`);
                const p = res.data;
                setPropertyData(p);
                setFormData({
                    title: p.title || '',
                    location: p.location || '',
                    price: p.price || '',
                    rooms: p.rooms || '',
                    floor: p.floor || '',
                    description: p.description || '',
                    distanceToMetro: p.distanceToMetro || '',
                    distanceToUniversity: p.distanceToUniversity || '',
                    area: p.area || '',
                    latitude: p.latitude || '',
                    longitude: p.longitude || ''
                });
            } catch (error) {
                alert("Failed to load property: " + (error.response?.data || error.message));
                navigate('/my-properties');
            } finally {
                setLoading(false);
            }
        };
        fetchProperty();
    }, [id, navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleLocationSelect = (loc) => {
        setFormData(prev => ({
            ...prev,
            latitude: loc.lat,
            longitude: loc.lng
        }));
    };

    const handleSubmitDetails = async (e) => {
        e.preventDefault();
        try {
            // Ensure numeric values are numbers
            const payload = {
                ...propertyData, // Keep existing fields like photos/videos/ownerId
                ...formData,
                price: parseFloat(formData.price),
                rooms: parseInt(formData.rooms),
                floor: parseInt(formData.floor),
                area: parseFloat(formData.area || 0),
                distanceToMetro: parseFloat(formData.distanceToMetro || 0),
                distanceToUniversity: parseFloat(formData.distanceToUniversity || 0),
                latitude: parseFloat(formData.latitude || 0),
                longitude: parseFloat(formData.longitude || 0)
            };

            const response = await api.put(`/properties/${id}`, payload);
            setPropertyData(response.data);
            setStep(2); // Move to media upload
        } catch (error) {
            alert('Failed to update property: ' + (error.response?.data || error.message));
        }
    };

    const refreshPropertyMedia = async () => {
        const res = await api.get(`/properties/${id}`);
        setPropertyData(res.data);
    };

    const handleFinish = () => {
        navigate('/my-properties');
    };

    if (loading) return <div className="text-center mt-5">Loading...</div>;

    return (
        <div style={{ maxWidth: '800px', margin: '2rem auto' }} className="card fade-in">
            <h1 className="title">{step === 1 ? 'Edit Property Details' : 'Manage Media'}</h1>

            {/* Step 1: Details Form */}
            {step === 1 && (
                <form onSubmit={handleSubmitDetails}>
                    <div style={{ marginBottom: '1rem' }}>
                        <label>Title</label>
                        <input type="text" name="title" value={formData.title} onChange={handleChange} required />
                    </div>

                    <div style={{ marginBottom: '1rem' }}>
                        <label>Description</label>
                        <textarea name="description" value={formData.description} onChange={handleChange} required style={{ minHeight: '100px' }} />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Price (Nightly)</label>
                            <input type="number" name="price" value={formData.price} onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Location</label>
                            <input type="text" name="location" value={formData.location} onChange={handleChange} required />
                        </div>
                    </div>

                    <div style={{ marginBottom: '1rem' }}>
                        <label>Pin Location on Map (Click to update)</label>
                        <div style={{ height: '350px', border: '1px solid #ddd', borderRadius: '4px' }}>
                            <MapComponent
                                selectedLocation={formData.latitude && formData.longitude ? { lat: parseFloat(formData.latitude), lng: parseFloat(formData.longitude) } : null}
                                onLocationSelect={handleLocationSelect}
                                height="100%"
                            />
                        </div>
                        {formData.latitude && (
                            <div style={{ fontSize: '0.8rem', color: '#666', marginTop: '5px' }}>
                                Selected: {parseFloat(formData.latitude).toFixed(4)}, {parseFloat(formData.longitude).toFixed(4)}
                            </div>
                        )}
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Rooms</label>
                            <input type="number" name="rooms" value={formData.rooms} onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Floor</label>
                            <input type="number" name="floor" value={formData.floor} onChange={handleChange} required />
                        </div>
                    </div>

                    <div style={{ marginBottom: '1rem' }}>
                        <label>Area (m²)</label>
                        <input type="number" step="0.1" name="area" value={formData.area} onChange={handleChange} />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Distance to Metro (km)</label>
                            <input type="number" step="0.1" name="distanceToMetro" value={formData.distanceToMetro} onChange={handleChange} />
                        </div>
                        <div style={{ marginBottom: '1rem' }}>
                            <label>Distance to University (km)</label>
                            <input type="number" step="0.1" name="distanceToUniversity" value={formData.distanceToUniversity} onChange={handleChange} />
                        </div>
                    </div>

                    <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                        <button type="submit" style={{ flex: 1 }}>Next: Manage Photos & Videos</button>
                    </div>
                </form>
            )}

            {/* Step 2: Media Upload */}
            {step === 2 && propertyData && (
                <div>
                    <div style={{ marginBottom: '1rem', padding: '1rem', background: '#f0f9ff', borderRadius: '4px' }}>
                        <strong>Editing: {propertyData.title}</strong>
                    </div>

                    <MediaUploader
                        propertyId={id}
                        existingPhotos={propertyData.photos}
                        existingVideos={propertyData.videos}
                        onUpdate={refreshPropertyMedia}
                    />

                    <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
                        <button onClick={() => setStep(1)} style={{ background: '#6c757d', flex: 1 }}>Back to Details</button>
                        <button onClick={handleFinish} style={{ background: '#28a745', flex: 1 }}>Finish Editing</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default EditProperty;
