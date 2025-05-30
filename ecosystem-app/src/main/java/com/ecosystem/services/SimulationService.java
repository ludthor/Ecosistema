package com.ecosystem.services;

import com.ecosystem.model.creatures.Creature;
import com.ecosystem.model.creatures.Plant;
import com.ecosystem.model.creatures.Herbivore;
import com.ecosystem.model.creatures.Carnivore;
import com.ecosystem.model.environment.Territory;
import com.ecosystem.model.environment.TPlace;
import com.ecosystem.model.enums.Gender;
import com.ecosystem.utils.EcoUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SimulationService {
    // Use CopyOnWriteArrayList for thread-safe reads in controller while updates happen in scheduled task
    private List<Creature> creatures = new CopyOnWriteArrayList<>();
    private Territory territory;
    private final int canvasWorldWidth; // Renamed to reflect it's the canvas/conceptual size
    private final int canvasWorldHeight; // Renamed to reflect it's the canvas/conceptual size
    private final float effectiveSimWidth; // Actual simulation width based on territory
    private final float effectiveSimHeight; // Actual simulation height based on territory
    private final int numPlants = 25;
    private final int numHerbivores = 15;
    private final int numCarnivores = 10;
    private final int territoryGridWidth = 10; // Example: 10x10 grid for TPlaces
    private final int territoryGridHeight = 10;
    private final int placeSize; // Size of each TPlace in pixels

    private static Random random = new Random();

    // Default constructor for Spring, world size can be configured via properties or setters if needed
    public SimulationService() {
        this.canvasWorldWidth = 800; // Default or load from config
        this.canvasWorldHeight = 600; // Default or load from config
        this.placeSize = Math.min(canvasWorldWidth / territoryGridWidth, canvasWorldHeight / territoryGridHeight);
        this.territory = new Territory(territoryGridWidth, territoryGridHeight, placeSize);
        this.effectiveSimWidth = this.territory.getWidth() * this.territory.getPlaceSize();
        this.effectiveSimHeight = this.territory.getHeight() * this.territory.getPlaceSize();
        // creatures list is already initialized as CopyOnWriteArrayList
        initializeCreatures();
    }

    // Constructor that allows setting world size, useful for testing or specific configurations
    public SimulationService(int canvasWorldWidth, int canvasWorldHeight) {
        this.canvasWorldWidth = canvasWorldWidth;
        this.canvasWorldHeight = canvasWorldHeight;
        this.placeSize = Math.min(this.canvasWorldWidth / territoryGridWidth, this.canvasWorldHeight / territoryGridHeight);
        // creatures list is already initialized as CopyOnWriteArrayList by class member initialization
        this.territory = new Territory(territoryGridWidth, territoryGridHeight, placeSize);
        this.effectiveSimWidth = this.territory.getWidth() * this.territory.getPlaceSize();
        this.effectiveSimHeight = this.territory.getHeight() * this.territory.getPlaceSize();
        initializeCreatures();
    }

    private void initializeCreatures() {
        creatures.clear(); // Clear any existing creatures

        // Initialize Plants
        for (int i = 0; i < numPlants; i++) {
            creatures.add(new Plant(
                effectiveSimWidth, effectiveSimHeight, // Use effective simulation dimensions
                10 + random.nextInt(5),  // size (10-14)
                true,                    // toroidal
                600 + random.nextInt(401) // maxAge (600-1000)
            ));
        }

        // Initialize Herbivores
        for (int i = 0; i < numHerbivores; i++) {
            creatures.add(new Herbivore(
                effectiveSimWidth, effectiveSimHeight,     // Use effective simulation dimensions
                0.5f + random.nextFloat(),   // speed (0.5-1.5)
                12 + random.nextInt(7),      // size (12-18)
                true,                        // toroidal
                (float) Math.toRadians(20 + random.nextInt(21)), // dirVariation (20-40 degrees)
                900 + random.nextInt(301),   // maxAge (900-1200)
                0.02f + random.nextFloat() * 0.08f, // mutationRate (0.02-0.1)
                1 + random.nextInt(2),       // maxOffspring (1-2)
                25 + random.nextFloat() * 25 // offspringEnergy (25-50)
            ));
        }

        // Initialize Carnivores
        for (int i = 0; i < numCarnivores; i++) {
            creatures.add(new Carnivore(
                effectiveSimWidth, effectiveSimHeight,     // Use effective simulation dimensions
                0.8f + random.nextFloat(),   // speed (0.8-1.8)
                15 + random.nextInt(9),      // size (15-23)
                true,                        // toroidal
                (float) Math.toRadians(15 + random.nextInt(16)), // dirVariation (15-30 degrees)
                1000 + random.nextInt(501),  // maxAge (1000-1500)
                0.03f + random.nextFloat() * 0.07f, // mutationRate (0.03-0.1)
                1 + random.nextInt(2),       // maxOffspring (1-2)
                30 + random.nextFloat() * 30 // offspringEnergy (30-60)
            ));
        }
        
        // After creating all creatures, ensure they are in the territory.
        // The runSimulationStep will also handle adding them to TPlaces.
        for (Creature creature : this.creatures) {
             if (creature.isAlive()) { // Should be true for new creatures
                territory.addCreature(creature); // Add to territory's internal list and grid
             }
        }
    }

    @Scheduled(fixedRate = 100) // Approx 10 FPS
    public void runSimulationStep() {
        // Create a temporary list from the thread-safe CopyOnWriteArrayList for safe iteration and modification
        List<Creature> currentCreaturesSnapshot = new ArrayList<>(this.creatures);
        List<Creature> nextStepCreatures = new ArrayList<>(); // To build the list for the next state
        List<Creature> newOffspring = new ArrayList<>();

        // 1. Move creatures
        for (Creature creature : currentCreaturesSnapshot) {
            if (creature.isAlive()) {
                // Pass canvasWorldWidth/Height, but Creature.move now uses its internal effective dimensions for wrapping
                creature.move((int)canvasWorldWidth, (int)canvasWorldHeight); 
            }
        }
        
        // 2. Update territory (clear and re-add based on new positions)
        // This ensures TPlace lists are correct for encounter solving.
        List<Creature> allTerritoryCreatures = territory.getAllCreatures();
        for(Creature c : allTerritoryCreatures) {
            territory.removeCreature(c); // Remove from TPlace lists
        }
        for (Creature creature : currentCreaturesSnapshot) {
            if (creature.isAlive()) {
                territory.addCreature(creature); // Re-add to TPlace lists based on new positions
            }
        }
        
        // 3. Solve encounters
        // Iterate through TPlaces after they've been updated with current creature positions
        for (int y = 0; y < territory.getHeight(); y++) {
            for (int x = 0; x < territory.getWidth(); x++) {
                TPlace place = territory.getPlace(x, y);
                if (place == null) continue;
                List<Creature> creaturesInPlace = new ArrayList<>(place.getCreatures()); // Iterate over a copy
                if (creaturesInPlace.size() < 2) continue;

                for (int i = 0; i < creaturesInPlace.size(); i++) {
                    for (int j = i + 1; j < creaturesInPlace.size(); j++) {
                        Creature c1 = creaturesInPlace.get(i);
                        Creature c2 = creaturesInPlace.get(j);
                        
                        // Check if creatures are still part of the main simulation snapshot and alive
                        if (currentCreaturesSnapshot.contains(c1) && c1.isAlive() &&
                            currentCreaturesSnapshot.contains(c2) && c2.isAlive()) {
                            
                            // Pass effectiveSimWidth/Height to solveEncounter if it needs world dimensions for spawning, etc.
                            // Creature.reproduce now uses the parent's effective dimensions.
                            List<Creature> c1Offspring = c1.solveEncounter(c2, (int)effectiveSimWidth, (int)effectiveSimHeight);
                            if (c1Offspring != null && !c1Offspring.isEmpty()) {
                                newOffspring.addAll(c1Offspring);
                            }
                            // Assuming solveEncounter may modify 'other' (c2), so we don't call c2.solveEncounter(c1)
                        }
                    }
                }
            }
        }
        
        // 4. Build the list for the next step
        for (Creature creature : currentCreaturesSnapshot) {
            if (creature.isAlive()) {
                nextStepCreatures.add(creature);
            } else {
                // If a creature died, ensure it's removed from territory grid if it was there
                territory.removeCreature(creature);
            }
        }
        nextStepCreatures.addAll(newOffspring); // Add all newly born creatures
        
        // 5. Update the main creatures list (CopyOnWriteArrayList)
        this.creatures = new CopyOnWriteArrayList<>(nextStepCreatures);
    }

    // Getter for creatures, primarily for observation or external use (e.g., API)
    public List<Creature> getCreatures() {
        // Return a snapshot for thread safety, as CopyOnWriteArrayList iterator is a snapshot.
        // The list itself is already thread-safe for reads.
        return new ArrayList<>(this.creatures); 
    }

    public Territory getTerritory() {
        return territory;
    }
    
    public int getCanvasWorldWidth() {
        return canvasWorldWidth;
    }

    public int getCanvasWorldHeight() {
        return canvasWorldHeight;
    }
    
    public float getEffectiveSimWidth(){
        return effectiveSimWidth;
    }

    public float getEffectiveSimHeight(){
        return effectiveSimHeight;
    }
}
