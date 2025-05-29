package com.ecosystem.services;

import com.ecosystem.model.creatures.*;
import com.ecosystem.model.environment.Territory;
import com.ecosystem.model.environment.TPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

public class SimulationServiceTest {

    private SimulationService simulationService;
    private final int worldWidth = 200; // Smaller world for easier testing
    private final int worldHeight = 200;

    @BeforeEach
    void setUp() {
        // Use the constructor that allows setting world size
        simulationService = new SimulationService(worldWidth, worldHeight);
        // Note: initializeCreatures is called by the constructor.
    }

    @Test
    void initializeCreatures_createsCorrectMixAndNumberOfCreatures() {
        List<Creature> creatures = simulationService.getCreatures();
        // Default numbers: Plants=25, Herbivores=15, Carnivores=10
        long plantCount = creatures.stream().filter(c -> c instanceof Plant).count();
        long herbivoreCount = creatures.stream().filter(c -> c instanceof Herbivore).count();
        long carnivoreCount = creatures.stream().filter(c -> c instanceof Carnivore).count();

        assertEquals(25, plantCount, "Should initialize 25 plants.");
        assertEquals(15, herbivoreCount, "Should initialize 15 herbivores.");
        assertEquals(10, carnivoreCount, "Should initialize 10 carnivores.");
        assertEquals(25 + 15 + 10, creatures.size(), "Total number of creatures should be correct.");

        // Check if creatures are within bounds (basic check, more detailed in CreatureTest)
        for (Creature c : creatures) {
            assertTrue(c.getX() >= 0 && c.getX() <= worldWidth, "Creature X out of bounds.");
            assertTrue(c.getY() >= 0 && c.getY() <= worldHeight, "Creature Y out of bounds.");
        }
    }

    @Test
    void runSimulationStep_updatesCreatureStates() {
        // This test is high-level. It checks if *something* happens.
        // More specific interactions are tested below.
        List<Creature> initialCreatures = simulationService.getCreatures();
        if (initialCreatures.isEmpty()) {
            // If initializeCreatures somehow didn't run or created no creatures
            simulationService.initializeCreatures(); // Explicitly call if needed, though constructor should handle it
            initialCreatures = simulationService.getCreatures();
            if (initialCreatures.isEmpty()) fail("Failed to initialize creatures for test.");
        }

        // Capture initial state of a few creatures (e.g., first herbivore and carnivore)
        Creature firstHerbivore = initialCreatures.stream().filter(c -> c instanceof Herbivore).findFirst().orElse(null);
        Creature firstCarnivore = initialCreatures.stream().filter(c -> c instanceof Carnivore).findFirst().orElse(null);

        assertNotNull(firstHerbivore, "Test requires at least one Herbivore to be initialized.");
        assertNotNull(firstCarnivore, "Test requires at least one Carnivore to be initialized.");

        float herbivoreInitialX = firstHerbivore.getX();
        float herbivoreInitialEnergy = firstHerbivore.getEnergy();
        float carnivoreInitialX = firstCarnivore.getX();
        float carnivoreInitialEnergy = firstCarnivore.getEnergy();

        simulationService.runSimulationStep(); // Run one step

        // Check if states have changed (position, energy, etc.)
        // Note: Exact changes are hard to predict due to randomness in movement and interactions.
        // We're looking for *any* change as an indicator the simulation step ran.
        assertTrue(firstHerbivore.getX() != herbivoreInitialX || firstHerbivore.getEnergy() < herbivoreInitialEnergy || firstHerbivore.getAge() > 0,
                   "Herbivore state should change after simulation step.");
        assertTrue(firstCarnivore.getX() != carnivoreInitialX || firstCarnivore.getEnergy() < carnivoreInitialEnergy || firstCarnivore.getAge() > 0,
                   "Carnivore state should change after simulation step.");
        
        // Also check if dead creatures are removed and offspring might be added
        int creatureCountAfterStep = simulationService.getCreatures().size();
        // Count can go up (reproduction) or down (death). It's unlikely to be exactly the same
        // unless no deaths and no reproductions occurred.
        // This is a weak assertion but can catch major issues.
        // assertTrue(initialCreatures.size() != creatureCountAfterStep || creatureCountAfterStep > 0, "Creature count might change.");
    }
    
    @Test
    void checkTerritory_updatesTerritoryCorrectly() {
        // initializeCreatures already populates the service's creature list and calls territory.addCreature for each.
        // The runSimulationStep also updates territory.
        // This test will verify that creatures are indeed in the territory grid.
        Territory territory = simulationService.getTerritory();
        simulationService.runSimulationStep(); // Ensure territory grid is populated based on current positions

        List<Creature> serviceCreatures = simulationService.getCreatures();
        if (serviceCreatures.isEmpty()) fail("No creatures in service to test territory with.");

        boolean foundInGrid = false;
        Creature firstLiveCreature = serviceCreatures.stream().filter(Creature::isAlive).findFirst().orElse(null);
        if (firstLiveCreature == null && !serviceCreatures.isEmpty()) {
             // if all creatures somehow died in first step, re-init for this test's purpose
            simulationService.initializeCreatures(); 
            simulationService.runSimulationStep();
            serviceCreatures = simulationService.getCreatures();
            firstLiveCreature = serviceCreatures.stream().filter(Creature::isAlive).findFirst().orElse(null);
        }
        
        assertNotNull(firstLiveCreature, "Need at least one live creature for this test.");

        int gridX = (int) (firstLiveCreature.getX() / territory.getPlaceSize());
        int gridY = (int) (firstLiveCreature.getY() / territory.getPlaceSize());
        
        // Clamp gridX and gridY to be within territory bounds, similar to Territory.java logic
        gridX = Math.max(0, Math.min(gridX, territory.getWidth() - 1));
        gridY = Math.max(0, Math.min(gridY, territory.getHeight() - 1));

        TPlace place = territory.getPlace(gridX, gridY);
        assertNotNull(place, "TPlace should exist for creature's coordinates.");
        
        if (place.getCreatures().contains(firstLiveCreature)) {
            foundInGrid = true;
        }

        assertTrue(foundInGrid, "A creature from the service should be found in the correct TPlace in the territory grid.");
    }


    @Test
    void solveEncounters_plantHerbivoreInteraction() {
        // Clear existing creatures and set up a specific scenario
        simulationService.getCreatures().clear(); // Clear creatures from SimulationService list
        // Manually create and add one plant and one herbivore very close to each other
        Plant testPlant = new Plant(10, 10, 10, true, 500);
        Herbivore testHerbivore = new Herbivore("TestEater", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        testHerbivore.setEnergy(30); // Make herbivore hungry

        // Add to simulation service's list (which is now CopyOnWriteArrayList)
        List<Creature> testList = new java.util.concurrent.CopyOnWriteArrayList<>();
        testList.add(testPlant);
        testList.add(testHerbivore);
        
        // Manually set the internal list of creatures in SimulationService for this test
        // This is a bit of a hack, ideally we'd have a method in SimulationService to set creatures for testing
        // For now, we'll rely on the fact that runSimulationStep uses this.creatures
        // Need to reflect this change in the private field, which is tricky without a setter or reflection.
        // So, instead, let's re-initialize and then filter/manipulate.
        simulationService.initializeCreatures(); // Re-init to get a fresh list
        simulationService.getCreatures().clear(); // Clear it again
        simulationService.getCreatures().add(testPlant);
        simulationService.getCreatures().add(testHerbivore);
        
        float initialPlantHealth = testPlant.getHealth();
        float initialHerbivoreEnergy = testHerbivore.getEnergy();

        simulationService.runSimulationStep(); // This will call internal methods including those that lead to solveEncounters

        assertTrue(testPlant.getHealth() < initialPlantHealth || !testPlant.isAlive(), "Plant should be eaten (health reduced or died).");
        assertTrue(testHerbivore.getEnergy() > initialHerbivoreEnergy, "Herbivore energy should increase after eating.");
    }

    @Test
    void solveEncounters_carnivoreHerbivoreInteraction() {
        simulationService.getCreatures().clear();
        Carnivore testCarnivore = new Carnivore("Hunter", 10, 10, 1.5f, 15, true, com.ecosystem.model.enums.Gender.FEMALE, 0.1f, 1200, 0.05f, 1, 40);
        Herbivore testPrey = new Herbivore("Prey", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        testCarnivore.setEnergy(30); // Make carnivore hungry

        simulationService.getCreatures().add(testCarnivore);
        simulationService.getCreatures().add(testPrey);

        float initialPreyHealth = testPrey.getHealth();
        float initialCarnivoreEnergy = testCarnivore.getEnergy();

        simulationService.runSimulationStep();

        assertTrue(testPrey.getHealth() < initialPreyHealth || !testPrey.isAlive(), "Herbivore (prey) should be hunted (health reduced or died).");
        if (!testPrey.isAlive()) {
            assertTrue(testCarnivore.getEnergy() > initialCarnivoreEnergy, "Carnivore energy should increase after eating prey.");
        }
    }
    
    @Test
    void solveEncounters_reproductionInteraction() {
        simulationService.getCreatures().clear();
        Herbivore herbivore1 = new Herbivore("H1", 10, 10, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        Herbivore herbivore2 = new Herbivore("H2", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.FEMALE, 0.1f, 1000, 0.05f, 1, 25);
        
        herbivore1.setEnergy(100); herbivore1.setMateTimer(0); herbivore1.setAge(500);
        herbivore2.setEnergy(100); herbivore2.setMateTimer(0); herbivore2.setAge(500);

        simulationService.getCreatures().add(herbivore1);
        simulationService.getCreatures().add(herbivore2);
        
        int initialCreatureCount = simulationService.getCreatures().size();

        simulationService.runSimulationStep(); // This step should trigger reproduction

        // Check if new offspring was added
        // Note: offspring might be of type Creature, or specific like Herbivore.
        // The reproduction logic in Creature.java creates a new Creature instance.
        // For this test, we expect the count to increase.
        assertTrue(simulationService.getCreatures().size() > initialCreatureCount, "Creature count should increase due to reproduction.");
        
        // Verify mate timers are set
        assertTrue(herbivore1.getMateTimer() > 0, "Herbivore1 mate timer should be set.");
        assertTrue(herbivore2.getMateTimer() > 0, "Herbivore2 mate timer should be set.");
    }
}
