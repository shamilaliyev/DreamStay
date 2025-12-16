import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Properties from './pages/Properties';
import PropertyDetails from './pages/PropertyDetails';
import AdminDashboard from './pages/AdminDashboard';
import AdminUserDetails from './pages/AdminUserDetails';
import AdminPropertyDetails from './pages/AdminPropertyDetails';
import OwnerProfile from './pages/OwnerProfile';
import UploadId from './pages/UploadId';
import MyProfile from './pages/MyProfile';
import MyProperties from './pages/MyProperties';
import AddProperty from './pages/AddProperty';
import EditProperty from './pages/EditProperty';
import Messages from './pages/Messages';

function App() {
  // Initialize state directly from localStorage to avoid initial null render
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('user');
      return saved ? JSON.parse(saved) : null;
    } catch (e) {
      console.error("Failed to parse user from storage", e);
      localStorage.removeItem('user');
      return null;
    }
  });

  useEffect(() => {
    // Listen for storage events (login/logout sync)
    const handleStorageChange = () => {
      try {
        const u = localStorage.getItem('user');
        setUser(u ? JSON.parse(u) : null);
      } catch (e) {
        setUser(null);
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    window.location.href = '/login';
  };

  return (
    <Router>
      <div className="app-min-h-screen">
        <nav className="nav">
          <div className="nav-content">
            <Link to="/" className="nav-logo">
              DreamStay
            </Link>
            <div className="nav-links">
              <Link to="/properties">Explore Listings</Link>

              {!user && (
                <>
                  <Link to="/login">Login</Link>
                  <Link to="/register" className="btn btn-primary" style={{ padding: '0.5rem 1rem', background: 'var(--accent)', color: 'white', border: 'none' }}>Register</Link>
                </>
              )}

              {user && (
                <>
                  {user.role === 'admin' && <Link to="/admin" className="badge-info" style={{ background: 'rgba(255,255,255,0.2)', color: 'white' }}>Admin Panel</Link>}
                  {(user.role === 'seller' || user.role === 'agent') && <Link to="/my-properties">My Listings</Link>}

                  {/* Verification Link */}
                  {((user.role === 'seller' || user.role === 'agent') && !user.idVerified) && (
                    <Link to="/verify-identity" style={{ color: '#FCD34D' }}>Verify Identity ⚠️</Link>
                  )}

                  <Link to="/messages">Messages</Link>
                  <Link to="/my-profile" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <div style={{ width: '32px', height: '32px', borderRadius: '50%', background: '#334155', display: 'flex', justifyContent: 'center', alignItems: 'center', color: 'white', fontSize: '0.8rem' }}>
                      {user.name.charAt(0).toUpperCase()}
                    </div>
                  </Link>
                  <button onClick={handleLogout} className="btn-danger" style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>
                    Logout
                  </button>
                </>
              )}
            </div>
          </div>
        </nav>
        <div className="container animate-fade-in">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/properties" element={<Properties />} />
            <Route path="/properties/:id" element={<PropertyDetails />} />

            {/* Protected Routes */}
            <Route path="/admin" element={user?.role === 'admin' ? <AdminDashboard /> : <Navigate to="/login" />} />
            <Route path="/admin/users/:id" element={user?.role === 'admin' ? <AdminUserDetails /> : <Navigate to="/login" />} />
            <Route path="/admin/properties/:id" element={user?.role === 'admin' ? <AdminPropertyDetails /> : <Navigate to="/login" />} />
            <Route path="/my-properties" element={<MyProperties />} />
            <Route path="/add-property" element={<AddProperty />} />
            <Route path="/edit-property/:id" element={<EditProperty />} />

            {/* Public Profile */}
            <Route path="/users/:id/profile" element={<OwnerProfile />} />

            {/* Verification */}
            <Route path="/verify-identity" element={<UploadId />} />

            <Route path="/my-profile" element={user ? <MyProfile /> : <Navigate to="/login" />} />
            <Route path="/messages" element={user ? <Messages /> : <Navigate to="/login" />} />

            <Route path="/" element={<Properties />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
