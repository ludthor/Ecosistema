# User Testing Steps for P5.js Container Styling

This document outlines the steps to test the CSS changes made to `frontend/public/style.css` for styling the page container elements (`body`, `#app-container`, `#canvas-parent`) to ensure a clean, full-screen P5.js canvas presentation.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend `index.html` uses the `app-container` and `canvas-parent` structure.
3.  The frontend `app.js` parents the P5.js canvas to `#canvas-parent` and creates it with `windowWidth, windowHeight`.
4.  The frontend `style.css` has been updated with the new CSS reset and styles for `body`, `#app-container`, and `#canvas-parent`.
5.  The frontend Node.js server is running (`npm start` in the `frontend` directory).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12). Keep the **"Elements"** tab available to inspect CSS and the **"Console"** tab for any errors.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe Page and Canvas Layout (Key Visual Test):**
    *   [ ] **Page Background:** Is the overall page background (outside of any specific element styling) now a dark gray (`#222`)? (This might only be visible if the canvas or its parent somehow doesn't cover everything, or briefly during loading).
    *   [ ] **Canvas Full Screen:**
        *   Does the P5.js canvas (which should have a light gray background from `background(220)` in `app.js`) still fill the *entire* browser window?
        *   Are there any unexpected margins, paddings, or scrollbars around the canvas or the page? (The CSS reset and `overflow: hidden` should prevent these).
    *   [ ] **`<h1>` Tag Visibility:**
        *   Is the "Ecosystem Simulator" `<h1>` title visible?
        *   Based on the new CSS, it's styled with `position: absolute; top: 0; left: 0; width: 100%; text-align: center; z-index: 10;`. Does it appear centered at the top, potentially overlaying the very top of the canvas, with a slight semi-transparent background and white text?
    *   [ ] **Creature Drawing:** Are creatures still visible and drawing correctly in the top-left portion (0-600 effective area) of the full-screen canvas?
    *   [ ] **Download Button:** Is the "Download Canvas" button still visible and correctly positioned at the bottom-left, on top of the canvas?
    *   [ ] **Window Resizing:** Does resizing the browser window still result in the canvas correctly adapting to the new full size, with all elements (h1, button, creatures) maintaining their relative positions or expected behavior?

4.  **Inspect CSS (Elements Tab - Optional but helpful):**
    *   [ ] Select the `<body>` element. Verify its `background-color` is `#222` and it has `width: 100%; height: 100%; overflow: hidden;`.
    *   [ ] Select `<main id="app-container">`. Verify it has `width: 100%; height: 100%; display: flex; flex-direction: column;`.
    *   [ ] Select `<div id="canvas-parent">`. Verify it has `width: 100%; height: 100%;`. The P5.js canvas should be inside it.
    *   [ ] Select the P5.js generated `<canvas>` element. Its width and height attributes should match `window.innerWidth` and `window.innerHeight`.

5.  **Inspect Console Logs:**
    *   [ ] Are there any new JavaScript errors or warnings, especially related to layout or styling?
    *   [ ] Are all previous P5.js setup, resize, data fetching, and drawing logs appearing as expected?

6.  **Report Back Specific Observations (User Task):**
    Please provide feedback on:

    *   **1. Overall Layout:**
        *   Did the canvas correctly fill the entire screen with the new CSS?
        *   Was the page background color `#222` (where visible)?
        *   Was the `<h1>` title visible, centered at the top, and styled as expected (potentially overlaying the canvas)?
        *   Was the download button correctly positioned and visible?
    *   **2. Functionality:**
        *   Did creatures render correctly in their usual top-left area?
        *   Did window resizing work as expected for the canvas and UI elements?
    *   **3. Console Errors:**
        *   Report any new errors or warnings.

This feedback will confirm if the CSS changes for container styling achieve the desired clean, full-screen presentation and if all elements are behaving as expected.
