# User Testing Steps for Download Canvas Feature

This document outlines the steps to test the "Download Canvas" feature implemented in the Ecosystem Simulator.

## Prerequisites:

1.  The backend Spring Boot application is running (`./gradlew bootRun`).
2.  The frontend Node.js server is running (`npm start` in the `frontend` directory) with the latest `index.html` (includes the download button) and `app.js` (includes the download handling logic).

## Testing Steps:

1.  **Ensure Backend and Frontend are Running:**
    *   Verify both servers are operational.

2.  **Test in Browser:**
    *   [ ] Open your web browser.
    *   [ ] Open the Developer Tools (usually by pressing F12) and select the **"Console"** tab. Keep this visible.
    *   [ ] Navigate to the frontend URL: `http://localhost:8081`.

3.  **Observe the UI:**
    *   [ ] **Key Question 1:** Is the "Download Canvas" button visible on the page? (It should be positioned at the bottom-left corner based on the inline styles).

4.  **Test Download Functionality:**
    *   [ ] **Key Action:** Click the "Download Canvas" button.
    *   [ ] **File Download Prompt:** Does a file download prompt appear in your browser, or does the file download automatically (depending on browser settings)?
    *   [ ] **Filename:** Is the default filename `ecosystem_snapshot.png`?
    *   [ ] **Save the File:** Complete the download process.
    *   [ ] **Open Downloaded Image:** Locate the downloaded PNG file and open it with an image viewer.
    *   [ ] **Verify Image Content:** Does the downloaded image accurately represent the state of the canvas at the moment you clicked the button? This should include:
        *   The fixed background art (light gray background, red square, blue circle, green line).
        *   Any creatures that were visible on the canvas, in their correct positions, shapes, colors, and sizes.
        *   The image dimensions should match the canvas dimensions (e.g., full-screen if that was the last setting for canvas size).

5.  **Inspect Console Logs:**
    *   Carefully examine the console output after clicking the button:
    *   [ ] **Download Log:** Is the message `"Canvas download initiated."` logged in the console?
    *   **Error Messages:**
        *   [ ] Are there any JavaScript errors related to the download functionality?
        *   [ ] Does the "message channel closed" error (if previously observed) appear or interfere?

6.  **Report Back Specific Observations (User Task):**
    Please provide feedback on the following:

    *   **1. Button Visibility:** Was the "Download Canvas" button visible and correctly positioned? (Yes / No, and describe position if wrong)
    *   **2. Download Process:**
        *   Did clicking the button trigger a file download? (Yes / No)
        *   Was the default filename `ecosystem_snapshot.png`? (Yes / No)
    *   **3. Downloaded Image:**
        *   Were you able to open the downloaded image? (Yes / No)
        *   Did the image accurately reflect the canvas content (background art and creatures)? (Yes / No, and describe discrepancies if any)
    *   **4. Console Logs:**
        *   Did the `"Canvas download initiated."` message appear? (Yes / No)
        *   Were there any errors logged during or after the download attempt?
    *   **5. Overall Functionality:**
        *   Does the download feature work as expected?

This feedback will confirm if the canvas download feature has been implemented correctly.
