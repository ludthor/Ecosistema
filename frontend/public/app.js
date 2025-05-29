const canvas = document.getElementById('simulationCanvas');
const ctx = canvas.getContext('2d');

// Set canvas dimensions (should match backend simulation dimensions if possible)
// canvas.width = 800; // Old value
// canvas.height = 500; // Old value
canvas.width = 600;  // New value, matching example effectiveSimWidth
canvas.height = 600; // New value, matching example effectiveSimHeight

const backendUrl = 'http://localhost:8080/api/simulation/state'; // Assuming Spring Boot runs on 8080
let creaturesCache = []; // Cache for storing fetched creatures

async function fetchCreatures() {
    try {
        const response = await fetch(backendUrl);
        console.log('API Response Status:', response.status); // Added log
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log('Fetched creatures data (parsed JSON):', data); // Added log
        return data;
    } catch (error) {
        console.error("Could not fetch creatures:", error);
        return []; // Return empty array on error
    }
}

function drawCreature(creature) {
    console.log('Drawing creature:', creature); // Added log
    let creatureColor = creature.color; // Default to its given color from backend (which is random hex)
    let shape = 'rect'; // Default shape
    let drawSize = creature.size; // Default size

    // Determine color and shape based on type, overriding the random hex color for specific types
    switch (creature.type) {
        case 'Plant':
            creatureColor = 'green';
            shape = 'rect'; // Plants are squares
            // Plants could be slightly larger or have a fixed size for visibility
            // drawSize = 10; // Optional: override size for plants
            break;
        case 'Herbivore':
            creatureColor = 'blue';
            shape = 'circle'; // Herbivores are circles
            break;
        case 'Carnivore':
            creatureColor = 'red';
            shape = 'circle'; // Carnivores are circles
            break;
        default:
            // For base "Creature" type if any, or unknown types
            // Keep the creature.color if type is unknown, or default to grey
            creatureColor = creature.color || 'grey'; 
            break;
    }

    ctx.fillStyle = creatureColor;
    
    if (shape === 'rect') {
        ctx.fillRect(creature.x - drawSize / 2, creature.y - drawSize / 2, drawSize, drawSize);
    } else if (shape === 'circle') {
        ctx.beginPath();
        ctx.arc(creature.x, creature.y, drawSize / 2, 0, Math.PI * 2);
        ctx.fill();
    }

    // Optional: Draw health bar or energy level (can be added later)
    // Example:
    // if (creature.type !== 'Plant') { // Don't draw for plants for now
    //     ctx.fillStyle = 'rgba(0, 0, 0, 0.5)';
    //     ctx.fillRect(creature.x - drawSize / 2, creature.y - drawSize / 2 - 7, drawSize, 5);
    //     ctx.fillStyle = 'lightgreen'; // Or 'orange' for energy
    //     ctx.fillRect(creature.x - drawSize / 2, creature.y - drawSize / 2 - 7, drawSize * (creature.health / 100), 5);
    // }
}

async function gameLoop() {
    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const fetchedCreatures = await fetchCreatures(); // Renamed to avoid conflict with global 'creatures' if any
    console.log('Creatures received in gameLoop:', fetchedCreatures); // Added log
    creaturesCache = fetchedCreatures; // Update cache
    
    creaturesCache.forEach(creature => {
        if (creature && typeof creature.x === 'number' && typeof creature.y === 'number' && typeof creature.size === 'number') {
            drawCreature(creature);
        } else {
            console.warn("Invalid creature data for drawing:", creature);
        }
    });

    requestAnimationFrame(gameLoop); // Loop
}

// Click handler
canvas.addEventListener('click', function(event) {
    const rect = canvas.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;

    const clickedCreature = creaturesCache.find(c => {
        if (!c || typeof c.x !== 'number' || typeof c.y !== 'number' || typeof c.size !== 'number') {
            return false;
        }
        const size = c.size;
        // Check bounds based on shape
        if (c.type === 'Plant' || (c.type !== 'Herbivore' && c.type !== 'Carnivore')) { // Assuming Plant and default are rects
            return mouseX >= c.x - size / 2 && mouseX <= c.x + size / 2 &&
                   mouseY >= c.y - size / 2 && mouseY <= c.y + size / 2;
        } else { // Herbivore and Carnivore are circles
            const distance = Math.sqrt(Math.pow(mouseX - c.x, 2) + Math.pow(mouseY - c.y, 2));
            return distance <= size / 2;
        }
    });

    if (clickedCreature) {
        console.log("Creature clicked:", {
            id: clickedCreature.id,
            name: clickedCreature.name,
            gender: clickedCreature.gender,
            type: clickedCreature.type,
            energy: clickedCreature.energy,
            health: clickedCreature.health,
            x: clickedCreature.x,
            y: clickedCreature.y,
            size: clickedCreature.size,
            color: clickedCreature.color // Log original color for debugging
        });
        // Display this info on the page instead of console.log in a future task
        // For example, update a div: document.getElementById('creatureInfo').textContent = JSON.stringify(clickedCreature, null, 2);
    }
});

// Start the simulation loop
gameLoop();
