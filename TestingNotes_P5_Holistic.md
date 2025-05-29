# Holistic Testing Checklist - P5.js Frontend Implementation

This document guides you through a holistic test of the Ecosystem Simulator's frontend, which has been migrated to use the P5.js library for canvas management, rendering, and other features.

**Objective:** To ensure all P5.js-based components (creature rendering, full-screen canvas, dynamic resizing, canvas download) are working together correctly and no significant issues have been introduced during the migration.

## Prerequisites:

1.  The latest backend code (Spring Boot application) is running (`./gradlew bootRun`).
2.  The latest frontend code (Node.js server in `frontend` directory, `npm start`) is running. This includes:
    *   `index.html` loading the P5.js library from CDN.
    *   `style.css` configured for full-page display.
    *   `app.js` fully migrated to P5.js structure (`setup()`, `draw()`, `drawP5Creature()`, `windowResized()`, P5-based download).

## I. Pre-Test Recommendation:

*   [ ] **Browser State:** It's highly recommended to perform these tests in an **Incognito/Private browser window** or with **all browser extensions temporarily disabled**. This helps ensure a clean testing environment.
*   [ ] **Developer Tools:** Have the browser's Developer Tools open (F12), with the **"Console"** tab visible to monitor logs and errors throughout the test.

## II. Test Execution:

Navigate to the frontend URL (typically `http://localhost:8081`) and perform the following checks, referencing details from previous P5.js specific testing notes where necessary.

### A. P5.js Canvas Setup and Full-Screen Functionality
*(Referencing checks from `TestingNotes_P5FullScreenCanvas.md` and `TestingNotes_P5CleanedCode.md`)*

1.  **Initial Canvas State:**
    *   [ ] On page load, does the P5.js canvas correctly occupy the *entire* browser window?
    *   [ ] Is the canvas background light gray (or the color set by `background()` in `draw()`)?
    *   [ ] **Console Log:** Is `"P5.js setup complete. Canvas created at window dimensions: <width> x <height>"` logged?
2.  **Dynamic Resizing:**
    *   [ ] Manually resize the browser window (larger, smaller, different aspect ratio).
    *   [ ] Does the P5.js canvas dynamically resize to fill the new window dimensions?
    *   [ ] **Console Log:** Is `"P5.js canvas resized to: <new_width> <new_height>"` logged each time the resize completes?

### B. P5.js Creature Rendering
*(Referencing checks from `TestingNotes_P5CleanedCode.md` or `TestingNotes_P5CreatureDrawing.md`)*

1.  **Creature Visibility and Appearance:**
    *   [ ] Are creatures visible on the P5.js canvas?
    *   [ ] Are **Plants** rendered as **dark green P5.js rectangles** of varying sizes?
    *   [ ] Are **Herbivores** rendered as **blue P5.js circles** (ellipses) of varying sizes?
    *   [ ] Are **Carnivores** rendered as **red P5.js circles** (ellipses) of varying sizes?
    *   [ ] Are creatures sized according to their `creature.size` property?
    *   [ ] Are creatures appearing in the **top-left 600x600 area** of the full-screen canvas? (This is expected as their coordinate system is based on `effectiveSimWidth/Height` which was 600x600 in P5 setup via `createCanvas(600,600)` before full-screen was implemented - if `createCanvas` is now `windowWidth, windowHeight` and no scaling is done, they'd still be in that 0-600 range from top-left).
        *Clarification based on latest changes: `createCanvas` is now `windowWidth, windowHeight`. The simulation logic for creature coordinates is still based on an effective 600x600 area. So, creatures should appear in the top-left of the larger canvas.*
    *   [ ] Do creatures move and update their positions, indicating periodic data fetching and P5.js redraws are working?
2.  **Console Logs for Creature Data & Drawing:**
    *   [ ] Data Fetching: Are logs like `"API Response Status: 200"`, `"Fetched creatures data (parsed JSON): [...]"`, and `"Initial creatures fetched in setup: <count>"` appearing correctly?
    *   [ ] `drawP5Creature` (Optional): If the detailed log inside `drawP5Creature` is enabled, do parameters look correct? (Usually kept commented due to noise).

### C. P5.js Download Canvas Feature
*(Referencing checks from `TestingNotes_P5DownloadCanvas.md`)*

1.  **Button Visibility:**
    *   [ ] Is the "Download Canvas" button visible (typically bottom-left)?
2.  **Download Functionality:**
    *   [ ] Click the "Download Canvas" button. Does it trigger a file download using P5.js `saveCanvas()`?
    *   [ ] Is the default filename `ecosystem_snapshot.png`?
    *   [ ] Open the downloaded image. Does it accurately represent the *entire current P5.js canvas view*, including the full-screen background and all currently visible creatures in their correct positions (top-left area)?
3.  **Console Log for Download:**
    *   [ ] Is `"P5.js: Canvas download initiated via saveCanvas()."` logged when the button is clicked?

### D. General Console Health and Errors

1.  **JavaScript Errors:**
    *   [ ] Throughout all testing, are there any *new or persistent critical JavaScript errors* in the console, especially any P5.js specific errors (e.g., "color is not defined", "rect is not defined", "saveCanvas is not defined")?
2.  **"Message Channel Closed" Error:**
    *   [ ] If this error was previously observed, does it still appear?
    *   [ ] Does testing in an Incognito/Private window (or with extensions disabled) affect its appearance?

## III. Feedback Request:

Please provide comprehensive feedback on the above points. For each checklist item ([ ]):
*   Indicate if it passed (Yes/No, or describe the observation).
*   If something failed or behaved unexpectedly, please describe the issue in as much detail as possible, including:
    *   Relevant console logs (especially error messages or unexpected values in diagnostic logs).
    *   A description of what you observed vs. what you expected.

**Key areas of interest for feedback:**
*   **Primary:** Is the P5.js rendering of creatures stable and correct (appearance, positioning within the larger full-screen canvas)?
*   **Secondary:** Are the P5.js full-screen canvas/resizing and P5.js download features working as expected?
*   **Tertiary:** What is the overall stability and console health of the P5.js frontend?

This holistic test will help confirm the success of the P5.js migration for core frontend functionalities.
