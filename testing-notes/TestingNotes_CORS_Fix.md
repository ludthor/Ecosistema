# Manual Verification Steps for CORS Fix - Ecosystem Simulator

This document outlines the steps to manually verify that the CORS (Cross-Origin Resource Sharing) configuration on the Spring Boot backend is working correctly, allowing the frontend application to communicate with it without CORS errors.

## Prerequisites:

1.  The latest backend code with the `WebConfig.java` CORS configuration is built and ready to run.
2.  The frontend code is up-to-date.

## Verification Steps:

1.  **Ensure Backend is Running:**
    *   Navigate to the project root directory (where `ecosystem-app` and `frontend` are located).
    *   Start the Spring Boot backend application:
        ```bash
        ./gradlew bootRun 
        ```
        (or `gradlew.bat bootRun` on Windows)
    *   Verify it starts without errors and is listening on `http://localhost:8080`.

2.  **Ensure Frontend is Running:**
    *   Navigate to the `frontend` directory:
        ```bash
        cd frontend
        ```
    *   Start the Node.js Express server (if not already running, or if it was stopped):
        ```bash
        npm start
        ```
    *   Verify it starts without errors and is listening on `http://localhost:8081`.

3.  **Test in Browser:**
    *   Open a web browser (e.g., Chrome, Firefox, Edge). It's often helpful to use a private/incognito window to avoid caching issues during testing.
    *   Open the browser's Developer Tools (usually by pressing F12). Select the "Console" tab and also familiarize yourself with the "Network" tab.
    *   Navigate to the frontend URL: `http://localhost:8081`.

4.  **Verify Functionality and Absence of CORS Error:**

    *   **Console Tab:**
        *   [ ] **Crucial Check:** Verify that there are **NO** CORS-related errors. Specifically, the error message like "Access to fetch at 'http://localhost:8080/api/simulation/state' from origin 'http://localhost:8081' has been blocked by CORS policy..." should **NOT** be present.
        *   [ ] Check for any other unexpected JavaScript errors.

    *   **Network Tab:**
        *   [ ] Look for the requests made to `http://localhost:8080/api/simulation/state`.
        *   [ ] For each such request, verify that the HTTP status code is **200 OK**.
        *   [ ] Click on one of these successful requests to inspect its details.
        *   [ ] In the "Headers" section for the response, look for the `Access-Control-Allow-Origin` header.
        *   [ ] **Crucial Check:** Verify that the `Access-Control-Allow-Origin` header is present and its value is `http://localhost:8081`.

    *   **Visual Confirmation:**
        *   [ ] The ecosystem simulation should appear and load correctly on the canvas.
        *   [ ] Creatures (Plants, Herbivores, Carnivores) should be rendered according to their types (colors and shapes).
        *   [ ] Creatures should be moving, indicating that data is being successfully fetched and updated from the backend.

    *   **Interaction:**
        *   [ ] Click on different creatures on the canvas.
        *   [ ] Check the browser's developer console. Creature information should still be logged as before, confirming that click-event-driven API communication (if any, or data fetching for display) is working.

## Expected Outcome:

If all checks pass, particularly the absence of CORS errors in the console and the presence of the correct `Access-Control-Allow-Origin` header in network responses, the CORS configuration is working as expected. The frontend should be fully functional.

## Troubleshooting:

*   If CORS errors persist:
    *   Double-check that the `WebConfig.java` file is correctly implemented and saved in the `com.ecosystem.config` package.
    *   Ensure the Spring Boot application was rebuilt and restarted *after* adding/modifying the `WebConfig.java`.
    *   Verify the `allowedOrigins` in `WebConfig.java` exactly matches the frontend URL (`http://localhost:8081`).
    *   Clear browser cache or test in a new incognito window.
*   If the backend doesn't start, check for compilation errors or issues in the Spring Boot application logs.
*   If the frontend doesn't start, check for errors in the Node.js server console.
