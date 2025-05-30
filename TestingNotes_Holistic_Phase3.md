# Holistic Testing Checklist - Phase 3 (Post-Debugging and Feature Additions)

This document guides you through a holistic test of the Ecosystem Simulator web application after recent debugging efforts (related to the blank canvas and JavaScript errors) and the addition of new features (full-screen canvas, canvas download).

**Objective:** To ensure all components are working together correctly, creatures are rendering as expected, new features are functional, and no new significant issues have been introduced.

## Prerequisites:

1.  The latest backend code (Spring Boot application) is running (`./gradlew bootRun`).
2.  The latest frontend code (Node.js server in `frontend` directory, `npm start`) is running, incorporating all recent changes to `app.js` and `style.css`.

## I. Pre-Test Recommendation:

*   [ ] **Browser State:** It's highly recommended to perform these tests in an **Incognito/Private browser window** or with **all browser extensions temporarily disabled**. This helps ensure that the "message channel closed" error (if still present) is not due to extensions and provides a cleaner testing environment.
*   [ ] **Developer Tools:** Have the browser's Developer Tools open (F12), with the **"Console"** tab visible to monitor logs and errors throughout the test.

## II. Test Execution:

Navigate to the frontend URL (typically `http://localhost:8081`) and perform the following checks.

### A. Full-Screen Canvas and Fixed Background Art Verification
*(Referencing checks from `TestingNotes_FullScreenCanvas.md` and `TestingNotes_FixedBackgroundArt.md`)*

1.  **Canvas Layout:**
    *   [ ] Does the main canvas element fill the *entire* browser window?
    *   [ ] Are there *no scrollbars* on the page?
2.  **Fixed Background Art:**
    *   [ ] Is the overall canvas background `lightgray` (or the color set for the fixed background)?
    *   [ ] Is the `red square` (50x50) visible at the top-left of the *window* (coordinates 10,10)?
    *   [ ] Is the `blue circle` (50px radius) visible at the top-right of the *window*?
    *   [ ] Is the `green horizontal line` (5px thick) visible across the middle of the *window*?
3.  **Console Log for Fixed Art:**
    *   [ ] Does the message `"Attempted to draw fixed background art."` appear repeatedly in the console (once per frame)?

### B. Creature Rendering Verification
*(Referencing checks from `TestingNotes_CreaturesOnFixedArt.md` and knowledge of the 600x600 effective simulation area)*

1.  **Creature Visibility and Appearance:**
    *   [ ] Are creatures visible, appearing **on top of** the fixed background art?
    *   [ ] Are **Plants** rendered as **green rectangles** of varying sizes?
    *   [ ] Are **Herbivores** rendered as **blue circles** of varying sizes?
    *   [ ] Are **Carnivores** rendered as **red circles** of varying sizes?
    *   [ ] Do creatures appear to be confined to the **top-left 600x600 pixel area** of the (now larger) canvas? (This is expected, as their coordinate system is based on `effectiveSimWidth/Height`).
    *   [ ] Do creatures move, and does their movement include toroidal wrapping at the edges of this 600x600 effective area?
2.  **Console Logs for Creature Drawing:**
    *   [ ] Does the `Drawing creature: {...}` log appear for each creature per frame, showing raw data?
    *   [ ] Does the `Restored Shapes - Attempting draw: type=..., shape=..., color=..., x=..., y=..., size=...` log appear for each creature per frame?
        *   Inspect a few of these logs: Are `type`, `shape`, `color` (actual fillStyle), `x`, `y`, and `size` all plausible and consistent with the visual output?
        *   Are `x` and `y` coordinates within the `[0, 600)` range?
        *   Are `size` values positive numbers?

### C. Data Fetching Verification
*(General check, logs should be present from previous debugging steps)*

1.  **API Communication Logs:**
    *   [ ] Is `API Response Status: 200` logged?
    *   [ ] Is `Fetched creatures data (parsed JSON): [...]` logged, showing a valid array?
    *   [ ] Is `Creatures received in gameLoop: [...]` logged, showing a valid array?

### D. Download Canvas Feature Verification
*(Referencing checks from `TestingNotes_DownloadCanvas.md`)*

1.  **Button Visibility:**
    *   [ ] Is the "Download Canvas" button visible (typically bottom-left)?
2.  **Download Functionality:**
    *   [ ] Click the "Download Canvas" button. Does it trigger a file download?
    *   [ ] Is the default filename `ecosystem_snapshot.png`?
    *   [ ] Open the downloaded image. Does it accurately represent the *entire current canvas view*, including:
        *   The full-screen fixed background art (gray, red square, blue circle, green line).
        *   All currently visible creatures, correctly positioned within their 600x600 effective area on the larger canvas.
3.  **Console Log for Download:**
    *   [ ] Is `"Canvas download initiated."` logged when the button is clicked?

### E. General Console Health

1.  **JavaScript Errors:**
    *   [ ] Throughout all testing, are there any *new or persistent critical JavaScript errors* in the console (aside from the "message channel closed" error if it's still present and deemed external)?
2.  **"Message Channel Closed" Error:**
    *   [ ] If this error ("Uncaught (in promise) Error: A listener indicated an asynchronous response by returning true, but the message channel closed before a response was received") was previously observed, does it still appear?
    *   [ ] Did testing in an Incognito/Private window (or with extensions disabled) affect its appearance?

## III. Feedback Request:

Please provide comprehensive feedback on the above points. For each checklist item ([ ]):
*   Indicate if it passed (Yes/No, or describe the observation).
*   If something failed or behaved unexpectedly, please describe the issue in as much detail as possible, including:
    *   Relevant console logs (especially error messages or unexpected values in diagnostic logs).
    *   A description of what you observed vs. what you expected.

**Key areas of interest for feedback:**
*   **Primary:** Is the main "blank canvas" issue resolved? Are creatures consistently visible and rendered correctly according to their type, color, size, and shape within their designated 600x600 area?
*   **Secondary:** Do the full-screen canvas and download features work as expected?
*   **Tertiary:** What is the status of the "message channel closed" error, especially when testing with extensions disabled/incognito mode?

This holistic test will help confirm the stability and correctness of the application after the recent series of changes.
