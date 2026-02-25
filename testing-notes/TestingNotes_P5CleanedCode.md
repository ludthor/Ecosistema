# User Testing Steps for P5.js Rendering (Cleaned Codebase)

This document guides you through testing the current P5.js rendering setup in `frontend/public/app.js` after obsolete code has been removed. The primary goal is to confirm that creatures are being fetched and rendered correctly using P5.js.

**Please refer primarily to the detailed steps already outlined in [TestingNotes_P5CreatureDrawing.md](TestingNotes_P5CreatureDrawing.md).** This document serves as a reminder of the key aspects to check with the now cleaner `app.js` file.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend `index.html` includes the P5.js library.
3.  The frontend `app.js` has been updated to the cleaned P5.js structure:
    *   Obsolete non-P5.js code (old game loop, old draw functions, old event listeners) has been removed.
    *   P5.js `setup()` initializes a 600x600 canvas, fetches initial data, and sets up periodic data refresh.
    *   P5.js `draw()` clears the background and calls `drawP5Creature()` for each creature in `creaturesCache`.
    *   `drawP5Creature()` uses P5.js commands to render creatures.
4.  The frontend Node.js server is running (`npm start` in the `frontend` directory).

## Key Verification Points (from [TestingNotes_P5CreatureDrawing.md](TestingNotes_P5CreatureDrawing.md)):

1.  **Ensure Backend and Frontend are Running.**
2.  **Test in Browser:**
    *   [ ] Open your web browser, preferably in an Incognito/Private window to minimize extension interference.
    *   [ ] Open the Developer Tools (F12) and select the **"Console"** tab.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the Canvas:**
    *   [ ] **P5.js Canvas:** Is a 600x600 canvas visible with a light gray background (from `background(220);` in `draw()`)?
    *   [ ] **Creature Rendering (Primary Question):** Are creatures visible and rendered correctly?
        *   **Plants:** Appear as dark green (`color(0, 128, 0)`) rectangles of varying sizes?
        *   **Herbivores:** Appear as blue (`color(0, 0, 255)`) circles of varying sizes?
        *   **Carnivores:** Appear as red (`color(255, 0, 0)`) circles of varying sizes?
        *   Creatures of other types (if any) should use their `creature.color` hex value or default to gray.
    *   [ ] **Movement/Updates:** Do creatures appear to move and update their positions, indicating the periodic data fetch and redraw are working?

4.  **Inspect Console Logs:**
    *   [ ] **P5.js Setup:** `"P5.js setup complete. Canvas created 600x600."` and `"P5.js: setup() finished."` logs.
    *   [ ] **Data Fetching:**
        *   `"API Response Status: 200"`
        *   `"Fetched creatures data (parsed JSON): [...]"`
        *   `"Initial creatures fetched in setup: <count>"`
        *   (Optional, if enabled) `"Creatures cache updated: <count>"` periodically.
    *   [ ] **`drawP5Creature` Logs (Optional):** If you have uncommented the log within `drawP5Creature`, confirm it shows reasonable parameters. Otherwise, this can remain commented.
    *   [ ] **Errors:** Are there any JavaScript errors in the console? Pay attention to P5.js related errors or issues from data fetching.

## Specific Feedback Request:

Please provide feedback focusing on:

*   **1. Creature Visibility and Correctness:**
    *   **Primary Question:** Are creatures visible on the P5.js canvas, and do they generally look correct according to the P5.js drawing logic (shape, color based on type, varying sizes)? (Yes / No, and describe any discrepancies)
*   **2. Console Errors:**
    *   Are there any errors in the console? If so, please provide the error messages.
*   **3. Initial Fixed Art (from `setup()` in previous P5 test - Subtask #20):**
    *   The fixed diagnostic art (red square, blue circle, green line) that was temporarily drawn in `setup()` in the *very first P5 setup test* (Subtask #20's [TestingNotes_P5Setup.md](TestingNotes_P5Setup.md)) is no longer being drawn in the current `app.js`'s `setup()` or `draw()` function (it was removed from `draw()` in Subtask #21, and was not part of `setup()` in the final version of #20's `app.js`). This is expected. The focus now is purely on creature rendering on the blank canvas.

This test will confirm if the migration to P5.js for rendering, using the cleaned-up codebase, is successful.
