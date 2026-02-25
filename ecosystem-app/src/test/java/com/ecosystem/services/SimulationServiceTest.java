package com.ecosystem.services;

import com.ecosystem.model.creatures.*;
import com.ecosystem.model.environment.Territory;
import com.ecosystem.model.environment.TPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;
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
        // Manually create and add one plant and one herbivore very close to each other
        Plant testPlant = new Plant(10, 10, 10, true, 500, worldWidth, worldHeight);
        Herbivore testHerbivore = new Herbivore("TestEater", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        testHerbivore.setEnergy(30); // Make herbivore hungry
        testHerbivore.setX(10);
        testHerbivore.setY(10);
        testHerbivore.setSpeed(0f);
        testHerbivore.setDirVariation(0f);

        List<Creature> testList = new ArrayList<>();
        testList.add(testHerbivore);
        testList.add(testPlant);
        simulationService.setCreaturesForTesting(testList);
        
        float initialPlantHealth = testPlant.getHealth();
        float initialHerbivoreEnergy = testHerbivore.getEnergy();

        simulationService.runSimulationStep(); // This will call internal methods including those that lead to solveEncounters

        assertTrue(testPlant.getHealth() < initialPlantHealth || !testPlant.isAlive(), "Plant should be eaten (health reduced or died).");
        assertTrue(testHerbivore.getEnergy() > initialHerbivoreEnergy, "Herbivore energy should increase after eating.");
    }

    @Test
    void solveEncounters_carnivoreHerbivoreInteraction() {
        Carnivore testCarnivore = new Carnivore("Hunter", 10, 10, 1.5f, 15, true, com.ecosystem.model.enums.Gender.FEMALE, 0.1f, 1200, 0.05f, 1, 40);
        Herbivore testPrey = new Herbivore("Prey", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        testCarnivore.setEnergy(30); // Make carnivore hungry

        List<Creature> testList = new ArrayList<>();
        testList.add(testCarnivore);
        testList.add(testPrey);
        simulationService.setCreaturesForTesting(testList);

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
        Herbivore herbivore1 = new Herbivore("H1", 10, 10, 1, 12, true, com.ecosystem.model.enums.Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        Herbivore herbivore2 = new Herbivore("H2", 11, 11, 1, 12, true, com.ecosystem.model.enums.Gender.FEMALE, 0.1f, 1000, 0.05f, 1, 25);
        
        herbivore1.setEnergy(100); herbivore1.setMateTimer(0); herbivore1.setAge(500);
        herbivore2.setEnergy(100); herbivore2.setMateTimer(0); herbivore2.setAge(500);

        List<Creature> testList = new ArrayList<>();
        testList.add(herbivore1);
        testList.add(herbivore2);
        simulationService.setCreaturesForTesting(testList);
        
        int initialCreatureCount = testList.size();

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

    @Test
    void resetSimulation_sameSeed_producesSameInitialState() {
        long seed = 123456789L;

        simulationService.resetSimulation(seed);
        List<String> first = snapshotCreatures(simulationService.getCreatures());

        simulationService.resetSimulation(seed);
        List<String> second = snapshotCreatures(simulationService.getCreatures());

        assertEquals(first, second, "Initial state should be identical when reset with the same seed.");
    }

    @Test
    void resetSimulation_sameSeed_producesSameOneStepState() {
        long seed = 987654321L;

        simulationService.resetSimulation(seed);
        simulationService.runSimulationStep();
        List<String> firstAfterStep = snapshotCreatures(simulationService.getCreatures());

        simulationService.resetSimulation(seed);
        simulationService.runSimulationStep();
        List<String> secondAfterStep = snapshotCreatures(simulationService.getCreatures());

        assertEquals(firstAfterStep, secondAfterStep, "One-step state should be identical when replayed with the same seed.");
    }

    @Test
    void runSimulationStep_incrementsStepCount() {
        long initialStep = simulationService.getSimulationStepCount();

        simulationService.runSimulationStep();
        simulationService.runSimulationStep();

        assertEquals(initialStep + 2, simulationService.getSimulationStepCount(), "Simulation step counter should increment per run.");
    }

    @Test
    void resetSimulation_resetsStepCountAndUpdatesSeed() {
        simulationService.resetSimulation(42L);
        simulationService.runSimulationStep();
        assertTrue(simulationService.getSimulationStepCount() > 0, "Step count should increase after running a step.");

        simulationService.resetSimulation(7L);
        assertEquals(0, simulationService.getSimulationStepCount(), "Step count should reset to zero after reset.");
        assertEquals(7L, simulationService.getSimulationSeed(), "Simulation seed should reflect the latest reset seed.");
    }

    private List<String> snapshotCreatures(List<Creature> creatures) {
        return creatures.stream().map(c -> String.format(
                "%s|%s|%s|%s|%.6f|%.6f|%.6f|%.6f|%.6f|%.6f|%.6f|%d|%d|%.6f|%d|%.6f|%b",
                c.getType(),
                c.getName(),
                c.getGender(),
                c.getColor(),
                c.getX(),
                c.getY(),
                c.getDirection(),
                c.getEnergy(),
                c.getHealth(),
                c.getSpeed(),
                c.getDirVariation(),
                c.getSize(),
                c.getMaxAge(),
                c.getMutationRate(),
                c.getMaxOffspring(),
                c.getOffspringEnergy(),
                c.isAlive()
        )).collect(Collectors.toList());
    }
}
