# Manual Verification Steps for "Outside the Territory Grid" Fix

This document outlines the steps to manually verify that the fix for the "Creature ... is outside the territory grid" error messages is working correctly. The fix ensures creatures are initialized and move within the actual pixel boundaries covered by the `Territory` grid.

## Prerequisites:

1.  The latest backend code, including the modifications to `Creature.java`, `Plant.java`, `Herbivore.java`, `Carnivore.java`, and `SimulationService.java` (related to `effectiveSimWidth` and `effectiveSimHeight`), is built and ready to run.
2.  The frontend code is up-to-date (optional for this specific test, but recommended for context).

## Verification Steps:

1.  **Ensure Backend is Running:**
    *   Navigate to the project root directory.
    *   Start the Spring Boot backend application:
        ```bash
        ./gradlew bootRun
        ```
        (or `gradlew.bat bootRun` on Windows).
    *   **Crucial:** Pay close attention to the console output from the backend application, both during startup and as the simulation continues to run for several minutes.

2.  **Ensure Frontend is Running (Optional but Recommended for Visual Context):**
    *   Navigate to the `frontend` directory:
        ```bash
        cd frontend
        ```
    *   Start the Node.js Express server:
        ```bash
        npm start
        ```
    *   Open `http://localhost:8081` in a web browser.

3.  **Primary Test: Monitor Backend Console Output:**
    *   [ ] Observe the backend console output continuously for at least 2-3 minutes while the simulation is running (after initial creature setup and during ongoing movement and interaction steps).
    *   [ ] **Key Verification:** Confirm that the error messages "Creature ... is outside the territory grid. Position: (...)" (which originate from `System.err.println` in `Territory.java`) are **NO LONGER appearing** in the backend console. The absence of these messages is the primary indicator that the fix is working.

4.  **Secondary Test: Visual Confirmation (if Frontend is running):**
    *   [ ] Observe the creatures being rendered on the frontend canvas.
    *   [ ] All creatures should appear and move within a defined rectangular area on the canvas. This area corresponds to the `effectiveSimWidth` by `effectiveSimHeight` calculated in `SimulationService.java` (e.g., 600x600 pixels if `worldWidth=800, worldHeight=600, placeSize=60, territoryGrid=10x10`).
    *   [ ] **Expected Behavior:** If the canvas dimensions configured in `frontend/public/app.js` (e.g., 800x500 or 800x600 based on `canvasWorldWidth`/`Height` in `SimulationService`) are larger than the `effectiveSimWidth` or `effectiveSimHeight`, you should see empty space on the canvas where creatures do not enter. For example, if canvas is 800x600 but effective sim is 600x600, there might be an empty 200px strip on one side. This is an expected visual outcome of confining creatures to the territory's pixel area.
    *   [ ] Verify that creatures correctly wrap around the edges of this *effective simulation area*. For instance, if a creature moves off the right edge of the 600px effective width, it should reappear on the left edge of this 600px area.

## Expected Outcome:

The primary success criterion is the complete absence of "Creature ... is outside the territory grid" messages in the backend console output. Secondary visual confirmation on the frontend should show creatures respecting the new effective boundaries.

## Troubleshooting:

*   If "outside the territory grid" messages persist:
    *   Double-check all the code modifications from the fix:
        *   `Creature.java`: Correct storage and usage of `creatureEffectiveWorldWidth/Height` in constructors and `move()`. Correct passing of these to offspring in `reproduce()`.
        *   `Plant.java`, `Herbivore.java`, `Carnivore.java`: Constructors correctly call `super()` with effective dimensions.
        *   `SimulationService.java`: Correct calculation of `effectiveSimWidth/Height` and their use in `initializeCreatures()` and calls to `solveEncounter()`.
    *   Ensure the Spring Boot application was rebuilt (`./gradlew clean build bootRun`) and restarted *after* applying all code changes.
*   If visual behavior on the frontend is unexpected (e.g., creatures still seem to use old larger boundaries, or wrapping is incorrect):
    *   Verify that the `creatureEffectiveWorldWidth/Height` are being correctly used in the `Creature.move()` method's boundary conditions.
    *   Ensure the frontend is fetching the latest creature data.
    *   Confirm the canvas size in `app.js` versus the `effectiveSimWidth/Height` to understand expected empty areas.
