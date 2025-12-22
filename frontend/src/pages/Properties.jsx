import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

const Properties = () => {
    const [properties, setProperties] = useState([]);
    const [filters, setFilters] = useState({
        search: '',
        minPrice: '',
        maxPrice: '',
        rooms: '',
        floor: '',
        minArea: '',
        maxArea: '',
        maxDistanceToMetro: '',
        maxDistanceToUniversity: ''
    });

    useEffect(() => {
        fetchProperties();
    }, []);

    const fetchProperties = async () => {
        try {
            const params = new URLSearchParams();
            if (filters.search) params.append('search', filters.search);
            if (filters.minPrice) params.append('minPrice', filters.minPrice);
            if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
            if (filters.rooms) params.append('rooms', filters.rooms);
            if (filters.floor) params.append('floor', filters.floor);
            if (filters.minArea) params.append('minArea', filters.minArea);
            if (filters.maxArea) params.append('maxArea', filters.maxArea);
            if (filters.maxDistanceToMetro) params.append('maxDistanceToMetro', filters.maxDistanceToMetro);
            if (filters.maxDistanceToUniversity) params.append('maxDistanceToUniversity', filters.maxDistanceToUniversity);

            // Use the search endpoint if any filter is active, otherwise default listing
            const endpoint = params.toString() ? `/properties/search?${params.toString()}` : '/properties';
            const response = await api.get(endpoint);
            setProperties(response.data);
        } catch (error) {
            console.error('Error fetching properties', error);
        }
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleSearch = (e) => {
        e.preventDefault();
        fetchProperties();
    };

    return (
        <div className="animate-fade-in">
            {/* Hero Section */}
            <div className="hero">
                <div className="hero-content">
                    <h1>Find Your Dream Stay</h1>
                    <p>Discover luxury villas, cozy apartments, and verified properties.</p>
                </div>
            </div>

            <div style={{ marginBottom: '2rem', position: 'relative', zIndex: 10, padding: '0 1rem' }}>
                <h2 className="title" style={{ display: 'none' }}>Discover Properties</h2> {/* Hidden title since we have Hero */}

                {/* Search Bar */}
                <form onSubmit={handleSearch} className="card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '1rem', alignItems: 'end', maxWidth: '1000px', margin: '0 auto', border: 'none', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)' }}>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Location / Title</label>
                        <input
                            type="text"
                            name="search"
                            value={filters.search}
                            onChange={handleFilterChange}
                            placeholder="City, Street..."
                            style={{ width: '100%' }}
                        />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Min Price</label>
                        <input
                            type="number"
                            name="minPrice"
                            value={filters.minPrice}
                            onChange={handleFilterChange}
                            placeholder="0"
                            style={{ width: '100%' }}
                        />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Max Price</label>
                        <input
                            type="number"
                            name="maxPrice"
                            value={filters.maxPrice}
                            onChange={handleFilterChange}
                            placeholder="No limit"
                            style={{ width: '100%' }}
                        />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Rooms</label>
                        <input
                            type="number"
                            name="rooms"
                            value={filters.rooms}
                            onChange={handleFilterChange}
                            placeholder="Any"
                            style={{ width: '100%' }}
                        />
                    </div>
                    <button type="submit" style={{ height: '42px', marginTop: 'auto' }}>Search</button>


                    {/* Advanced Filters */}
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Floor</label>
                        <input type="number" name="floor" value={filters.floor} onChange={handleFilterChange} placeholder="Any" style={{ width: '100%' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Min Area (m²)</label>
                        <input type="number" name="minArea" value={filters.minArea} onChange={handleFilterChange} placeholder="0" style={{ width: '100%' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Max Area (m²)</label>
                        <input type="number" name="maxArea" value={filters.maxArea} onChange={handleFilterChange} placeholder="Any" style={{ width: '100%' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Max Dist. Metro (km)</label>
                        <input type="number" name="maxDistanceToMetro" value={filters.maxDistanceToMetro} onChange={handleFilterChange} placeholder="Any" style={{ width: '100%' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Max Dist. Uni (km)</label>
                        <input type="number" name="maxDistanceToUniversity" value={filters.maxDistanceToUniversity} onChange={handleFilterChange} placeholder="Any" style={{ width: '100%' }} />
                    </div>
                </form>
            </div >

            <div style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>{properties.length} results found</div>

            <div className="grid">
                {properties.map(p => (
                    <Link to={`/properties/${p.id}`} key={p.id} className="card" style={{ padding: 0, overflow: 'hidden', display: 'flex', flexDirection: 'column', transition: 'transform 0.2s' }}>

                        {/* Image Placeholder or Actual Image */}
                        <div style={{ height: '200px', backgroundColor: '#E2E8F0', backgroundSize: 'cover', backgroundPosition: 'center', backgroundImage: p.photos && p.photos.length > 0 ? `url(${p.photos[0]})` : 'none', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#94A3B8' }}>
                            {!p.photos || p.photos.length === 0 ? 'No Image' : ''}
                        </div>

                        <div style={{ padding: '1.5rem', flex: 1, display: 'flex', flexDirection: 'column' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '0.5rem' }}>
                                <h3 style={{ fontSize: '1.25rem', marginBottom: 0 }}>{p.title}</h3>
                                <span style={{ backgroundColor: '#EEF2FF', color: 'var(--primary)', padding: '0.25rem 0.75rem', borderRadius: '1rem', fontSize: '0.875rem', fontWeight: '600' }}>
                                    ${p.price.toLocaleString()}
                                </span>
                            </div>

                            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '1rem', flex: 1 }}>
                                {p.location}
                            </p>

                            <div style={{ display: 'flex', gap: '1rem', fontSize: '0.875rem', color: 'var(--text-muted)', borderTop: '1px solid #F1F5F9', paddingTop: '1rem' }}>
                                <span>🛏 {p.rooms} Beds</span>
                                <span>🏢 Floor {p.floor}</span>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>
            {
                properties.length === 0 && (
                    <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
                        <h3>No properties match your filters.</h3>
                        <p>Try adjusting your search criteria.</p>
                    </div>
                )
            }
        </div >
    );
};

export default Properties;
