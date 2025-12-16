import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import MediaUploader from '../components/MediaUploader';

const AddProperty = () => {
    const navigate = useNavigate();
    const [step, setStep] = useState(1); // Step 1: Details, Step 2: Media
    const [propertyId, setPropertyId] = useState(null);
    const [propertyData, setPropertyData] = useState(null); // To store created property for media view

    const [formData, setFormData] = useState({
        title: '',
        location: '',
        price: '',
        rooms: '',
        floor: '',
        description: '',
        distanceToMetro: '',
        distanceToUniversity: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmitDetails = async (e) => {
        e.preventDefault();
        try {
            // Ensure numeric values are numbers
            const payload = {
                ...formData,
                price: parseFloat(formData.price),
                rooms: parseInt(formData.rooms),
                floor: parseInt(formData.floor),
                distanceToMetro: parseFloat(formData.distanceToMetro || 0),
                distanceToUniversity: parseFloat(formData.distanceToUniversity || 0)
            };

            const response = await api.post('/properties', payload);
            setPropertyId(response.data.id);
            setPropertyData(response.data);
            setStep(2); // Move to media upload
            alert('Property details saved! Now you can upload photos and videos.');
        } catch (error) {
            alert('Failed to create property: ' + (error.response?.data || error.message));
        }
    };

    const refreshPropertyMedia = async () => {
        if (!propertyId) return;
        const res = await api.get(`/properties/${propertyId}`);
        setPropertyData(res.data);
    };

    const handleFinish = () => {
        navigate('/my-properties');
    };

    return (
        <div style={{ maxWidth: '800px', margin: '2rem auto' }} className="card fade-in">
            <h1 className="title">{step === 1 ? 'Add New Property' : 'Upload Media'}</h1>

            {/* Step 1: Details Form */}
            {step === 1 && (
                <form onSubmit={handleSubmitDetails}>
                    <div style={{ marginBottom: '1rem' }}>
                        <label>Title</label>
                        <input type="text" name="title" value={formData.title} onChange={handleChange} required placeholder="e.g. Luxury Apartment near BSU" />
                    </div>

                    <div style={{ marginBottom: '1rem' }}>
                        <label>Description</label>
                        <textarea name="description" value={formData.description} onChange={handleChange} required style={{ minHeight: '100px' }} placeholder="Detailed description of the property..." />
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

                    <button type="submit" style={{ width: '100%', marginTop: '1rem' }}>Next: Upload Photos & Videos</button>
                </form>
            )}

            {/* Step 2: Media Upload */}
            {step === 2 && propertyData && (
                <div>
                    <div style={{ marginBottom: '1rem', padding: '1rem', background: '#f0f9ff', borderRadius: '4px' }}>
                        <strong>Creating: {propertyData.title}</strong>
                    </div>

                    <MediaUploader
                        propertyId={propertyId}
                        existingPhotos={propertyData.photos}
                        existingVideos={propertyData.videos}
                        onUpdate={refreshPropertyMedia}
                    />

                    <button onClick={handleFinish} style={{ width: '100%', marginTop: '2rem', background: '#28a745' }}>Finish & Publish</button>
                </div>
            )}
        </div>
    );
};

export default AddProperty;
