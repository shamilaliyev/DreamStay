# DreamStay

## About the Project

**DreamStay** is a full-stack web application developed as a university academic project, designed to simulate a real-world real estate listing platform where users can list, browse, and explore properties for sale or rent. The project follows a modern Frontend + Backend architecture and demonstrates practical full-stack development skills.

The main goals of this project are:

- Building a full-stack web application
- Integrating frontend and backend components
- Working with RESTful APIs
- Managing data storage and file uploads
- Applying collaborative development practices using GitHub


## Project Objectives

This project was developed for academic purposes and aims to demonstrate:

- Usage of modern JavaScript frameworks
- Backend development with Java technologies
- API design and implementation
- Git and GitHub workflow in team projects
- Proper project structuring for real-world applications


## Technologies Used

### Frontend
- React
- Vite
- HTML5
- CSS3
- JavaScript (ES6+)

### Backend
- Java (Spring Boot)
- Gradle
- RESTful API

### Data Storage
- PostgreSQL (Migrated from JSON-based storage)
- `database_dump.sql` included for schema and sample data

### Other Tools and Concepts
- Git and GitHub
- REST APIs
- Multipart file upload (image/video handling)
- JWT / Session Management (if applicable)


## Key Features

### User Roles
- **Buyer**: Browse properties, favorite items, contact sellers, write reviews.
- **Seller**: List properties, manage listings, interact with buyers.
- **Agent**: Similar to Seller but with verified professional status.
- **Admin**: Platform oversight, user approval, property verification.

### Core Functionalities
- **Property Management**: 
  - Create, edit, and delete listings.
  - Upload multiple photos and videos.
  - Detailed property views with location, amenities, and media galleries.
- **Search & Filter**: Advanced filtering by price, location, rooms, etc.
- **User Verification**:
  - ID Document upload for Agents/Sellers.
  - Admin approval workflow for verified status.
- **Messaging System**:
  - Real-time-like messaging between users.
  - Inbox/Outbox management.
  - **Blocking**: Users can block others to prevent harassment.
- **Reviews & Reports**:
  - Rate and review sellers/agents.
  - Report inappropriate content or users to Admins.


## Project Structure

```
DreamStay/
│
├── frontend/          # Client-side application (React + Vite)
│   ├── src/
│   │   ├── components/# Reusable UI components
│   │   ├── pages/     # Application public and protected pages
│   │   └── ...
│   ├── ...
│
├── backend/           # Server-side application (Spring Boot)
│   ├── src/
│   │   ├── main/java/ # Controllers, Models, Services, Repositories
│   │   └── ...
│   ├── database_dump.sql # PostgreSQL database schema and data
│   └── ...
│
├── backend_uml.md     # Backend Class Diagram
├── frontend_uml.md    # Frontend Architecture Diagram
└── README.md
```


## Installation and Running the Project

### 1. Database Setup (PostgreSQL)
Before running the backend, you must set up the database.

1.  **Create User & DB**:
    ```sql
    CREATE USER dreamstay_user WITH PASSWORD 'password';
    CREATE DATABASE dreamstay;
    GRANT ALL PRIVILEGES ON DATABASE dreamstay TO dreamstay_user;
    ```
2.  **Import Data**:
    Navigate to the `backend/` directory and run:
    ```bash
    psql -U dreamstay_user -h localhost -d dreamstay -f database_dump.sql
    ```
    *(Or use pgAdmin to restore `database_dump.sql`)*

### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
The frontend will run by default at:  
[http://localhost:5173](http://localhost:5173/)

### 3. Backend Setup
```bash
cd backend
gradlew bootRun
```
The backend will run by default at:  
[http://localhost:8080](http://localhost:8080/)


## Frontend and Backend Integration

The frontend communicates with the backend through RESTful APIs.

Main functionalities include:

- Fetching and displaying user data
- Listing available properties (hotels and accommodations)
- Adding and displaying reviews
- Sending and storing user messages
- Uploading and handling images


## Applying Changes and Restarting Services

When making changes to the frontend or backend code, the running services may need to be restarted for the updates to take effect.

### Restarting the Backend
If the backend is already running and you make changes to the server-side code, stop the current process and restart it:

```bash
# Stop the running backend (press in terminal)
Ctrl + C

# Start the backend again
cd backend
gradlew bootRun
```

### Restarting the Frontend
In most cases, the frontend development server automatically reloads after changes. If it does not, restart it manually:

```bash
# Stop the running frontend (press in terminal)
Ctrl + C

# Start the frontend again
cd frontend
npm run dev
```


## Team Collaboration

This project was developed as a team-based university project.

Key collaboration aspects:

- Clear separation of frontend and backend responsibilities
- Structured commit history
- Collaborative development using GitHub


## Academic Note

This project was developed purely for educational purposes and is meant to illustrate real-world software engineering concepts that are taught at the university level. Skills demonstrated include:

- Full-stack web development
- Team collaboration
- Version control with GitHub
- Applying theoretical knowledge in practice


## Project Author

WhoCares Team


## Team Contributions

This project was developed collaboratively by a team of four members. The workload was distributed equally, with each member contributing approximately 25% to the overall project.

### Contribution Details

- **Shamil Aliyev (45%)** -- Backend development, including server-side logic, REST API implementation, data handling, and application configuration.

- **Yusif Behbudov (25%)** -- Frontend development, including user interface design, component structure, state management, and frontend-backend integration.

- **Yusif Abbasov (15%)** -- Assisted with frontend development, contributing to UI components, feature implementation, testing, and improving user experience.

- **Ayyub Guluzada (15%)** -- Assisted with backend development, supporting API development, testing server-side functionality, debugging, and integration tasks.

All team members were involved in planning, development, testing, and integration phases, ensuring balanced and equal participation throughout the project lifecycle.


## License

This project is intended for academic and educational use only.
