# User Testing Steps for P5.js Download Canvas Feature

This document outlines the steps to test the "Download Canvas" feature after it has been updated to use P5.js's `saveCanvas()` function.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend `index.html` includes the P5.js library and the `<button id="downloadCanvasBtn">`.
3.  The frontend `app.js` has been updated to:
    *   Use P5.js for canvas creation and rendering (`setup()`, `draw()`, `drawP5Creature()`).
    *   Implement full-screen canvas functionality.
    *   Include the new event listener for `#downloadCanvasBtn` that uses `saveCanvas()`.
4.  The frontend Node.js server is running (`npm start` in the `frontend` directory).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12) and select the **"Console"** tab. Keep this visible.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the UI:**
    *   [ ] **Button Visibility:** Is the "Download Canvas" button still visible on the page (typically bottom-left)?
    *   [ ] **Canvas Display:** Is the P5.js canvas displaying correctly (full-screen, with creatures in the top-left 600x600 area)?

4.  **Test Download Functionality (P5.js method):**
    *   [ ] **Key Action:** Click the "Download Canvas" button.
    *   [ ] **File Download Prompt:** Does a file download prompt appear, or does the file download automatically? (P5.js `saveCanvas()` usually triggers an immediate download).
    *   [ ] **Filename:** Is the downloaded file named `ecosystem_snapshot.png`?
    *   [ ] **Save the File:** Complete the download process if prompted.
    *   [ ] **Open Downloaded Image:** Locate and open the downloaded PNG file.
    *   [ ] **Verify Image Content:**
        *   Does the downloaded image accurately represent the *entire current state of the P5.js canvas*?
        *   This should include the full-screen light gray background (if `background(220)` is still in `draw()`).
        *   It should include any visible creatures, correctly positioned within their 600x600 effective area on the larger canvas.
        *   The image dimensions should match the current full-screen canvas dimensions.

5.  **Inspect Console Logs:**
    *   Carefully examine the console output after clicking the button:
    *   [ ] **Download Log:** Is the message `"P5.js: Canvas download initiated via saveCanvas()."` logged in the console?
    *   **Error Messages:**
        *   [ ] Are there any JavaScript errors related to the download functionality (e.g., "saveCanvas is not defined", or errors from within the P5.js library)?
        *   [ ] (Check general console health) Does the "message channel closed" error (if previously observed) still appear or interfere?

6.  **Report Back Specific Observations (User Task):**
    Please provide feedback on the following:

    *   **1. Button Visibility:** Was the "Download Canvas" button visible? (Yes / No)
    *   **2. Download Process (P5.js):**
        *   Did clicking the button trigger a file download using `saveCanvas()`? (Yes / No)
        *   Was the default filename `ecosystem_snapshot.png`? (Yes / No)
    *   **3. Downloaded Image:**
        *   Were you able to open the downloaded image? (Yes / No)
        *   Did the image accurately reflect the full P5.js canvas content (full-screen background, creatures)? (Yes / No, and describe discrepancies if any)
    *   **4. Console Logs:**
        *   Did the `"P5.js: Canvas download initiated via saveCanvas()."` message appear? (Yes / No)
        *   Were there any errors logged during or after the download attempt, especially `saveCanvas not found`?
    *   **5. Overall Functionality:**
        *   Does the download feature now work correctly using the P5.js method?

This feedback will confirm if the canvas download feature has been successfully migrated to use P5.js's `saveCanvas()` function.
