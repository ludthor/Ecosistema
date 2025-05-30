# User Testing Steps for P5.js Canvas Container and HTML Structure

This document outlines the steps to test the updated HTML structure and the P5.js canvas being parented to a specific `div` container.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend `index.html` has been updated with the new structure (`app-container`, `canvas-parent` div, `ui-controls` div, and removed old canvas tag).
3.  The frontend `app.js` has been updated in `setup()` to use `cnv.parent('canvas-parent');`.
4.  The frontend `style.css` should still have rules for `html, body` to be full size. We will also verify if `#canvas-parent` needs additional styling.
5.  The frontend Node.js server is running (`npm start` in the `frontend` directory).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12). Select the **"Elements"** tab to inspect the DOM structure, and keep the **"Console"** tab visible for logs.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Verify HTML Structure (Elements Tab):**
    *   [ ] Inspect the DOM. Does it match the new structure?
        *   Is there a `<main id="app-container">`?
        *   Inside `app-container`, is there an `<h1>Ecosystem Simulator</h1>`?
        *   Inside `app-container`, is there a `<div id="canvas-parent">`?
        *   **Key Check:** Is the P5.js generated `<canvas>` element now *inside* the `<div id="canvas-parent">`? (P5.js might give its canvas a default ID like `defaultCanvas0` or similar class names).
        *   Is the `<div id="ui-controls">` (with the download button) present, typically after `canvas-parent` within `app-container`?
    *   [ ] Is the old `<canvas id="simulationCanvas"></canvas>` tag completely gone?

4.  **Observe Canvas Functionality and Layout:**
    *   [ ] **Canvas Visibility & Full Screen:**
        *   Is the P5.js canvas still visible and filling the entire browser window?
        *   (This might require `#canvas-parent` to also be styled to 100% width/height if the P5 canvas respects its parent's bounds strictly and `style.css` doesn't already make generic divs full size. If the canvas is *not* full screen, this is an important point to note for CSS refinement).
    *   [ ] **Creature Drawing:** Are creatures still visible and drawing correctly in the top-left portion of the canvas?
    *   [ ] **Window Resizing:** Does the canvas still resize correctly when the browser window is resized?
    *   [ ] **Download Button:** Is the "Download Canvas" button still visible (bottom-left) and functional?

5.  **Inspect Console Logs:**
    *   [ ] **P5.js Setup Log:** Does the log now say `"P5.js setup complete. Canvas created in #canvas-parent at window dimensions: <width> x <height>"`?
    *   [ ] **Other Logs:** Are all other logs (resize, data fetching, drawing, download initiation) still appearing correctly?
    *   [ ] **Errors:** Are there any new JavaScript errors, especially related to DOM manipulation or P5.js parenting?

6.  **Report Back Specific Observations (User Task):**
    Please provide feedback on:

    *   **1. HTML Structure:**
        *   Confirm the new DOM structure (presence of `app-container`, `canvas-parent`, P5 canvas inside `canvas-parent`, absence of old `simulationCanvas`).
    *   **2. Canvas Layout & Functionality:**
        *   Did the canvas still fill the full screen? If not, describe its appearance/size.
        *   Were creatures visible and correctly positioned in the top-left?
        *   Did window resizing work for the canvas?
        *   Did the download button work?
    *   **3. Console Logs:**
        *   Confirm the updated P5.js setup log message.
        *   Report any new errors or missing expected logs.

This feedback will confirm if the structural changes are successful and if any CSS adjustments are needed for `#canvas-parent` to ensure the full-screen canvas behavior is maintained.
