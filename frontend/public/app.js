// Old canvas and context setup - no longer needed with P5.js
// const canvas = document.getElementById('simulationCanvas'); 
// const ctx = canvas.getContext('2d'); 

// Old canvas dimension setting - P5.js handles this via createCanvas
// canvas.width = window.innerWidth; 
// canvas.height = window.innerHeight; 

const backendUrl = 'http://localhost:8080/api/simulation/state';
let creaturesCache = []; 

// --- P5.js Structure ---

function setup() {
    createCanvas(windowWidth, windowHeight); // Use P5.js windowWidth and windowHeight
    console.log("P5.js setup complete. Canvas created at window dimensions:", windowWidth, "x", windowHeight);
    
    // Initial fetch of creatures
    fetchCreatures().then(data => {
        if (data) {
            creaturesCache = data;
            console.log('Initial creatures fetched in setup:', creaturesCache.length);
        }
    }).catch(error => {
        console.error("Error during initial fetch in setup:", error);
    });

    // Periodic refresh of creature data
    setInterval(async () => {
        try {
            const data = await fetchCreatures();
            if (data) {
                creaturesCache = data;
                // console.log('Creatures cache updated:', creaturesCache.length); // Can be noisy
            }
        } catch (error) {
            console.error("Error during periodic fetch:", error);
        }
    }, 500); // Refresh every 500ms
    
    console.log('P5.js: setup() finished.');
}

function windowResized() {
    resizeCanvas(windowWidth, windowHeight);
    console.log("P5.js canvas resized to:", windowWidth, windowHeight);
    // Optional: Redraw static elements or re-center view if needed after resize
    // The main draw() loop will continue to render content.
}

function draw() {
    background(220); // Clear background with light gray

    if (creaturesCache.length > 0) {
        for (let creature of creaturesCache) {
            drawP5Creature(creature); 
        }
    }
    
    // console.log('P5.js draw() loop running'); // Can be very noisy
}

// --- End P5.js Structure ---

function drawP5Creature(creature) {
    if (!creature || typeof creature.x !== 'number' || typeof creature.y !== 'number' || typeof creature.size !== 'number') {
        // console.warn("Invalid creature data for P5 drawing:", creature);
        return;
    }

    let creatureColorValue; 
    let shapeType = 'rect'; 
    let p5DrawSize = creature.size;

    switch (creature.type) {
        case 'Plant':
            creatureColorValue = color(0, 128, 0); 
            shapeType = 'rect';
            break;
        case 'Herbivore':
            creatureColorValue = color(0, 0, 255); 
            shapeType = 'circle';
            break;
        case 'Carnivore':
            creatureColorValue = color(255, 0, 0); 
            shapeType = 'circle';
            break;
        default:
            if (creature.color && typeof creature.color === 'string' && creature.color.startsWith('#')) {
                try {
                    creatureColorValue = color(creature.color); 
                } catch (e) {
                    creatureColorValue = color(128); 
                }
            } else {
                creatureColorValue = color(128); 
            }
            break;
    }

    fill(creatureColorValue);
    noStroke(); 

    if (shapeType === 'rect') {
        rectMode(CENTER);
        rect(creature.x, creature.y, p5DrawSize, p5DrawSize);
    } else if (shapeType === 'circle') {
        ellipseMode(CENTER);
        ellipse(creature.x, creature.y, p5DrawSize, p5DrawSize); 
    }
    
    // console.log(`P5 Draw: type=${creature.type}, shape=${shapeType}, color=${creatureColorValue.toString()}, x=${creature.x}, y=${creature.y}, size=${p5DrawSize}`);
}

async function fetchCreatures() { 
    try {
        const response = await fetch(backendUrl);
        // The console logs for API status and parsed JSON were here. 
        // They can be re-added if specific debugging of fetch is needed again.
        // console.log('API Response Status:', response.status); 
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        // console.log('Fetched creatures data (parsed JSON):', data); 
        return data;
    } catch (error) {
        console.error("Could not fetch creatures:", error);
        return []; 
    }
}

// Note: The old gameLoop, old drawCreature (using ctx), the old click listener, 
// and the old download button logic have been removed as they are obsolete with P5.js.
// P5.js handles the draw loop automatically. Event handling (clicks, downloads) 
// will be re-implemented using P5.js specific functions if needed.
// The console logs specific to those old functions are also implicitly removed.
// The logs for 'Creatures received in gameLoop' and the more detailed 'Drawing creature' logs
// from the old structure are also naturally gone with the removal of gameLoop and old drawCreature.
// The new P5 structure has its own logs in setup, draw, drawP5Creature, and fetchCreatures.

document.addEventListener('DOMContentLoaded', () => {
    const downloadButton = document.getElementById('downloadCanvasBtn');
    if (downloadButton) {
        downloadButton.addEventListener('click', () => {
            // Ensure P5 canvas exists and saveCanvas function is available
            if (typeof saveCanvas === 'function') { 
                saveCanvas('ecosystem_snapshot', 'png'); // P5.js global function
                console.log('P5.js: Canvas download initiated via saveCanvas().');
            } else {
                console.error('P5.js saveCanvas function not found. Is P5.js loaded correctly and script order correct?');
            }
        });
    } else {
        console.warn('Download button #downloadCanvasBtn not found.');
    }
});
