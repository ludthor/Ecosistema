# Ecosystem Simulator Web App

## Description

Ecosystem Simulator is a deterministic, web-based ecosystem simulation with a Java backend and a p5.js frontend. It models Plants, Herbivores, and Carnivores with lifecycle, interaction, and reproduction rules, and now includes reproducible seeded resets, behavior-state cues, and improved visual motion/rendering.

## Current Stack

*   **Backend**
    *   Java 21
    *   Spring Boot 3.4.3
    *   Gradle 9.x compatible build
*   **Frontend**
    *   JavaScript (ES6+), p5.js
    *   HTML5, CSS3
    *   Express.js static server
*   **Simulation/runtime capabilities**
    *   Deterministic RNG with seed control (`POST /api/simulation/reset?seed=...`)
    *   Metadata endpoint (`/api/simulation/metadata`) including world bounds and population counters
    *   Frontend world-to-canvas mapping with interpolation for smoother perceived movement
    *   Behavior states (e.g., `Roaming`, `Seeking food`, `Evading`, `Pursuing`, `Mate-seeking`, `Growing`)

## Run the App

### Prerequisites

*   Java 21+
*   Node.js + npm
*   Gradle (installed) or Gradle wrapper if preferred

### Backend (Spring Boot)

From this repository root (`Ecosistema/`):

```bash
cd ecosystem-app
gradle bootRun --no-daemon
```

Backend runs on `http://localhost:8080`.

### Frontend (Express)

From this repository root (`Ecosistema/`):

```bash
cd frontend
npm install
npm start
```

Frontend runs on `http://localhost:8081`.

## Frontend Controls

*   **Seed (optional) + Reset**: reset with deterministic seed
*   **Random Reset**: reset with non-deterministic seed
*   **Replay Check**: runs same-seed reset comparison
*   **Behavior cues toggle**: press `L` to show/hide in-canvas behavior cue rings and legend state
*   **Download Canvas**: export current frame as PNG

## API

See [API Documentation](API_DOCUMENTATION.md) for full request/response details.

Core endpoints:

*   `GET /api/simulation/state`
*   `GET /api/simulation/metadata`
*   `POST /api/simulation/reset`
*   `POST /api/simulation/reset?seed=<long>`

## Project Structure Overview

*   `ecosystem-app/` backend service and simulation model
*   `frontend/` web UI (p5.js rendering and controls)
*   `API_DOCUMENTATION.md` backend API reference
*   `testing-notes/` implementation/testing notes archive (`TestingNotes*.md`), indexed in `testing-notes/README.md`

## Notes

If visuals seem stale after updates, do a hard refresh in the browser (`Cmd+Shift+R`) to bypass cached frontend assets.

## Quick QA Checklist (2–3 minutes)

Use this checklist after pulling changes or updating simulation/render logic.

1. **Service health**
    * Backend responds: `GET /api/simulation/state` and `GET /api/simulation/metadata` return `200`.
    * Frontend loads at `http://localhost:8081`.

2. **Deterministic reset**
    * Enter a seed (e.g., `2026`) and click **Reset**.
    * Confirm header shows the same seed and step resets to `0`.
    * Click **Replay Check** and confirm it reports `PASS`.

3. **World/grid alignment**
    * Verify creatures occupy the same apparent interaction space as the grid/habitat.
    * Confirm there is no half-canvas seam or dead zone where grid/environment diverges from creature activity.

4. **Behavior-state readability**
    * Click different creatures and verify status badge appears (e.g., `Roaming`, `Seeking food`, `Pursuing`, `Evading`, `Growing`).
    * Confirm in-canvas cue rings match legend meaning.

5. **Behavior cue toggle**
    * Press `L` to hide behavior cues and legend state.
    * Press `L` again to restore cues.

6. **Timeline/metadata updates**
    * Let simulation run for ~5–10 seconds.
    * Confirm `step` increases and trend lines update.

7. **Random reset sanity**
    * Click **Random Reset** and verify `seed` becomes `Random` in UI.
    * Ensure simulation continues (population remains non-empty and moving).
