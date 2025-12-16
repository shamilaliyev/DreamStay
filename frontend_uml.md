# DreamStay Frontend Architecture Diagram

```mermaid
graph TD
    %% Entry Point
    Entry[main.jsx] --> App[App.jsx]

    %% Router & State
    App --> Router[React Router]
    App --> AuthState[User State localStorage]

    %% Public Routes
    Router --> Login[Login.jsx]
    Router --> Register[Register.jsx]
    Router --> Properties[Properties.jsx]
    Router --> PropDetails[PropertyDetails.jsx]
    Router --> OwnerProf[OwnerProfile.jsx]

    %% Protected Routes: Generic
    Router --> MyProfile[MyProfile.jsx]
    Router --> Messages[Messages.jsx]

    %% Protected Routes: Admin
    Router --> AdminDash[AdminDashboard.jsx]
    AdminDash --> AdminUser[AdminUserDetails.jsx]
    AdminDash --> AdminProp[AdminPropertyDetails.jsx]

    %% Protected Routes: Seller/Agent
    Router --> MyProps[MyProperties.jsx]
    Router --> AddProp[AddProperty.jsx]
    Router --> EditProp[EditProperty.jsx]
    Router --> UploadId[UploadId.jsx]
    
    %% Components Usage
    PropDetails --> MediaUploader[MediaUploader.jsx]
    AddProp --> MediaUploader
    EditProp --> MediaUploader

    %% Relationships
    subgraph Public Area
    Properties
    PropDetails
    Login
    Register
    OwnerProf
    end

    subgraph Admin Area
    AdminDash
    AdminUser
    AdminProp
    end

    subgraph User Area
    MyProfile
    Messages
    UploadId
    end

    subgraph Property Management
    MyProps
    AddProp
    EditProp
    end

    %% Styles
    classDef page fill:#f9f,stroke:#333,stroke-width:2px;
    classDef component fill:#dfd,stroke:#333,stroke-width:2px;
    
    class Login,Register,Properties,PropDetails,OwnerProf,MyProfile,Messages,AdminDash,AdminUser,AdminProp,MyProps,AddProp,EditProp,UploadId page;
    class MediaUploader component;
```

# Frontend Concept Map

- **App.jsx**: Main entry point handling Routing and Global User State (via `localStorage`).
- **Pages**:
    - **Public**: Login, Register, Property Listing, Property Details.
    - **Admin**: Dashboard for verification and user management.
    - **User/Seller**: My Profile, My Properties, Messages, ID Verification.
- **Components**:
    - **MediaUploader**: Reusable component for handling image and video uploads.
