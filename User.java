# DreamStay Backend UML Class Diagram

```mermaid
classDiagram
    %% User Hierarchy
    class User {
        <<abstract>>
        -Long id
        -String name
        -String email
        -String role
        -String password
        -boolean isVerified
        -String governmentId
        -String avatarPath
        +setMailService(MailService)
        +setPropertyManager(PropertyManager)
    }

    class Admin {
        +approveUser(User)
        +verifyProperty(Property)
    }

    class Buyer {
        +addFavorite(Property)
    }

    class Seller {
        +List~Property~ properties
        +addProperty(Property)
    }

    class Agent {
        +List~Property~ properties
        +verifyId()
    }

    User <|-- Admin
    User <|-- Buyer
    User <|-- Seller
    User <|-- Agent

    %% Core Models
    class Property {
        -Long id
        -Long ownerId
        -String title
        -String location
        -double price
        -int rooms
        -int floor
        -String description
        -List~String~ photos
        -List~String~ videos
        -boolean isArchived
        -boolean isVerified
        +boolean hasPhotos()
        +boolean hasVideos()
    }

    class Message {
        -Long id
        -Long senderId
        -Long recipientId
        -Long propertyId
        -String text
        -LocalDateTime timestamp
        -boolean isRead
    }

    class Review {
        -Long reviewerId
        -Long targetUserId
        -int rating
        -String comment
    }

    %% Relationships
    Seller "1" --> "*" Property : owns
    Agent "1" --> "*" Property : manages
    User "1" --> "*" Message : sends
    User "1" --> "*" Message : receives
    User "1" --> "*" Review : writes
    User "1" --> "*" Review : receives
    Message "0..1" --> "1" Property : references

    %% Services
    class PropertyManager {
        +List~Property~ getAllProperties()
        +Property getPropertyById(Long)
        +void addProperty(Property)
        +void deleteProperty(Long)
    }

    class AuthService {
        +User login(String, String)
        +User register(User)
    }

    class MailService {
        +void sendMessage(Message)
        +List~Message~ getMessages(Long)
    }

    class ReviewService {
        +void addReview(Review)
        +double getAverageRating(Long)
    }

    %% Service Dependencies
    PropertyManager ..> Property : manages
    AuthService ..> User : authenticates
    MailService ..> Message : handles
    ReviewService ..> Review : handles
    
    %% Controllers (simplified)
    class PropertyController {
        +getProperty(Long)
        +createProperty(Property)
    }
    
    class AuthController {
        +login(LoginRequest)
    }

    PropertyController --> PropertyManager : uses
    AuthController --> AuthService : uses
```
