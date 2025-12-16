import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const Messages = () => {
    const [partners, setPartners] = useState([]);
    const [selectedPartner, setSelectedPartner] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isSearching, setIsSearching] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    const user = JSON.parse(localStorage.getItem('user'));

    useEffect(() => {
        fetchPartners();
    }, []);

    useEffect(() => {
        if (selectedPartner) {
            fetchChat(selectedPartner.id);
        }
    }, [selectedPartner]);


    const fetchPartners = async () => {
        try {
            const response = await api.get('/messages/partners');
            setPartners(response.data);
        } catch (error) {
            console.error('Error fetching partners', error);
        }
    };

    const fetchChat = async (partnerId) => {
        try {
            const response = await api.get(`/messages/chat/${partnerId}`);
            setMessages(response.data);
        } catch (error) {
            console.error('Error fetching chat', error);
        }
    };

    const handleEmailSearch = async () => {
        if (!searchQuery.includes('@')) {
            alert('Please enter a valid email');
            return;
        }
        try {
            const response = await api.get(`/users/find-by-email?email=${searchQuery}`);
            startNewChat(response.data);
        } catch (error) {
            console.error(error);
            const errorMsg = error.response?.data
                ? (typeof error.response.data === 'object' ? JSON.stringify(error.response.data) : error.response.data)
                : 'User not found or connection error.';
            alert(errorMsg);
        }
    };

    const startNewChat = (partner) => {
        // detailed logic to check if partner is already in list
        const existing = partners.find(p => p.id === partner.id);
        if (!existing) {
            setPartners(prev => [partner, ...prev]);
        }
        setSelectedPartner(partner);
        setIsSearching(false);
        setSearchQuery('');
        setSearchResults([]);
    };

    const handleSend = async (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !selectedPartner) return;

        try {
            await api.post('/messages/send', {
                recipientEmail: selectedPartner.email,
                content: newMessage
            });
            setNewMessage('');
            fetchChat(selectedPartner.id); // Refresh chat for self
            // If new partner, ensure they stay in list
            if (!partners.find(p => p.id === selectedPartner.id)) {
                fetchPartners();
            }
        } catch (error) {
            alert('Failed to send: ' + error.message);
        }
    };

    return (
        <div className="fade-in" style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '2rem', height: 'calc(100vh - 120px)' }}>

            {/* Sidebar: Partners List */}
            <div className="card" style={{ overflowY: 'auto', padding: '1rem', display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                    <h3 style={{ margin: 0 }}>Messages</h3>
                    <button onClick={() => setIsSearching(!isSearching)} style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}>
                        {isSearching ? 'Cancel' : '+ New'}
                    </button>
                </div>

                {isSearching && (
                    <form onSubmit={(e) => { e.preventDefault(); handleEmailSearch(); }} style={{ marginBottom: '1rem' }}>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <input
                                type="email"
                                placeholder="Enter user email..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                style={{ flex: 1 }}
                                autoFocus
                                required
                            />
                            <button type="submit">Go</button>
                        </div>
                    </form>
                )}

                <div style={{ flex: 1, overflowY: 'auto' }}>
                    {partners.length === 0 && !isSearching && <p className="text-muted">No conversations.</p>}
                    {partners.map(p => (
                        <div
                            key={p.id}
                            onClick={() => setSelectedPartner(p)}
                            style={{
                                padding: '1rem',
                                cursor: 'pointer',
                                borderRadius: 'var(--radius)',
                                background: selectedPartner?.id === p.id ? 'var(--bg-secondary)' : 'transparent',
                                marginBottom: '0.5rem',
                                border: '1px solid var(--border)'
                            }}
                        >
                            <div style={{ fontWeight: '600' }}>{p.name}</div>
                            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{p.role}</div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Main: Chat Window */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                {!selectedPartner ? (
                    <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                        Select a conversation to start chatting
                    </div>
                ) : (
                    <>
                        <div style={{ paddingBottom: '1rem', borderBottom: '1px solid var(--border)', marginBottom: '1rem' }}>
                            <h3 style={{ margin: 0 }}>{selectedPartner.name}</h3>
                        </div>

                        <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '0.5rem', paddingRight: '0.5rem' }}>
                            {messages.length === 0 && <p className="text-muted text-center">No messages yet.</p>}
                            {messages.map((msg, idx) => {
                                const isMe = msg.senderId === user.id;
                                return (
                                    <div key={idx} style={{
                                        alignSelf: isMe ? 'flex-end' : 'flex-start',
                                        maxWidth: '70%',
                                        background: isMe ? 'var(--primary)' : 'var(--bg-secondary)',
                                        color: isMe ? '#fff' : 'var(--text)',
                                        padding: '0.75rem 1rem',
                                        borderRadius: '1rem',
                                        borderBottomRightRadius: isMe ? '0' : '1rem',
                                        borderBottomLeftRadius: isMe ? '1rem' : '0'
                                    }}>
                                        <div>{msg.text}</div>
                                        <div style={{ fontSize: '0.7rem', opacity: 0.8, marginTop: '0.25rem', textAlign: 'right' }}>
                                            {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        <form onSubmit={handleSend} style={{ display: 'flex', gap: '1rem', marginTop: '1rem', borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
                            <input
                                type="text"
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                placeholder="Type a message..."
                                style={{ flex: 1 }}
                            />
                            <button type="submit">Send</button>
                        </form>
                    </>
                )}
            </div>
        </div>
    );
};

export default Messages;
