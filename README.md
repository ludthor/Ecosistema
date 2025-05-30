# Ecosystem Simulator Web App

## Description

A web-based application simulating a simple ecosystem with plants, herbivores, and carnivores. This project is a modern web application rewrite of an original Java Processing-based ecosystem simulation. It features a Spring Boot backend that runs the simulation logic and a JavaScript frontend for visualization.

## Technologies Used

*   **Backend:**
    *   Java (JDK 17)
    *   Spring Boot (version 2.7.5)
    *   Gradle
*   **Frontend:**
    *   JavaScript (ES6+)
    *   HTML5
    *   CSS3
    *   Express.js (for serving static frontend files)
*   **Simulation:**
    *   Custom Java logic for creature movement (including toroidal world wrapping), interaction (eating, hunting, reproduction), and lifecycle (aging, energy consumption, health).
    *   Creature types include Plants, Herbivores, and Carnivores, each with distinct behaviors.

## Setup and Running Instructions

### Prerequisites:

*   **JDK:** Version 17 or newer is recommended (project is set to sourceCompatibility '17').
*   **Gradle:** The project includes a Gradle wrapper (`gradlew` and `gradlew.bat`), so a separate Gradle installation is not strictly required. If you prefer to use an installed version, ensure it's compatible.
*   **Node.js and npm:** Required for the frontend. Download from [nodejs.org](https://nodejs.org/).

### Backend (Spring Boot Application):

1.  **Navigate to the project root directory** in your terminal.
2.  **Run the Spring Boot application:**
    *   On Linux/macOS: `./gradlew bootRun`
    *   On Windows: `gradlew.bat bootRun`
3.  The backend server will start, and by default, it will be available at `http://localhost:8080`.
    *   The simulation begins running automatically in the background.

### Frontend (Node.js/Express Server):

1.  **Navigate to the `frontend` directory:**
    ```bash
    cd frontend
    ```
2.  **Install dependencies:**
    ```bash
    npm install
    ```
3.  **Start the frontend server:**
    ```bash
    npm start
    ```
4.  The frontend server will start, and by default, it will be accessible at `http://localhost:8081`.
5.  **Open `http://localhost:8081` in your web browser** to view the simulation.

## API Endpoint

The application exposes an API endpoint to get the current state of the simulation.
For detailed information about the API, see the [API Documentation](API_DOCUMENTATION.md).

## Project Structure Overview

*   `ecosystem-app/`: Root directory for the backend Spring Boot application.
    *   `src/main/java/com/ecosystem/`: Main Java source code for the backend.
        *   `controller/`: Spring MVC controllers (e.g., `SimulationController.java`).
        *   `model/`: Core data models.
            *   `creatures/`: Creature classes (`Creature.java`, `Plant.java`, `Herbivore.java`, `Carnivore.java`).
            *   `environment/`: Environment classes (`Territory.java`, `TPlace.java`).
            *   `enums/`: Enumerations like `Gender.java`.
        *   `services/`: Service layer classes (e.g., `SimulationService.java`).
        *   `utils/`: Utility classes (e.g., `EcoUtils.java`).
    *   `src/test/java/com/ecosystem/`: Unit tests for the backend.
    *   `build.gradle`: Gradle build script for the backend.
*   `frontend/`: Root directory for the frontend application.
    *   `public/`: Static assets (HTML, CSS, JavaScript client code).
        *   `index.html`: Main HTML page for the frontend.
        *   `style.css`: CSS styles.
        *   `app.js`: Client-side JavaScript for fetching data and rendering the simulation on canvas.
    *   `package.json`: Node.js project file for managing frontend dependencies and scripts.
    *   `server.js`: Express.js server to serve the static files in `public/`.
*   `API_DOCUMENTATION.md`: Detailed documentation for the backend API.
*   `README.md`: This file - project overview and setup instructions.
*   `TestingNotes.md`: Manual frontend testing checklist.

---

This structure separates the backend Java application from the frontend JavaScript application, allowing them to be developed and run somewhat independently, communicating via the defined API.
