import React, { useState } from 'react';
import api from '../api/axiosConfig';

const MediaUploader = ({ propertyId, existingPhotos = [], existingVideos = [], onUpdate }) => {
    const [uploading, setUploading] = useState(false);
    const [dragActivePhoto, setDragActivePhoto] = useState(false);
    const [dragActiveVideo, setDragActiveVideo] = useState(false);
    const [photoPreview, setPhotoPreview] = useState([]);
    const [videoPreview, setVideoPreview] = useState([]);

    // --- Upload Handlers ---

    const uploadFiles = async (files, type) => {
        if (files.length === 0) return;

        const formData = new FormData();
        Array.from(files).forEach(file => {
            formData.append('file', file);
        });

        // Preview logic
        if (type === 'photos') {
            const newPreviews = Array.from(files).map(f => URL.createObjectURL(f));
            setPhotoPreview(prev => [...prev, ...newPreviews]);
        } else {
            const newPreviews = Array.from(files).map(f => f.name); // Just names for video
            setVideoPreview(prev => [...prev, ...newPreviews]);
        }

        setUploading(true);
        try {
            // Upload sequentially or parallel? The backend takes individual requests in my previous impl?
            // Wait, previous AddProperty used "file" param (singular). 
            // The endpoint expects "file" param. I should loop. 
            // OR did I update it to accept multiple? 
            // Checking Controller: addPhoto takes @RequestParam("file") MultipartFile. Singular.
            // So on Frontend I must loop.

            const endpoint = type === 'photos'
                ? `/properties/${propertyId}/photos`
                : `/properties/${propertyId}/videos`;

            // Loop through all files
            for (let i = 0; i < files.length; i++) {
                const singleData = new FormData();
                singleData.append('file', files[i]);
                await api.post(endpoint, singleData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
            }

            onUpdate(); // Trigger refresh to get server URLs
            setPhotoPreview([]);
            setVideoPreview([]);
        } catch (error) {
            alert(`${type} upload failed: ` + (error.response?.data || error.message));
        } finally {
            setUploading(false);
        }
    };

    // --- Drag & Drop Handlers ---

    const handleDrag = (e, setDrag) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === "dragenter" || e.type === "dragover") {
            setDrag(true);
        } else if (e.type === "dragleave") {
            setDrag(false);
        }
    };

    const handleDrop = (e, type, setDrag) => {
        e.preventDefault();
        e.stopPropagation();
        setDrag(false);
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
            uploadFiles(e.dataTransfer.files, type);
        }
    };

    const handleChange = (e, type) => {
        e.preventDefault();
        if (e.target.files && e.target.files.length > 0) {
            uploadFiles(e.target.files, type);
        }
    };

    // --- Delete Handlers ---

    const handleDeletePhoto = async (index) => {
        if (!confirm("Delete this photo?")) return;
        try {
            await api.delete(`/properties/${propertyId}/photos/${index}`);
            onUpdate();
        } catch (error) {
            alert("Delete failed");
        }
    };

    const handleDeleteVideo = async (index) => {
        if (!confirm("Delete this video?")) return;
        try {
            await api.delete(`/properties/${propertyId}/videos/${index}`);
            onUpdate();
        } catch (error) {
            alert("Delete failed");
        }
    };

    return (
        <div className="media-uploader" style={{ marginTop: '2rem' }}>
            {uploading && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    background: 'rgba(255,255,255,0.8)', zIndex: 999,
                    display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column'
                }}>
                    <div className="spinner"></div>
                    <h3>Uploading Media... Please Wait</h3>
                </div>
            )}

            {/* PHOTOS SECTION */}
            <div style={{ marginBottom: '2rem' }}>
                <h3 style={{ borderBottom: '1px solid #ddd', paddingBottom: '0.5rem' }}>Photos</h3>

                {/* Existing Photos Grid */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
                    {existingPhotos.map((photo, index) => (
                        <div key={index} style={{ position: 'relative', aspectRatio: '4/3' }}>
                            <img src={photo} alt="Property" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '8px', border: '1px solid #ddd' }} />
                            <button
                                onClick={() => handleDeletePhoto(index)}
                                style={{
                                    position: 'absolute', top: -5, right: -5,
                                    background: '#ff4444', color: 'white',
                                    border: 'none', borderRadius: '50%',
                                    width: '24px', height: '24px', cursor: 'pointer',
                                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                                }}
                            >×</button>
                        </div>
                    ))}
                    {/* Previews */}
                    {photoPreview.map((src, i) => (
                        <div key={'prev' + i} style={{ position: 'relative', aspectRatio: '4/3', opacity: 0.5 }}>
                            <img src={src} alt="Preview" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '8px' }} />
                        </div>
                    ))}
                </div>

                {/* Drag Drop Zone */}
                <div
                    onDragEnter={(e) => handleDrag(e, setDragActivePhoto)}
                    onDragLeave={(e) => handleDrag(e, setDragActivePhoto)}
                    onDragOver={(e) => handleDrag(e, setDragActivePhoto)}
                    onDrop={(e) => handleDrop(e, 'photos', setDragActivePhoto)}
                    style={{
                        border: `2px dashed ${dragActivePhoto ? '#2196f3' : '#ccc'}`,
                        borderRadius: '12px',
                        padding: '2rem',
                        textAlign: 'center',
                        background: dragActivePhoto ? '#e3f2fd' : '#f8f9fa',
                        transition: 'all 0.2s ease',
                        cursor: 'pointer'
                    }}
                >
                    <p style={{ margin: '0 0 1rem 0', color: '#666' }}>Drag & drop photos here, or click to select</p>
                    <input
                        type="file"
                        multiple
                        accept="image/*"
                        onChange={(e) => handleChange(e, 'photos')}
                        style={{ display: 'none' }}
                        id="photo-upload"
                    />
                    <label htmlFor="photo-upload" style={{
                        padding: '0.5rem 1rem',
                        background: '#2196f3',
                        color: 'white',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        display: 'inline-block'
                    }}>Select Photos</label>
                </div>
            </div>

            {/* VIDEOS SECTION */}
            <div style={{ marginBottom: '2rem' }}>
                <h3 style={{ borderBottom: '1px solid #ddd', paddingBottom: '0.5rem' }}>Videos</h3>

                {/* Existing Videos Grid */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
                    {existingVideos.map((video, index) => (
                        <div key={index} style={{ position: 'relative', aspectRatio: '16/9' }}>
                            <video src={video} style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '8px', border: '1px solid #ddd' }} controls />
                            <button
                                onClick={() => handleDeleteVideo(index)}
                                style={{
                                    position: 'absolute', top: -5, right: -5,
                                    background: '#ff4444', color: 'white',
                                    border: 'none', borderRadius: '50%',
                                    width: '24px', height: '24px', cursor: 'pointer',
                                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                                    zIndex: 10
                                }}
                            >×</button>
                        </div>
                    ))}
                </div>

                {/* Drag Drop Zone */}
                <div
                    onDragEnter={(e) => handleDrag(e, setDragActiveVideo)}
                    onDragLeave={(e) => handleDrag(e, setDragActiveVideo)}
                    onDragOver={(e) => handleDrag(e, setDragActiveVideo)}
                    onDrop={(e) => handleDrop(e, 'videos', setDragActiveVideo)}
                    style={{
                        border: `2px dashed ${dragActiveVideo ? '#2196f3' : '#ccc'}`,
                        borderRadius: '12px',
                        padding: '2rem',
                        textAlign: 'center',
                        background: dragActiveVideo ? '#e3f2fd' : '#f8f9fa',
                        transition: 'all 0.2s ease',
                        cursor: 'pointer'
                    }}
                >
                    <p style={{ margin: '0 0 1rem 0', color: '#666' }}>Drag & drop videos here (Max 100MB), or click to select</p>
                    <input
                        type="file"
                        multiple
                        accept="video/*"
                        onChange={(e) => handleChange(e, 'videos')}
                        style={{ display: 'none' }}
                        id="video-upload"
                    />
                    <label htmlFor="video-upload" style={{
                        padding: '0.5rem 1rem',
                        background: '#2196f3',
                        color: 'white',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        display: 'inline-block'
                    }}>Select Videos</label>
                </div>
            </div>
        </div>
    );
};

export default MediaUploader;
