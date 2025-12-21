import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const Messages = () => {
    const [partners, setPartners] = useState([]);
    const [selectedPartner, setSelectedPartner] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isSearching, setIsSearching] = useState(false); // For "Find User Mode"
    const [emailSearchQuery, setEmailSearchQuery] = useState(''); // For finding user by email
    const [searchQuery, setSearchQuery] = useState(''); // For message content search
    const [searchResults, setSearchResults] = useState([]);
    const [isBlocked, setIsBlocked] = useState(false);

    const user = JSON.parse(localStorage.getItem('user'));

    useEffect(() => {
        fetchPartners();
    }, []);

    useEffect(() => {
        if (selectedPartner) {
            fetchChat(selectedPartner.id);
            checkBlockStatus(selectedPartner.id);
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

    const checkBlockStatus = async (partnerId) => {
        try {
            const response = await api.get(`/messages/block/${partnerId}`);
            setIsBlocked(response.data);
        } catch (error) {
            console.error('Error checking block status', error);
        }
    };

    const handleEmailSearch = async () => {
        if (!emailSearchQuery.includes('@')) {
            alert('Please enter a valid email');
            return;
        }
        try {
            const response = await api.get(`/users/find-by-email?email=${emailSearchQuery}`);
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
        setEmailSearchQuery('');
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

    // --- New Feature Handlers ---

    const handleMessageSearch = async (query) => {
        setSearchQuery(query);
        if (!query.trim()) {
            setSearchResults([]);
            return;
        }
        try {
            const response = await api.get(`/messages/search?q=${query}`);
            setSearchResults(response.data);
        } catch (error) {
            console.error('Search failed', error);
        }
    };

    const handleDeleteChat = async () => {
        if (!selectedPartner || !window.confirm('Delete this entire conversation? Cannot be undone.')) return;
        try {
            await api.delete(`/messages/chat/${selectedPartner.id}`);
            setMessages([]);
            setPartners(prev => prev.filter(p => p.id !== selectedPartner.id));
            setSelectedPartner(null);
        } catch (error) {
            alert('Failed to delete: ' + error.message);
        }
    };

    const handleBlockToggle = async () => {
        if (!selectedPartner) return;

        if (isBlocked) {
            // Unblock logic
            if (!window.confirm(`Unblock ${selectedPartner.name}?`)) return;
            try {
                await api.delete(`/messages/block/${selectedPartner.id}`);
                setIsBlocked(false);
                alert('User unblocked');
            } catch (error) {
                alert('Failed to unblock: ' + (error.response?.data || error.message));
            }
        } else {
            // Block logic
            if (!window.confirm(`Block ${selectedPartner.name}? You won't receive messages from them.`)) return;
            try {
                await api.post(`/messages/block/${selectedPartner.id}`);
                setIsBlocked(true);
                alert('User blocked');
            } catch (error) {
                alert('Failed to block: ' + (error.response?.data || error.message));
            }
        }
    };

    return (
        <div className="fade-in" style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '2rem', height: 'calc(100vh - 120px)' }}>

            {/* Sidebar */}
            <div className="card" style={{ overflowY: 'auto', padding: '1rem', display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                    <h3 style={{ margin: 0 }}>Messages</h3>
                    <button onClick={() => setIsSearching(!isSearching)} style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}>
                        {isSearching ? 'Back to List' : 'Find User'}
                    </button>
                </div>

                {/* Message Content Search */}
                <div style={{ marginBottom: '1rem' }}>
                    <input
                        type="text"
                        placeholder="Search messages..."
                        value={searchQuery}
                        onChange={(e) => handleMessageSearch(e.target.value)}
                        style={{ width: '100%', fontSize: '0.9rem' }}
                    />
                </div>

                {/* Search Results Mode */}
                {searchQuery ? (
                    <div style={{ flex: 1, overflowY: 'auto' }}>
                        <h4 style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Search Results</h4>
                        {searchResults.length === 0 && <p className="text-muted text-sm">No messages found.</p>}
                        {searchResults.map((msg) => {
                            // Find partner to switch context
                            const partnerId = msg.senderId === user.id ? msg.recipientId : msg.senderId;
                            // We need partner info. For now, try to find in existing partners list or fetch?
                            // Simpler: Just click to open chat if we have partner logic (might need fetch).
                            const partner = partners.find(p => p.id === partnerId) || { id: partnerId, name: 'User ' + partnerId };

                            return (
                                <div
                                    key={msg.id}
                                    onClick={() => { setSelectedPartner(partner); setSearchQuery(''); setSearchResults([]); }}
                                    style={{ padding: '0.5rem', borderBottom: '1px solid var(--border)', cursor: 'pointer', fontSize: '0.85rem' }}
                                >
                                    <div style={{ fontWeight: 'bold' }}>{msg.senderId === user.id ? 'You' : partner.name}</div>
                                    <div className="text-muted" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{msg.text}</div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    // Normal Partner List Mode
                    <>
                        {isSearching && (
                            <form onSubmit={(e) => { e.preventDefault(); handleEmailSearch(); }} style={{ marginBottom: '1rem' }}>
                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <input
                                        type="email"
                                        placeholder="Enter user email..."
                                        value={emailSearchQuery}
                                        onChange={(e) => setEmailSearchQuery(e.target.value)}
                                        style={{ flex: 1 }}
                                        autoFocus
                                        required
                                    />
                                    <button type="submit">Go</button>
                                </div>
                            </form>
                        )}

                        <div style={{ flex: 1, overflowY: 'auto' }}>
                            {partners.length === 0 && <p className="text-muted">No conversations.</p>}
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
                    </>
                )}
            </div>

            {/* Main: Chat Window */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                {!selectedPartner ? (
                    <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                        Select a conversation to start chatting
                    </div>
                ) : (
                    <>
                        <div style={{ paddingBottom: '1rem', borderBottom: '1px solid var(--border)', marginBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <h3 style={{ margin: 0 }}>{selectedPartner.name}</h3>
                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                <button
                                    onClick={handleBlockToggle}
                                    style={{
                                        background: isBlocked ? 'var(--text-muted)' : 'var(--danger)',
                                        opacity: 0.8,
                                        fontSize: '0.8rem',
                                        padding: '0.3rem 0.8rem'
                                    }}
                                >
                                    {isBlocked ? 'Unblock' : 'Block'}
                                </button>
                                <button onClick={handleDeleteChat} style={{ background: 'transparent', border: '1px solid var(--danger)', color: 'var(--danger)', fontSize: '0.8rem', padding: '0.3rem 0.8rem' }}>Delete Chat</button>
                            </div>
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
