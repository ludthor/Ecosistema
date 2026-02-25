# API Documentation - Ecosystem Simulator

This document provides details about the API endpoint available in the Ecosystem Simulator application.

Related project docs:

*   [README.md](README.md) for run instructions and QA checklist
*   [testing-notes/README.md](testing-notes/README.md) for archived implementation/testing notes

## Get Simulation State

*   **Endpoint:** `GET /api/simulation/state`
*   **Description:** Retrieves the current state of all creatures in the simulation. The simulation progresses independently in the background via a scheduler, and this endpoint provides the latest snapshot of the creature states.
*   **Response Body:** JSON array of creature objects.

## Get Simulation Metadata

*   **Endpoint:** `GET /api/simulation/metadata`
*   **Description:** Returns reproducibility, world-space bounds, and population metadata for the current simulation run.
*   **Response Body:** JSON object with seed, step count, and alive population counts.

### Metadata Object Structure

```json
{
  "seed": 12345,
  "step": 87,
  "totalCreatures": 52,
  "plants": 28,
  "herbivores": 15,
  "carnivores": 9,
  "effectiveSimWidth": 600.0,
  "effectiveSimHeight": 600.0
}
```

## Reset Simulation (Optional Deterministic Seed)

*   **Endpoint:** `POST /api/simulation/reset`
*   **Description:** Resets the simulation to a newly initialized population. If a `seed` query parameter is provided, initialization and future random events become reproducible for that seed.
*   **Query Parameter (Optional):** `seed` (Long)
*   **Examples:**
  *   `POST /api/simulation/reset`
  *   `POST /api/simulation/reset?seed=12345`
*   **Response Body:** JSON array of creature objects after reset.

### Creature Object Structure

Each object in the JSON array represents a creature and has the following structure:

```json
[
  {
    "id": 1,
    "name": "Adce",
    "x": 123.45,
    "y": 67.89,
    "dx": 1.12, 
    "dy": -0.45,
    "direction": 1.98,
    "dirVariation": 0.26,
    "size": 15,
    "color": "#AABBCC",
    "toroidal": true,
    "health": 95.5,
    "energy": 88.0,
    "speed": 1.2,
    "gender": "MALE",
    "type": "Carnivore",
    "age": 150,
    "maxAge": 1200,
    "mutationRate": 0.05,
    "mateTimer": 0,
    "maxOffspring": 2,
    "offspringEnergy": 40.0,
    "status": "Roaming",
    "alive": true
  }
  // ... more creatures
]
```

**Key Fields for Frontend Display:**

*   `id` (Number): Unique identifier for the creature.
*   `name` (String): Generated name of the creature.
*   `x` (Number): Current X-coordinate of the creature on the simulation canvas.
*   `y` (Number): Current Y-coordinate of the creature on the simulation canvas.
*   `size` (Number): Represents the diameter of the creature, used for rendering.
*   `color` (String): Hex color code (e.g., "#RRGGBB") initially assigned to the creature. Note: The frontend may override this with type-specific colors (e.g., green for Plants).
*   `type` (String): The type of the creature (e.g., "Plant", "Herbivore", "Carnivore", "Creature").
*   `gender` (String): Gender of the creature ("MALE", "FEMALE", "NEUTRAL").
*   `health` (Number): Current health points of the creature.
*   `energy` (Number): Current energy level of the creature.
*   `age` (Number): Current age of the creature in simulation steps.
*   `alive` (Boolean): Indicates if the creature is currently alive.

**Additional Fields (Primarily for Backend Logic / Detailed State):**

*   `dx` (Number): Speed component along the X-axis.
*   `dy` (Number): Speed component along the Y-axis.
*   `direction` (Number): Current direction of movement in radians.
*   `dirVariation` (Number): Amount (in radians) by which direction can change per step.
*   `toroidal` (Boolean): Indicates if the world wraps around (true) or has hard boundaries (false).
*   `speed` (Number): Overall movement speed of the creature.
*   `maxAge` (Number): Maximum age the creature can reach before dying.
*   `mutationRate` (Number): Rate at which creature attributes might mutate during reproduction.
*   `mateTimer` (Number): Cooldown timer after mating, preventing immediate re-mating.
*   `maxOffspring` (Number): Maximum number of offspring a creature can produce in one mating event.
*   `offspringEnergy` (Number): Amount of energy transferred to each offspring upon birth.
*   `status` (String): Current behavior-state label (e.g., `Roaming`, `Seeking food`, `Foraging`, `Evading`, `Pursuing`, `Feeding`, `Mate-seeking`, `Growing`).

## Frontend Interaction Notes (Current UX)

*   The frontend consumes `effectiveSimWidth` and `effectiveSimHeight` from metadata to map world coordinates to canvas coordinates consistently.
*   The frontend supports deterministic testing controls:
  *   **Reset** with optional seed input
  *   **Random Reset**
  *   **Replay Check** (same-seed deterministic validation)
*   Keyboard toggle: press `L` to show/hide behavior cue overlays and legend visibility state.

While all fields are available in the API response, the "Key Fields for Frontend Display" are typically the most relevant for visualization. The "Additional Fields" provide a more complete state of the creature, which might be useful for debugging or more complex frontend representations not yet implemented.
