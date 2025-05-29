# User Testing Steps for Full-Screen Canvas

This document outlines the steps to test the modifications made to make the canvas fill the entire browser window.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend Node.js server is running (`npm start` in the `frontend` directory) with the latest `style.css` and `app.js` (canvas dimensions set to `window.innerWidth/Height`).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12) and select the **"Console"** tab. Keep this visible.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the Canvas and Page Layout (Key Visual Test):**
    *   [ ] **Key Question 1:** Does the canvas now fill the entire browser window?
        *   Try resizing the browser window. Does the canvas resize with it immediately (if resize handling was added, though the current task only sets it on load), or does it maintain the initial full-screen size? For this test, the initial full-screen size is the primary check.
        *   Are there any scrollbars on the page? (There shouldn't be, due to `overflow: hidden`).
    *   [ ] **Fixed Background Art:**
        *   Is the canvas background still light gray, now covering the full window?
        *   Is the red square (50x50) still visible at the top-left corner (10,10) of the window?
        *   Is the blue circle (50px radius) now positioned relative to the top-right of the *entire window* (centered at `window.innerWidth - 60`, `60`)?
        *   Is the green horizontal line (5px thick) now drawn across the middle of the *entire window*?
    *   [ ] **Creature Drawing:**
        *   Are creatures (Plants, Herbivores, Carnivores, with their correct shapes/colors/sizes) still visible?
        *   Are they being drawn in the top-left portion of the canvas? (This is expected, as their coordinates are still within the original effective simulation area, e.g., 0-600 for x and y, while the canvas is now larger).

4.  **Inspect Console Logs:**
    *   Carefully examine the console output:
    *   [ ] Are all the previous diagnostic logs still appearing as expected (API status, fetched data, creature drawing attempts, fixed art drawing attempt)?
    *   [ ] **Error Messages:** Are there any *new* JavaScript errors appearing in the console, particularly anything related to canvas dimensions, `window.innerWidth/Height`, or drawing operations?
    *   [ ] Does the "message channel closed" error (if previously observed) still appear?

5.  **Report Back Specific Observations (User Task):**
    Please provide feedback on the following:

    *   **1. Canvas Full-Screen:**
        *   Did the canvas fill the entire browser window? (Yes / No)
        *   Were there any scrollbars? (Yes / No)
    *   **2. Fixed Background Art Positioning:**
        *   Was the light gray background full-screen? (Yes / No)
        *   Was the red square at the top-left of the window? (Yes / No)
        *   Was the blue circle at the top-right of the window? (Yes / No)
        *   Was the green line across the middle of the window? (Yes / No)
    *   **3. Creature Visibility:**
        *   Were creatures still visible? (Yes / No)
        *   Were they located in the top-left area of the (now larger) canvas? (Yes / No)
    *   **4. Console Errors:**
        *   Report any new error messages.
        *   Report the status of the "message channel closed" error.

This feedback will confirm if the full-screen canvas implementation is working as intended visually and without new errors.
