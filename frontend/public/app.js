// Old canvas and context setup - no longer needed with P5.js
// const canvas = document.getElementById('simulationCanvas'); 
// const ctx = canvas.getContext('2d'); 

// Old canvas dimension setting - P5.js handles this via createCanvas
// canvas.width = window.innerWidth; 
// canvas.height = window.innerHeight; 

const backendUrl = 'http://localhost:8080/api/simulation/state';
const metadataUrl = 'http://localhost:8080/api/simulation/metadata';
const resetUrl = 'http://localhost:8080/api/simulation/reset';
let creaturesCache = []; 
let metadataCache = null;
let selectedCreatureId = null;
let populationHistory = [];
const maxHistoryPoints = 50;
const snapshotPollIntervalMs = 120;

let previousCreaturesById = new Map();
let currentCreaturesById = new Map();
let lastSnapshotAt = 0;
let snapshotDeltaMs = snapshotPollIntervalMs;
let showBehaviorCues = true;
const behaviorToggleKey = 'l';

let p5CanvasBackgroundColor; // Global variable for P5.js canvas background
let habitatLayer = null;
let habitatLayerWidth = 0;
let habitatLayerHeight = 0;
let habitatLayerSeedKey = null;

// Helper function to read CSS variables
function getCSSVariable(varName) {
    return getComputedStyle(document.documentElement).getPropertyValue(varName).trim();
}

const infoPanelPlaceholderHtml = `<p class="info-panel-placeholder">Click on a creature to inspect its details. Use reset controls for reproducible runs.</p>`;
let infoPanelElement = null; // To cache the info panel DOM element

function updateInfoPanel(htmlContent) {
    if (!infoPanelElement) {
        infoPanelElement = document.getElementById('info-panel');
    }
    if (infoPanelElement) {
        infoPanelElement.innerHTML = htmlContent;
    } else {
        console.error("#info-panel not found.");
    }
}

function updateMetadataDisplay(metadata) {
    const metadataElement = document.getElementById('simulation-metadata');
    if (!metadataElement) return;

    if (!metadata) {
        metadataElement.textContent = 'Seed: — | Step: —';
        return;
    }

    const seedValue = metadata.seed === null || metadata.seed === undefined ? 'Random' : metadata.seed;
    const stepValue = metadata.step === null || metadata.step === undefined ? '—' : metadata.step;
    metadataElement.textContent = `Seed: ${seedValue} | Step: ${stepValue}`;
}

function normalizeStatusKey(status) {
    if (!status || typeof status !== 'string') return 'neutral';
    return status.toLowerCase().replace(/\s+/g, '-');
}

function getStatusCueColor(status) {
    const key = normalizeStatusKey(status);
    if (key.includes('foraging') || key.includes('seeking-food')) return [94, 195, 114, 170];
    if (key.includes('evading')) return [247, 194, 74, 180];
    if (key.includes('pursuing')) return [235, 92, 92, 185];
    if (key.includes('feeding')) return [255, 152, 93, 180];
    if (key.includes('mate-seeking')) return [188, 117, 255, 180];
    if (key.includes('growing')) return [114, 214, 132, 170];
    return [180, 198, 220, 120];
}

function buildBehaviorLegendHtml() {
    if (!showBehaviorCues) {
        return `<p class="behavior-legend-disabled">Behavior cues are hidden. Press <strong>${behaviorToggleKey.toUpperCase()}</strong> to show them.</p>`;
    }

    return `
        <div class="behavior-legend">
            <h4 class="behavior-legend-title">Behavior Legend (press ${behaviorToggleKey.toUpperCase()} to toggle)</h4>
            <div class="behavior-legend-grid">
                <span class="legend-item"><i class="legend-dot status-seeking-food"></i>Seeking food / Foraging</span>
                <span class="legend-item"><i class="legend-dot status-evading"></i>Evading</span>
                <span class="legend-item"><i class="legend-dot status-pursuing"></i>Pursuing / Feeding</span>
                <span class="legend-item"><i class="legend-dot status-mate-seeking"></i>Mate-seeking</span>
                <span class="legend-item"><i class="legend-dot status-growing"></i>Growing</span>
                <span class="legend-item"><i class="legend-dot status-roaming"></i>Roaming / Neutral</span>
            </div>
        </div>
    `;
}

function displayCreatureInfo(creature) {
    if (!creature) {
        updateInfoPanel(infoPanelPlaceholderHtml);
        return;
    }

    // --- Data Handling & Defaults ---
    const typeDisplay = creature.type || "Unknown";
    const idPrefix = creature.id ? String(creature.id).substring(0, 4) : "N/A";
    const nameDisplay = creature.name || `${typeDisplay} #${idPrefix}`;
    
    let iconClass = '';
    let iconText = typeDisplay.charAt(0).toUpperCase();
    switch (typeDisplay.toLowerCase()) {
        case 'plant':
            iconClass = 'plant'; // CSS uses .creature-icon-placeholder.plant, not just .plant
            iconText = 'P';
            break;
        case 'herbivore':
            iconClass = 'herbivore';
            iconText = 'H';
            break;
        case 'carnivore':
            iconClass = 'carnivore';
            iconClass = 'carnivore';
            iconText = 'C';
            break;
        // default: iconClass remains '', iconText is first letter.
    }
    // The creature-icon-placeholder class has a default background (plant).
    // Specific classes like 'herbivore', 'carnivore' override this.
    // If iconClass is '', it will use the default.

    const ageDisplay = creature.age !== undefined ? `${creature.age} cycles` : "N/A";
    
    let energyDisplay = "N/A";
    if (creature.energy !== undefined) {
        if (creature.maxEnergy !== undefined) {
            energyDisplay = `${creature.energy} / ${creature.maxEnergy}`;
        } else {
            energyDisplay = `${creature.energy}`;
        }
    }
    // Plants might not have energy in the same way, or it's managed differently.
    if (typeDisplay.toLowerCase() === 'plant') {
        energyDisplay = "N/A (Rooted)";
    }

    const statusDisplay = creature.status || (typeDisplay.toLowerCase() === 'plant' ? "Growing" : "Roaming");
    const statusKey = normalizeStatusKey(statusDisplay);
    const cueStateText = showBehaviorCues ? "ON" : "OFF";

    // --- HTML Generation ---
    const htmlContent = `
        <div class="creature-details-card">
            <div class="creature-icon-placeholder ${iconClass}">${iconText}</div>
            <div class="creature-info">
                <h4 class="creature-name">${nameDisplay}</h4>
                <p class="creature-attribute">Species: <span class="creature-species">${typeDisplay}</span></p>
                <p class="creature-attribute">ID: <span class="creature-id">${creature.id || "N/A"}</span></p>
                <p class="creature-attribute">Age: <span class="creature-age">${ageDisplay}</span></p>
                <p class="creature-attribute">Energy: <span class="creature-energy">${energyDisplay}</span></p>
                <p class="creature-attribute">Status: <span class="creature-status status-badge status-${statusKey}">${statusDisplay}</span></p>
                <p class="creature-attribute">Behavior Cues: <span class="creature-cues">${cueStateText} (press ${behaviorToggleKey.toUpperCase()})</span></p>
                <p class="creature-attribute">Position: <span class="creature-pos">X: ${creature.x.toFixed(0)}, Y: ${creature.y.toFixed(0)}</span></p>
            </div>
        </div>
    `;
    updateInfoPanel(htmlContent);
}

function buildOverviewPanelHtml() {
    const total = creaturesCache.length;
    const plants = creaturesCache.filter(c => c.type === 'Plant' && c.alive !== false).length;
    const herbivores = creaturesCache.filter(c => c.type === 'Herbivore' && c.alive !== false).length;
    const carnivores = creaturesCache.filter(c => c.type === 'Carnivore' && c.alive !== false).length;
    const step = metadataCache?.step ?? '—';
    const seed = (metadataCache?.seed ?? null) === null ? 'Random' : metadataCache.seed;
    const historyStartStep = populationHistory.length > 0 ? populationHistory[0].step : '—';
    const historyEndStep = populationHistory.length > 0 ? populationHistory[populationHistory.length - 1].step : '—';
    const totalTrend = buildSparklineSvg(populationHistory.map(point => point.total), 'total');
    const plantTrend = buildSparklineSvg(populationHistory.map(point => point.plants), 'plants');
    const herbTrend = buildSparklineSvg(populationHistory.map(point => point.herbivores), 'herbivores');
    const carnTrend = buildSparklineSvg(populationHistory.map(point => point.carnivores), 'carnivores');

    return `
        <div class="overview-card">
            <h3 class="overview-title">Simulation Overview</h3>
            <div class="overview-grid">
                <div class="overview-stat">
                    <span class="overview-label">Total</span>
                    <span class="overview-value">${total}</span>
                </div>
                <div class="overview-stat">
                    <span class="overview-label">Plants</span>
                    <span class="overview-value">${plants}</span>
                </div>
                <div class="overview-stat">
                    <span class="overview-label">Herbivores</span>
                    <span class="overview-value">${herbivores}</span>
                </div>
                <div class="overview-stat">
                    <span class="overview-label">Carnivores</span>
                    <span class="overview-value">${carnivores}</span>
                </div>
            </div>
            <div class="overview-meta">
                <span>Seed: <strong>${seed}</strong></span>
                <span>Step: <strong>${step}</strong></span>
            </div>
            <div class="timeline-card">
                <div class="timeline-header">
                    <span class="timeline-title">Recent Population Trend</span>
                    <span class="timeline-range">Steps ${historyStartStep} → ${historyEndStep}</span>
                </div>
                <div class="trend-row">
                    <span class="trend-label">Total</span>
                    ${totalTrend}
                </div>
                <div class="trend-row">
                    <span class="trend-label">Plants</span>
                    ${plantTrend}
                </div>
                <div class="trend-row">
                    <span class="trend-label">Herbivores</span>
                    ${herbTrend}
                </div>
                <div class="trend-row">
                    <span class="trend-label">Carnivores</span>
                    ${carnTrend}
                </div>
            </div>
            ${buildBehaviorLegendHtml()}
            <p class="overview-hint">Tip: enter a seed, click <strong>Reset</strong>, then run <strong>Replay Check</strong> to verify deterministic resets.</p>
        </div>
    `;
}

function buildSparklineSvg(values, seriesClass) {
    if (!values || values.length < 2) {
        return `<div class="sparkline-empty">Not enough data yet</div>`;
    }

    const width = 180;
    const height = 32;
    const min = Math.min(...values);
    const max = Math.max(...values);
    const range = Math.max(1, max - min);

    const points = values.map((value, index) => {
        const x = (index / (values.length - 1)) * (width - 2) + 1;
        const y = height - 1 - ((value - min) / range) * (height - 2);
        return `${x.toFixed(2)},${y.toFixed(2)}`;
    }).join(' ');

    return `
        <svg class="sparkline ${seriesClass}" viewBox="0 0 ${width} ${height}" preserveAspectRatio="none" aria-hidden="true">
            <polyline class="sparkline-line" points="${points}"></polyline>
        </svg>
    `;
}

function updatePopulationHistory(metadata) {
    if (!metadata || metadata.step === null || metadata.step === undefined) {
        return;
    }

    const parsedStep = Number(metadata.step);
    if (Number.isNaN(parsedStep)) {
        return;
    }

    const latest = populationHistory[populationHistory.length - 1];
    if (latest && latest.step === parsedStep) {
        latest.total = Number(metadata.totalCreatures ?? latest.total);
        latest.plants = Number(metadata.plants ?? latest.plants);
        latest.herbivores = Number(metadata.herbivores ?? latest.herbivores);
        latest.carnivores = Number(metadata.carnivores ?? latest.carnivores);
        return;
    }

    populationHistory.push({
        step: parsedStep,
        total: Number(metadata.totalCreatures ?? 0),
        plants: Number(metadata.plants ?? 0),
        herbivores: Number(metadata.herbivores ?? 0),
        carnivores: Number(metadata.carnivores ?? 0)
    });

    if (populationHistory.length > maxHistoryPoints) {
        populationHistory = populationHistory.slice(populationHistory.length - maxHistoryPoints);
    }
}

function refreshInfoPanelState() {
    if (selectedCreatureId === null || selectedCreatureId === undefined) {
        updateInfoPanel(buildOverviewPanelHtml());
        return;
    }

    const selectedCreature = creaturesCache.find(c => c.id === selectedCreatureId && c.alive !== false);
    if (selectedCreature) {
        displayCreatureInfo(selectedCreature);
    } else {
        selectedCreatureId = null;
        updateInfoPanel(buildOverviewPanelHtml());
    }
}

function updateReplayStatus(message, state = 'neutral') {
    const replayStatusElement = document.getElementById('replay-status');
    if (!replayStatusElement) return;

    replayStatusElement.textContent = `Replay Check: ${message}`;
    replayStatusElement.classList.remove('replay-pass', 'replay-fail');
    if (state === 'pass') replayStatusElement.classList.add('replay-pass');
    if (state === 'fail') replayStatusElement.classList.add('replay-fail');
}

function normalizeCreaturesForComparison(creatures) {
    if (!Array.isArray(creatures)) return [];
    return creatures.map(({ id, ...rest }) => rest);
}

function getSimulationWorldWidth() {
    const value = metadataCache?.effectiveSimWidth;
    if (typeof value === 'number' && value > 0) return value;
    return (typeof width === 'number' && width > 0) ? width : 1;
}

function getSimulationWorldHeight() {
    const value = metadataCache?.effectiveSimHeight;
    if (typeof value === 'number' && value > 0) return value;
    return (typeof height === 'number' && height > 0) ? height : 1;
}

function worldToScreenX(worldX) {
    return (worldX / getSimulationWorldWidth()) * width;
}

function worldToScreenY(worldY) {
    return (worldY / getSimulationWorldHeight()) * height;
}

function screenToWorldX(screenX) {
    return (screenX / Math.max(1, width)) * getSimulationWorldWidth();
}

function screenToWorldY(screenY) {
    return (screenY / Math.max(1, height)) * getSimulationWorldHeight();
}

function getWorldToScreenScale() {
    const sx = width / getSimulationWorldWidth();
    const sy = height / getSimulationWorldHeight();
    return (sx + sy) / 2;
}

function ensureHabitatLayer() {
    const activeSeed = metadataCache?.seed ?? 'random';
    const seedKey = `${activeSeed}|${width}x${height}`;
    if (habitatLayer && habitatLayerWidth === width && habitatLayerHeight === height && habitatLayerSeedKey === seedKey) {
        return;
    }

    habitatLayer = createGraphics(width, height);
    habitatLayerWidth = width;
    habitatLayerHeight = height;
    habitatLayerSeedKey = seedKey;

    habitatLayer.noStroke();

    const worldWidth = getSimulationWorldWidth();
    const worldHeight = getSimulationWorldHeight();
    const step = 14;
    const noiseScale = 0.045;

    for (let wy = 0; wy <= worldHeight; wy += step) {
        for (let wx = 0; wx <= worldWidth; wx += step) {
            const nx = wx * noiseScale;
            const ny = wy * noiseScale;
            const value = noise(nx, ny);

            let terrainColor;
            if (value < 0.34) {
                terrainColor = color(34, 70, 96, 70);
            } else if (value < 0.58) {
                terrainColor = color(58, 114, 82, 58);
            } else {
                terrainColor = color(34, 92, 62, 72);
            }

            habitatLayer.fill(terrainColor);
            habitatLayer.rect(
                worldToScreenX(wx),
                worldToScreenY(wy),
                Math.max(1, step * (width / worldWidth)),
                Math.max(1, step * (height / worldHeight))
            );
        }
    }

    for (let i = 0; i < 48; i++) {
        const wx = (i * 97) % Math.max(1, worldWidth);
        const wy = (i * 151) % Math.max(1, worldHeight);
        const screenX = worldToScreenX(wx);
        const screenY = worldToScreenY(wy);
        const blobW = (40 + (i % 7) * 18) * getWorldToScreenScale();
        const blobH = (24 + (i % 5) * 16) * getWorldToScreenScale();
        habitatLayer.fill(28, 70, 48, 44);
        habitatLayer.ellipse(screenX, screenY, blobW, blobH);
    }
}

function interpolateAxis(prevValue, currentValue, interpolationFactor, boundary, toroidal) {
    if (!toroidal || !boundary || boundary <= 0) {
        return prevValue + (currentValue - prevValue) * interpolationFactor;
    }

    let delta = currentValue - prevValue;
    if (Math.abs(delta) > boundary / 2) {
        delta = delta > 0 ? delta - boundary : delta + boundary;
    }

    let interpolated = prevValue + delta * interpolationFactor;
    while (interpolated < 0) interpolated += boundary;
    while (interpolated >= boundary) interpolated -= boundary;
    return interpolated;
}

function ingestCreatureSnapshot(creatures) {
    const now = (typeof performance !== 'undefined' && performance.now) ? performance.now() : Date.now();

    const nextMap = new Map();
    for (const creature of creatures) {
        if (creature && creature.id !== undefined && creature.id !== null) {
            nextMap.set(creature.id, creature);
        }
    }

    if (currentCreaturesById.size === 0) {
        previousCreaturesById = new Map(nextMap);
    } else {
        previousCreaturesById = new Map(currentCreaturesById);
    }

    currentCreaturesById = nextMap;
    creaturesCache = creatures;

    if (lastSnapshotAt > 0) {
        snapshotDeltaMs = Math.max(40, now - lastSnapshotAt);
    }
    lastSnapshotAt = now;
}

function getInterpolatedCreatures() {
    if (!Array.isArray(creaturesCache) || creaturesCache.length === 0) {
        return [];
    }

    const now = (typeof performance !== 'undefined' && performance.now) ? performance.now() : Date.now();
    const elapsed = Math.max(0, now - lastSnapshotAt);
    const interpolationFactor = Math.min(1, snapshotDeltaMs > 0 ? elapsed / snapshotDeltaMs : 1);

    return creaturesCache.map(creature => {
        const previous = previousCreaturesById.get(creature.id);
        if (!previous) {
            return creature;
        }

        const boundaryX = getSimulationWorldWidth();
        const boundaryY = getSimulationWorldHeight();

        return {
            ...creature,
            x: interpolateAxis(previous.x, creature.x, interpolationFactor, boundaryX, creature.toroidal),
            y: interpolateAxis(previous.y, creature.y, interpolationFactor, boundaryY, creature.toroidal),
            direction: previous.direction + (creature.direction - previous.direction) * interpolationFactor
        };
    });
}

// --- P5.js Structure ---

let canvasParentRef = null; // To cache #canvas-parent element

function resizeP5Canvas() {
    if (!canvasParentRef) {
        canvasParentRef = document.getElementById('canvas-parent');
    }
    if (canvasParentRef) {
        // Ensure canvas parent has non-zero dimensions before resizing.
        // This can be an issue if CSS isn't fully applied or element is hidden.
        if (canvasParentRef.offsetWidth > 0 && canvasParentRef.offsetHeight > 0) {
            resizeCanvas(canvasParentRef.offsetWidth, canvasParentRef.offsetHeight);
            console.log("P5.js canvas resized to parent:", canvasParentRef.offsetWidth, "x", canvasParentRef.offsetHeight);
        } else {
            console.warn("#canvas-parent has zero dimensions. Canvas not resized. CSS ensure it's visible and has size.");
        }
    } else {
        console.warn("#canvas-parent not found for resizing P5 canvas.");
    }
}

function setup() {
    p5CanvasBackgroundColor = color(getCSSVariable('--background-color')); // Initialize with theme color
    
    canvasParentRef = document.getElementById('canvas-parent');
    if (!canvasParentRef) {
        console.error("CRITICAL: #canvas-parent div not found. P5.js canvas cannot be created.");
        // Fallback or error state if #canvas-parent is missing
        let cnv = createCanvas(100, 100); // Create a tiny fallback canvas
        cnv.parent(document.body); // Attach it somewhere to avoid breaking P5 flow
        return; // Stop further setup for canvas
    }

    const parentWidth = canvasParentRef.offsetWidth;
    const parentHeight = canvasParentRef.offsetHeight;

    if (parentWidth === 0 || parentHeight === 0) {
        console.warn("Warning: #canvas-parent has zero dimensions during setup. Canvas might not be visible. Check CSS.");
    }

    let cnv = createCanvas(parentWidth, parentHeight);
    cnv.parent(canvasParentRef); // Use the cached reference
    console.log("P5.js setup complete. Canvas created in #canvas-parent with dimensions:", parentWidth, "x", parentHeight);
    
    // Initial fetch of creatures + metadata
    Promise.all([fetchCreatures(), fetchSimulationMetadata()]).then(([creatures, metadata]) => {
        ingestCreatureSnapshot(creatures || []);
        metadataCache = metadata;
        updateMetadataDisplay(metadataCache);
        updatePopulationHistory(metadataCache);
        refreshInfoPanelState();
        console.log('Initial creatures fetched in setup:', creaturesCache.length);
    }).catch(error => {
        console.error("Error during initial fetch in setup:", error);
        updateMetadataDisplay(null);
        refreshInfoPanelState();
    });

    // Periodic refresh of creature data
    setInterval(async () => {
        try {
            const [creatures, metadata] = await Promise.all([fetchCreatures(), fetchSimulationMetadata()]);
            ingestCreatureSnapshot(creatures || []);
            metadataCache = metadata;
            updateMetadataDisplay(metadataCache);
            updatePopulationHistory(metadataCache);
            refreshInfoPanelState();
            // console.log('Creatures cache updated:', creaturesCache.length); // Can be noisy
        } catch (error) {
            console.error("Error during periodic fetch:", error);
            updateMetadataDisplay(null);
            refreshInfoPanelState();
        }
    }, snapshotPollIntervalMs);
    
    console.log('P5.js: setup() finished.');

    // Setup ResizeObserver if available
    if (typeof ResizeObserver !== 'undefined') {
        const resizeObserver = new ResizeObserver(entries => {
            // We are observing only one element.
            // A brief delay can sometimes help if the resize is rapid or involves CSS transitions.
            // requestAnimationFrame is a good way to sync with browser's repaint cycle.
            requestAnimationFrame(() => {
                console.log('ResizeObserver detected #canvas-parent resize.');
                resizeP5Canvas();
            });
        });
        resizeObserver.observe(canvasParentRef); // Observe the cached element
    } else {
        console.warn('ResizeObserver not supported. Canvas resize will only occur on window resize.');
    }
}

function windowResized() {
    // This is still useful as a fallback or for initial sizing on some browsers,
    // and when ResizeObserver is not available.
    console.log("windowResized event triggered.");
    resizeP5Canvas();
}

function draw() {
    drawEnvironmentBackground();

    const renderCreatures = getInterpolatedCreatures();
    if (renderCreatures.length > 0) {
        for (let creature of renderCreatures) {
            drawP5Creature(creature); 
        }
    }
    
    // console.log('P5.js draw() loop running'); // Can be very noisy
}

function drawEnvironmentBackground() {
    push();
    rectMode(CORNER);
    ellipseMode(CENTER);
    noStroke();

    const skyTop = color(24, 48, 80);
    const skyBottom = color(52, 102, 84);
    for (let y = 0; y < height; y += 2) {
        const blend = y / Math.max(1, height - 1);
        const c = lerpColor(skyTop, skyBottom, blend);
        fill(c);
        rect(0, y, width, 2);
    }

    ensureHabitatLayer();
    if (habitatLayer) {
        image(habitatLayer, 0, 0, width, height);
    }

    const worldWidth = getSimulationWorldWidth();
    const worldHeight = getSimulationWorldHeight();

    stroke(255, 255, 255, 12);
    strokeWeight(1);
    for (let x = 0; x <= worldWidth; x += 60) {
        const sx = worldToScreenX(x);
        line(sx, 0, sx, height);
    }
    for (let y = 0; y <= worldHeight; y += 60) {
        const sy = worldToScreenY(y);
        line(0, sy, width, sy);
    }
    noStroke();
    pop();
}

function mousePressed() {
    if (!creaturesCache || creaturesCache.length === 0) {
        return;
    }

    let creatureClicked = null;
    // Iterate backwards to select the topmost creature if they overlap
    for (let i = creaturesCache.length - 1; i >= 0; i--) {
        const creature = creaturesCache[i];
        // Using dist() from P5.js for click detection within circular bounds
        // Make sure mouseX and mouseY are defined (i.e., click is within canvas)
        if (typeof mouseX !== 'undefined' && typeof mouseY !== 'undefined') {
            const creatureScreenX = worldToScreenX(creature.x);
            const creatureScreenY = worldToScreenY(creature.y);
            const creatureScreenSize = creature.size * getWorldToScreenScale();
            let d = dist(mouseX, mouseY, creatureScreenX, creatureScreenY);
            if (d < creatureScreenSize / 2) {
                creatureClicked = creature;
                break; 
            }
        }
    }

    if (creatureClicked) {
        selectedCreatureId = creatureClicked.id;
        displayCreatureInfo(creatureClicked);
    } else {
        // Clicked on empty space
        selectedCreatureId = null;
        updateInfoPanel(buildOverviewPanelHtml());
    }
}

// --- End P5.js Structure ---

function drawP5Creature(creature) {
    if (!creature || typeof creature.x !== 'number' || typeof creature.y !== 'number' || typeof creature.size !== 'number') {
        // console.warn("Invalid creature data for P5 drawing:", creature);
        return;
    }

    const screenX = worldToScreenX(creature.x);
    const screenY = worldToScreenY(creature.y);
    const pulse = 1 + Math.sin(frameCount * 0.08 + creature.id) * 0.035;
    let p5DrawSize = creature.size * getWorldToScreenScale() * pulse;
    const heading = (typeof creature.direction === 'number') ? creature.direction : 0;

    noStroke();
    fill(0, 0, 0, 28);
    ellipse(screenX, screenY + p5DrawSize * 0.36, p5DrawSize * 0.9, p5DrawSize * 0.35);

    if (showBehaviorCues) {
        const statusColor = getStatusCueColor(creature.status);
        noFill();
        stroke(statusColor[0], statusColor[1], statusColor[2], statusColor[3]);
        strokeWeight(Math.max(1, p5DrawSize * 0.055));
        ellipse(screenX, screenY, p5DrawSize * 1.25, p5DrawSize * 1.25);
    }

    // Common modes, can be set once if all use CENTER, or per shape
    rectMode(CENTER);
    ellipseMode(CENTER);

    switch (creature.type) {
        case 'Plant':
            push();
            translate(screenX, screenY);
            const sway = Math.sin(frameCount * 0.05 + creature.id) * 0.08;
            rotate(sway);
            stroke(40, 110, 62, 220);
            strokeWeight(Math.max(1, p5DrawSize * 0.08));
            line(0, p5DrawSize * 0.3, 0, -p5DrawSize * 0.25);
            noStroke();
            fill(86, 173, 94, 230);
            ellipse(0, -p5DrawSize * 0.22, p5DrawSize * 0.68, p5DrawSize * 0.48);
            fill(112, 198, 120, 210);
            ellipse(0, -p5DrawSize * 0.12, p5DrawSize * 0.46, p5DrawSize * 0.34);
            pop();
            break;

        case 'Herbivore':
            push();
            translate(screenX, screenY);
            rotate(heading);
            noStroke();
            fill(112, 178, 255, 235);
            ellipse(0, 0, p5DrawSize * 1.05, p5DrawSize * 0.72);
            fill(90, 152, 226, 240);
            ellipse(p5DrawSize * 0.28, 0, p5DrawSize * 0.52, p5DrawSize * 0.44);

            fill(255, 255, 255, 240);
            ellipse(p5DrawSize * 0.3, -p5DrawSize * 0.1, p5DrawSize * 0.13, p5DrawSize * 0.13);
            fill(20, 40, 70, 220);
            ellipse(p5DrawSize * 0.32, -p5DrawSize * 0.1, p5DrawSize * 0.06, p5DrawSize * 0.06);

            stroke(90, 152, 226, 180);
            strokeWeight(Math.max(1, p5DrawSize * 0.06));
            line(-p5DrawSize * 0.5, -p5DrawSize * 0.1, -p5DrawSize * 0.72, -p5DrawSize * 0.24);
            line(-p5DrawSize * 0.5, p5DrawSize * 0.1, -p5DrawSize * 0.72, p5DrawSize * 0.24);
            pop();
            break;

        case 'Carnivore':
            push();
            translate(screenX, screenY);
            rotate(heading);
            noStroke();

            fill(214, 74, 74, 235);
            ellipse(0, 0, p5DrawSize * 1.12, p5DrawSize * 0.76);
            fill(170, 44, 44, 240);
            ellipse(p5DrawSize * 0.26, 0, p5DrawSize * 0.56, p5DrawSize * 0.48);

            fill(190, 50, 50, 230);
            triangle(
                -p5DrawSize * 0.16, -p5DrawSize * 0.28,
                -p5DrawSize * 0.02, -p5DrawSize * 0.56,
                p5DrawSize * 0.08, -p5DrawSize * 0.22
            );
            triangle(
                -p5DrawSize * 0.16, p5DrawSize * 0.28,
                -p5DrawSize * 0.02, p5DrawSize * 0.56,
                p5DrawSize * 0.08, p5DrawSize * 0.22
            );

            fill(255, 245, 245, 240);
            ellipse(p5DrawSize * 0.32, -p5DrawSize * 0.1, p5DrawSize * 0.12, p5DrawSize * 0.12);
            fill(40, 12, 12, 220);
            ellipse(p5DrawSize * 0.34, -p5DrawSize * 0.1, p5DrawSize * 0.06, p5DrawSize * 0.06);

            stroke(255, 220, 220, 180);
            strokeWeight(Math.max(1, p5DrawSize * 0.05));
            line(p5DrawSize * 0.44, -p5DrawSize * 0.06, p5DrawSize * 0.58, -p5DrawSize * 0.02);
            line(p5DrawSize * 0.44, p5DrawSize * 0.06, p5DrawSize * 0.58, p5DrawSize * 0.02);
            pop();
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
            rect(screenX, screenY, p5DrawSize, p5DrawSize); // Default to a square
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

async function fetchSimulationMetadata() {
    try {
        const response = await fetch(metadataUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Could not fetch simulation metadata:', error);
        return null;
    }
}

async function resetSimulation(seed) {
    try {
        let url = resetUrl;
        if (seed !== null && seed !== undefined && seed !== '') {
            const encodedSeed = encodeURIComponent(seed);
            url = `${resetUrl}?seed=${encodedSeed}`;
        }

        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const creatures = await response.json();
        ingestCreatureSnapshot(Array.isArray(creatures) ? creatures : []);
        metadataCache = await fetchSimulationMetadata();
        updateMetadataDisplay(metadataCache);
        if (metadataCache && Number(metadataCache.step) === 0) {
            populationHistory = [];
        }
        updatePopulationHistory(metadataCache);
        refreshInfoPanelState();
        return true;
    } catch (error) {
        console.error('Could not reset simulation:', error);
        return false;
    }
}

async function runReplayCheck(seed) {
    if (seed === null || seed === undefined || String(seed).trim() === '') {
        updateReplayStatus('Provide a seed first', 'fail');
        return false;
    }

    updateReplayStatus('Running...');

    let firstPopulation = null;
    let secondPopulation = null;

    try {
        const firstResponse = await fetch(`${resetUrl}?seed=${encodeURIComponent(seed)}`, { method: 'POST' });
        if (!firstResponse.ok) throw new Error(`First reset failed with ${firstResponse.status}`);
        firstPopulation = normalizeCreaturesForComparison(await firstResponse.json());

        const secondResponse = await fetch(`${resetUrl}?seed=${encodeURIComponent(seed)}`, { method: 'POST' });
        if (!secondResponse.ok) throw new Error(`Second reset failed with ${secondResponse.status}`);
        const secondPayload = await secondResponse.json();
        secondPopulation = normalizeCreaturesForComparison(secondPayload);
        ingestCreatureSnapshot(Array.isArray(secondPayload) ? secondPayload : []);
        metadataCache = await fetchSimulationMetadata();
        updateMetadataDisplay(metadataCache);
        if (metadataCache && Number(metadataCache.step) === 0) {
            populationHistory = [];
        }
        updatePopulationHistory(metadataCache);
        refreshInfoPanelState();

        const isDeterministic = JSON.stringify(firstPopulation) === JSON.stringify(secondPopulation);
        updateReplayStatus(isDeterministic ? 'PASS' : 'FAIL', isDeterministic ? 'pass' : 'fail');
        return isDeterministic;
    } catch (error) {
        console.error('Replay check failed:', error);
        updateReplayStatus('ERROR', 'fail');
        return false;
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
    const resetWithSeedBtn = document.getElementById('resetWithSeedBtn');
    const resetRandomBtn = document.getElementById('resetRandomBtn');
    const replayCheckBtn = document.getElementById('replayCheckBtn');
    const seedInput = document.getElementById('seedInput');
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
    
    infoPanelElement = document.getElementById('info-panel'); // Cache info panel element
    updateInfoPanel(buildOverviewPanelHtml());
    updateMetadataDisplay(metadataCache);
    updateReplayStatus('—');

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

    if (resetWithSeedBtn && seedInput) {
        resetWithSeedBtn.addEventListener('click', async () => {
            const seedValue = seedInput.value.trim();
            updateReplayStatus('—');
            resetWithSeedBtn.disabled = true;
            const success = await resetSimulation(seedValue);
            resetWithSeedBtn.disabled = false;
            if (!success) {
                console.error('Seeded reset failed.');
            }
        });
    } else {
        console.warn('Seeded reset controls not found.');
    }

    if (resetRandomBtn) {
        resetRandomBtn.addEventListener('click', async () => {
            updateReplayStatus('—');
            resetRandomBtn.disabled = true;
            const success = await resetSimulation(null);
            resetRandomBtn.disabled = false;
            if (!success) {
                console.error('Random reset failed.');
            }
        });
    } else {
        console.warn('Random reset button #resetRandomBtn not found.');
    }

    if (replayCheckBtn && seedInput) {
        replayCheckBtn.addEventListener('click', async () => {
            replayCheckBtn.disabled = true;
            await runReplayCheck(seedInput.value.trim());
            replayCheckBtn.disabled = false;
        });
    } else {
        console.warn('Replay check controls not found.');
    }

    document.addEventListener('keydown', (event) => {
        const target = event.target;
        const isTextInput = target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA');
        if (isTextInput || event.metaKey || event.ctrlKey || event.altKey) return;

        if (event.key && event.key.toLowerCase() === behaviorToggleKey) {
            showBehaviorCues = !showBehaviorCues;
            refreshInfoPanelState();
        }
    });
});
