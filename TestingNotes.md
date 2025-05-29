# Manual Frontend Testing Checklist for Ecosystem Simulator

This document outlines manual checks to be performed to ensure basic frontend functionality of the Ecosystem Simulator.

## Prerequisites:
1.  Backend Spring Boot application is running (typically on `http://localhost:8080`).
2.  Frontend Node.js server (`server.js` in `frontend` directory) is running (typically on `http://localhost:8081`).

## 1. Visual Verification:

*   **Load the page:**
    *   Open `http://localhost:8081` (or the address where the frontend Node.js server is running) in a web browser.
*   **Canvas Display:**
    *   [ ] Verify the canvas element (`<canvas id="simulationCanvas">`) is visible on the page.
    *   [ ] Verify the canvas dimensions are 800px width by 500px height (or as per current configuration).
    *   [ ] Verify the canvas background color is white (or as styled in `style.css`).
*   **Creature Rendering:**
    *   [ ] Are creatures appearing on the canvas after a brief loading period?
    *   [ ] Are Plants rendered as green squares?
    *   [ ] Are Herbivores rendered as blue circles?
    *   [ ] Are Carnivores rendered as red circles?
    *   [ ] Do creatures (Herbivores, Carnivores) generally move around the canvas?
    *   [ ] Do Plants remain mostly stationary (any movement should be minimal, if at all)?
    *   [ ] Do creatures appear to wrap around the edges of the canvas (toroidal movement)? (e.g., a creature exiting the right edge should reappear on the left).

## 2. Interaction Verification:

*   **Clicking on Creatures:**
    *   [ ] Click on various individual Plant creatures.
    *   [ ] Click on various individual Herbivore creatures.
    *   [ ] Click on various individual Carnivore creatures.
    *   [ ] After each click, check the browser's developer console.
        *   [ ] Does it log information for the clicked creature?
        *   [ ] Does the logged information include: `id`, `name`, `gender`, `type`, `energy`, `health`, `x`, `y`, `size`?
    *   [ ] Verify that click detection is reasonably accurate for square shapes (Plants).
    *   [ ] Verify that click detection is reasonably accurate for circular shapes (Herbivores, Carnivores). Ensure the click registers near the visible shape, not just a bounding box.
*   **API Communication:**
    *   [ ] Open the browser's developer tools to the "Network" tab.
    *   [ ] Filter requests if necessary (e.g., by "state" or "XHR").
    *   [ ] Verify that periodic GET requests are being made to the backend API endpoint (e.g., `http://localhost:8080/api/simulation/state`).
    *   [ ] For these requests:
        *   [ ] Check that the HTTP status code is 200 (OK).
        *   [ ] Check that the response payload is valid JSON.
        *   [ ] Check that the JSON data appears to be an array of creature objects with expected properties.

## 3. Console Errors:

*   **JavaScript Errors:**
    *   [ ] Open the browser's developer console.
    *   [ ] Check for any JavaScript errors reported during page load.
    *   [ ] Check for any JavaScript errors reported during interactions (e.g., clicking, or while the simulation is running).

## 4. Simulation Logic (Observed via Frontend - requires longer observation):

*   **Interactions (Longer Observation - several minutes):**
    *   [ ] **Herbivore-Plant:** Do Herbivores appear to affect Plants when they move over them? (e.g., do Plants disappear over time, especially if many Herbivores are in an area? This is a subtle visual check as direct "eating" animation is not implemented).
    *   [ ] **Carnivore-Herbivore:** Do Carnivores appear to affect Herbivores? (e.g., do Herbivores disappear if a Carnivore spends time near them?).
    *   [ ] **Reproduction:** Do creatures of the same type (Herbivores with Herbivores, Carnivores with Carnivores) that spend time close to each other eventually lead to new, smaller creatures of the same type appearing nearby? (This is very subtle and depends on timing and backend logic).
*   **Creature Survival:**
    *   [ ] Do creatures (especially Herbivores and Carnivores) eventually disappear from the canvas? This would visually indicate they have "died" based on backend logic (e.g., age, energy, or health depletion). (This requires some understanding or observation of the typical lifespan/energy consumption rates from the backend).

---
**Note:** These checks are primarily for verifying the frontend's ability to correctly fetch, display, and interact with data from the backend. Detailed verification of the backend simulation logic itself is covered by backend unit tests.I have documented the manual frontend testing checklist in a conceptual `TestingNotes.md` file. This list covers visual verification, interaction verification, API communication checks, console error checks, and basic observational checks for the simulation logic as perceived through the frontend. This fulfills the requirements of the current subtask.
