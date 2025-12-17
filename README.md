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
JSON-based data files:
- `users.json`
- `properties.json`
- `reviews.json`
- `messages.json`

### Other Tools and Concepts
- Git and GitHub
- REST APIs
- Multipart file upload (image handling)


## Project Structure

```
DreamStay14/
│
├── frontend/          # Client-side application (React + Vite)
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── backend/           # Server-side application (Spring Boot)
│   ├── src/
│   ├── build.gradle
│   ├── uploads/
│   └── messages.json
│
├── Database/          # JSON-based data storage
│   ├── users.json
│   ├── properties.json
│   ├── reviews.json
│   └── messages.json
│
└── README.md
```


## Installation and Running the Project

### Frontend Setup
```
cd frontend
npm install
npm run dev
```
The frontend will run by default at:  
[http://localhost:5173](http://localhost:5173/)


### Backend Setup
```
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
