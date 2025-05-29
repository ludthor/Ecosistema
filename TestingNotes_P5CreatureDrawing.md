# User Testing Steps for P5.js Creature Drawing

This document outlines the steps to test `frontend/public/app.js` after data fetching has been integrated with the P5.js structure and the `drawP5Creature()` function is called within the P5.js `draw()` loop.

**Assumption:** Testing of the basic P5.js setup (Subtask #20 - `TestingNotes_P5Setup.md`) confirmed that the P5.js canvas was created and its basic `draw()` loop was functional (e.g., rendering fixed art).

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`) and serving creature data at `/api/simulation/state`.
2.  The frontend `index.html` includes the P5.js library.
3.  The frontend `app.js` has been updated to:
    *   Fetch creature data in `setup()` (initial load and periodic refresh via `setInterval`).
    *   Store fetched data in `creaturesCache`.
    *   Call `drawP5Creature(creature)` for each creature in `creaturesCache` within the P5.js `draw()` loop.
    *   The `drawP5Creature()` function uses P5.js commands to render creatures.
    *   Fixed diagnostic art has been removed from the `draw()` loop (background is just cleared).
4.  The frontend Node.js server is running (`npm start` in `frontend` directory).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12) and select the **"Console"** tab. Keep this visible.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the Canvas (Key Visual Test):**
    *   [ ] **Key Question 1:** Is the canvas background light gray (from `background(220);` in `draw()`)?
    *   [ ] **Key Question 2:** Are creatures now visible on the P5.js canvas, rendered using P5.js commands?
        *   Do **Plants** appear as **dark green rectangles** (from `color(0, 128, 0)`) of various sizes?
        *   Do **Herbivores** appear as **blue circles** (from `color(0, 0, 255)`) of various sizes?
        *   Do **Carnivores** appear as **red circles** (from `color(255, 0, 0)`) of various sizes?
        *   Are creatures sized according to their `creature.size` property?
    *   [ ] Do creatures appear to be moving and updating their positions, indicating that the periodic data fetch and redraw are working?
    *   [ ] Are creatures confined to the 600x600 canvas area (as defined by `createCanvas(600, 600)`)?

4.  **Inspect Console Logs:**
    *   Carefully examine the console output:
    *   [ ] **P5.js Setup Log:** `"P5.js setup complete. Canvas created 600x600."` should appear once.
    *   [ ] **Data Fetching Logs:**
        *   `"API Response Status: 200"` (should appear for initial and periodic fetches).
        *   `"Fetched creatures data (parsed JSON): [...]"` (should show creature array for initial and periodic).
        *   `"Initial creatures fetched in setup: <count>"` (should appear once).
        *   Optional: `"Creatures cache updated: <count>"` (if uncommented in `setInterval`, would appear periodically).
    *   [ ] **P5 Creature Drawing Logs (Optional):** If you uncommented the log inside `drawP5Creature` (`// console.log(\`P5 Draw: type=${creature.type}...\`);`), it would be very noisy but could confirm parameters for individual P5.js drawing calls. For this test, it's likely better to keep it commented unless no creatures appear.
    *   **Error Messages:**
        *   [ ] Are there any JavaScript errors, especially P5.js related errors (e.g., "color is not defined", "rect is not defined") or errors from the `fetchCreatures` callbacks?
        *   [ ] Are there any errors like "Maximum call stack size exceeded" which might indicate an issue with the draw loop or data handling?
        *   [ ] Is the "message channel closed" error still present?

5.  **Report Back Specific Observations (User Task):**
    Please provide feedback on the following:

    *   **1. Canvas Appearance - Creatures:**
        *   Were creatures visible on the light gray P5.js canvas? (Yes / No)
        *   If yes, did their appearance (shape, color, size) match the P5.js drawing logic:
            *   Plants: Dark green rectangles?
            *   Herbivores: Blue circles?
            *   Carnivores: Red circles?
            *   Were sizes variable and appropriate?
        *   Did creatures appear to move/update over time?
    *   **2. Console Logs:**
        *   Confirm P5.js setup and data fetching logs appeared as expected.
        *   Report any errors, especially P5.js or data fetching related.
        *   Report the status of the "message channel closed" error.
    *   **3. Overall Functionality:**
        *   Does the simulation appear to be rendering correctly using P5.js based on fetched data?

This feedback will determine if the core migration of data fetching and rendering to the P5.js structure is successful.
