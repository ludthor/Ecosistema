# User Testing Steps for P5.js Full-Screen Canvas

This document outlines the steps to test the full-screen canvas functionality implemented in `frontend/public/app.js` using P5.js.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend `index.html` includes the P5.js library.
3.  The frontend `style.css` is configured for full-page body/html.
4.  The frontend `app.js` has been updated to:
    *   Use `createCanvas(windowWidth, windowHeight)` in `setup()`.
    *   Implement the `windowResized()` function with `resizeCanvas(windowWidth, windowHeight)`.
5.  The frontend Node.js server is running (`npm start` in the `frontend` directory).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12) and select the **"Console"** tab. Keep this visible.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the Canvas (Key Visual Test):**
    *   [ ] **Key Question 1:** Does the P5.js canvas now fill the *entire* browser window upon initial load?
        *   The background should be light gray (from `background(220);` in `draw()`).
    *   [ ] **Creature Drawing:**
        *   Are creatures (Plants, Herbivores, Carnivores, with correct shapes/colors/sizes) still visible?
        *   Are they being drawn in the **top-left portion** of the full-screen canvas? (This is expected, as their coordinates are still within the original effective simulation area, e.g., 0-600 for x and y, while the P5 canvas is now larger).
    *   [ ] **Window Resizing:**
        *   **Key Question 2:** Manually resize your browser window (make it larger, smaller, change aspect ratio).
        *   Does the P5.js canvas dynamically resize to fill the new window dimensions? The light gray background should always fill the new window size.
        *   After resizing, do creatures continue to draw correctly in the top-left portion of the resized canvas?

4.  **Inspect Console Logs:**
    *   Carefully examine the console output:
    *   [ ] **P5.js Setup Log:** Is the message `"P5.js setup complete. Canvas created at window dimensions: <actual_width> x <actual_height>"` logged once, reflecting the initial window size?
    *   [ ] **P5.js Resize Log:** When you resize the browser window, is the message `"P5.js canvas resized to: <new_width> <new_height>"` logged each time the resize operation completes?
    *   **Data Fetching & Creature Drawing Logs:**
        *   [ ] Are the logs related to data fetching (`API Response Status`, `Fetched creatures data`, `Initial creatures fetched`) and creature drawing (`drawP5Creature` logs, if enabled) still appearing correctly?
    *   **Error Messages:**
        *   [ ] Are there any *new* JavaScript errors appearing in the console, especially related to `windowWidth`, `windowHeight`, `createCanvas`, or `resizeCanvas`?

5.  **Report Back Specific Observations (User Task):**
    Please provide feedback on the following:

    *   **1. Initial Full-Screen Canvas:**
        *   Did the canvas fill the entire browser window on load? (Yes / No)
    *   **2. Creature Visibility & Positioning:**
        *   Were creatures visible? (Yes / No)
        *   Were they located in the top-left area of the full-screen canvas? (Yes / No)
    *   **3. Dynamic Resizing:**
        *   Did the canvas resize correctly when you changed the browser window size? (Yes / No)
        *   Did creatures remain in the top-left after resizing? (Yes / No)
    *   **4. Console Logs:**
        *   Confirm the P5.js setup log showed initial window dimensions.
        *   Confirm the P5.js resize log appeared with correct new dimensions when resizing the window.
        *   Report any new error messages.

This feedback will confirm if the P5.js full-screen and dynamic resizing functionality is working as intended.
