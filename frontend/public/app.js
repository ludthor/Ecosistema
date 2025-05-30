// Old canvas and context setup - no longer needed with P5.js
// const canvas = document.getElementById('simulationCanvas'); 
// const ctx = canvas.getContext('2d'); 

// Old canvas dimension setting - P5.js handles this via createCanvas
// canvas.width = window.innerWidth; 
// canvas.height = window.innerHeight; 

const backendUrl = 'http://localhost:8080/api/simulation/state';
let creaturesCache = []; 
let p5CanvasBackgroundColor; // Global variable for P5.js canvas background

// Helper function to read CSS variables
function getCSSVariable(varName) {
    return getComputedStyle(document.documentElement).getPropertyValue(varName).trim();
}

// --- P5.js Structure ---

function setup() {
    p5CanvasBackgroundColor = color(getCSSVariable('--background-color')); // Initialize with theme color
    let cnv = createCanvas(windowWidth, windowHeight); // Use P5.js windowWidth and windowHeight
    cnv.parent('canvas-parent'); // Tell P5 to put canvas in this div
    console.log("P5.js setup complete. Canvas created in #canvas-parent at window dimensions:", windowWidth, "x", windowHeight);
    
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
    if (p5CanvasBackgroundColor) {
        background(p5CanvasBackgroundColor);
    } else {
        // Fallback if somehow not set, though setup() should handle it.
        background(245, 248, 250); 
    }

    // --- OR a simple linear gradient example (comment out the solid background above if using this) ---
    // for (let i = 0; i < height; i++) {
    //   let inter = map(i, 0, height, 0, 1);
    //   let c = lerpColor(color(230, 235, 240), color(250, 255, 255), inter); // Light gray to off-white
    //   stroke(c);
    //   line(0, i, width, i);
    // }
    // noStroke(); // Reset stroke after gradient
    // --- End gradient example ---

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

    let p5DrawSize = creature.size;

    // Common modes, can be set once if all use CENTER, or per shape
    rectMode(CENTER);
    ellipseMode(CENTER);

    switch (creature.type) {
        case 'Plant':
            fill(100, 200, 100);      // Medium green body
            stroke(50, 150, 50);      // Darker green stroke
            strokeWeight(Math.max(1, p5DrawSize * 0.05)); // Proportional stroke, min 1px
            rect(creature.x, creature.y, p5DrawSize, p5DrawSize, p5DrawSize * 0.2); // Rounded corners

            // Optional detail: darker green circle
            noStroke(); // No stroke for the detail
            fill(50, 150, 50, 180); // Darker green, slightly transparent
            ellipse(creature.x, creature.y, p5DrawSize * 0.3, p5DrawSize * 0.3);
            break;

        case 'Herbivore':
            fill(100, 150, 255); // Softer blue
            noStroke();
            ellipse(creature.x, creature.y, p5DrawSize, p5DrawSize * 0.8); // Squashed ellipse

            // Optional detail: eye
            fill(255); // White eye
            // Simple eye: ensure it scales with p5DrawSize and is positioned relative to center
            // For a more dynamic eye based on direction:
            // let eyeOffsetX = p5DrawSize * 0.15 * (creature.dx / (abs(creature.dx) + abs(creature.dy) || 1));
            // let eyeOffsetY = p5DrawSize * 0.15 * (creature.dy / (abs(creature.dx) + abs(creature.dy) || 1));
            // ellipse(creature.x + eyeOffsetX, creature.y + eyeOffsetY, p5DrawSize * 0.15, p5DrawSize * 0.2);
            // Simplified static eye:
            ellipse(creature.x + p5DrawSize * 0.15, creature.y - p5DrawSize * 0.1, p5DrawSize * 0.12, p5DrawSize * 0.18);
            break;

        case 'Carnivore':
            let carnivoreBodyColor = color(200, 50, 50); // Nuanced red
            let carnivoreEarColor = color(150, 30, 30, 230); // Darker shade for ears, slightly transparent
            
            noStroke();
            
            // Body
            fill(carnivoreBodyColor);
            ellipse(creature.x, creature.y, p5DrawSize, p5DrawSize); // Main body ellipse

            // Simpler ears on top, slightly offset towards top.
            // For orientation with movement, push/translate/rotate/pop would be needed around this block.
            fill(carnivoreEarColor);
            let earSize = p5DrawSize * 0.4;
            let earOffset = p5DrawSize * 0.3; // How far from center the ears are placed
            
            // Simple ears without rotation for now
            ellipse(creature.x - earOffset, creature.y - earOffset, earSize, earSize);
            ellipse(creature.x + earOffset, creature.y - earOffset, earSize, earSize);
            break;
            
        default:
            // Existing default logic for unknown types or base "Creature"
            let defaultColorValue;
            if (creature.color && typeof creature.color === 'string' && creature.color.startsWith('#')) {
                try {
                    defaultColorValue = color(creature.color); 
                } catch (e) {
                    defaultColorValue = color(128); // Grey if hex is invalid
                }
            } else {
                defaultColorValue = color(128); // Grey
            }
            fill(defaultColorValue);
            noStroke(); 
            rectMode(CENTER); // Ensure rect mode is center for default
            rect(creature.x, creature.y, p5DrawSize, p5DrawSize); // Default to a square
            break;
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

    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const body = document.body;
    const currentThemeKey = 'themePreference';

    // Function to apply theme and update button
    const applyTheme = (theme) => {
        if (theme === 'dark') {
            body.classList.add('dark-mode');
            themeToggleBtn.textContent = 'Switch to Light Mode';
        } else {
            body.classList.remove('dark-mode');
            themeToggleBtn.textContent = 'Switch to Dark Mode';
        }
        // Update P5.js canvas background after DOM update
        // Use requestAnimationFrame to ensure styles are applied before reading them
        requestAnimationFrame(() => {
            p5CanvasBackgroundColor = color(getCSSVariable('--background-color'));
            console.log('P5.js canvas background updated to:', p5CanvasBackgroundColor.toString());
        });
    };

    // Load saved theme preference
    const savedTheme = localStorage.getItem(currentThemeKey);
    // Apply initial theme (this will also set the initial canvas background via the updated applyTheme)
    if (savedTheme) {
        applyTheme(savedTheme);
    } else {
        // Default to light as per initial CSS setup
        applyTheme('light'); 
    }
    // p5CanvasBackgroundColor will be set by P5.js setup() using the theme applied above,
    // and subsequent changes are handled by the themeToggleBtn listener.

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            let newTheme;
            if (body.classList.contains('dark-mode')) {
                newTheme = 'light';
            } else {
                newTheme = 'dark';
            }
            applyTheme(newTheme);
            localStorage.setItem(currentThemeKey, newTheme);
            console.log(`Theme switched to ${newTheme}. Preference saved.`);
        });
    } else {
        console.warn('Theme toggle button #themeToggleBtn not found.');
    }
});
