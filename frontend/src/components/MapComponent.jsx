import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import { Link } from 'react-router-dom';

// Fix for default marker icon missing in React-Leaflet
import icon from 'leaflet/dist/images/marker-icon.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';

let DefaultIcon = L.icon({
    iconUrl: icon,
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

L.Marker.prototype.options.icon = DefaultIcon;

// Component to handle map clicks for location selection
const LocationPicker = ({ onLocationSelect }) => {
    useMapEvents({
        click(e) {
            onLocationSelect(e.latlng);
        },
    });
    return null;
};

// Component to fly to the center when it changes
const MapUpdater = ({ center }) => {
    const map = useMapEvents({});
    useEffect(() => {
        if (center) {
            map.setView(center, map.getZoom());
        }
    }, [center, map]);
    return null;
};

const MapComponent = ({
    properties = [],
    selectedLocation, // { lat: number, lng: number }
    onLocationSelect,
    readOnly = false,
    height = '400px',
    center: initialCenter // optional override
}) => {
    // Default center (Baku)
    const [center, setCenter] = useState(initialCenter || [40.4093, 49.8671]);

    // Update center if selectedLocation changes
    useEffect(() => {
        if (selectedLocation && selectedLocation.lat && selectedLocation.lng) {
            setCenter([selectedLocation.lat, selectedLocation.lng]);
        } else if (properties.length > 0) {
            const validProperties = properties.filter(p => p.latitude && p.longitude);
            if (validProperties.length > 0) {
                setCenter([validProperties[0].latitude, validProperties[0].longitude]);
            }
        }
    }, [selectedLocation, properties]);

    return (
        <div style={{ height, width: '100%', borderRadius: '0.5rem', overflow: 'hidden', zIndex: 0 }}>
            <MapContainer
                center={center}
                zoom={13}
                scrollWheelZoom={true}
                style={{ height: '100%', width: '100%' }}
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />

                <MapUpdater center={center} />

                {/* Enable clicking to pick location if not read only */}
                {!readOnly && onLocationSelect && (
                    <LocationPicker onLocationSelect={(latlng) => onLocationSelect({ lat: latlng.lat, lng: latlng.lng })} />
                )}

                {/* Show marker for selected location (Add/Edit mode) */}
                {selectedLocation && selectedLocation.lat && selectedLocation.lng && (
                    <Marker position={[selectedLocation.lat, selectedLocation.lng]}>
                        <Popup>Selected Location</Popup>
                    </Marker>
                )}

                {/* Show markers for property list */}
                {properties.map(p => (
                    (p.latitude && p.longitude) ? (
                        <Marker key={p.id} position={[p.latitude, p.longitude]}>
                            <Popup>
                                <div style={{ minWidth: '150px' }}>
                                    <h3 style={{ margin: '0 0 5px 0', fontSize: '14px' }}>{p.title}</h3>
                                    <p style={{ margin: 0, color: '#666' }}>${p.price.toLocaleString()}</p>
                                    <Link to={`/properties/${p.id}`} style={{ display: 'block', marginTop: '5px', color: '#0066cc' }}>View Details</Link>
                                </div>
                            </Popup>
                        </Marker>
                    ) : null
                ))}
            </MapContainer>
        </div>
    );
};

export default MapComponent;
