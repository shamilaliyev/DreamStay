package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find messages where user is recipient
    List<Message> findByRecipientId(Long recipientId);

    // Find messages where user is sender
    List<Message> findBySenderId(Long senderId);

    // Find messages for a specific property
    List<Message> findByPropertyId(Long propertyId);

    // Check if conversation exists (simplification for "hasUserContacted")
    boolean existsBySenderIdAndRecipientId(Long senderId, Long recipientId);

    // Search messages for a user
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE (m.senderId = :userId OR m.recipientId = :userId) AND LOWER(m.text) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Message> searchMessages(@org.springframework.data.repository.query.Param("userId") Long userId,
            @org.springframework.data.repository.query.Param("query") String query);

    // Delete entire conversation between two users
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM Message m WHERE (m.senderId = :user1Id AND m.recipientId = :user2Id) OR (m.senderId = :user2Id AND m.recipientId = :user1Id)")
    void deleteConversation(@org.springframework.data.repository.query.Param("user1Id") Long user1Id,
            @org.springframework.data.repository.query.Param("user2Id") Long user2Id);
}
