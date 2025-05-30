package com.ecosystem.model.creatures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlantTest {

    private Plant plant;
    private final int worldWidth = 100;
    private final int worldHeight = 100;

    @BeforeEach
    void setUp() {
        plant = new Plant(50, 50, 10, true, 500);
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        Plant p = new Plant(10, 20, 12, false, 600);
        assertNotNull(p.getId());
        assertEquals(0, p.getSpeed(), "Plants should have zero speed.");
        assertEquals(12, p.getSize());
        assertFalse(p.isToroidal());
        assertEquals(600, p.getMaxAge());
        // Plants are Gender.NEUTRAL and have specific defaults for some Creature fields
        assertEquals(com.ecosystem.model.enums.Gender.NEUTRAL, p.getGender());
        assertEquals(100, p.getEnergy(), "Default energy for Plant.");
        assertEquals(50, p.getHealth(), "Default health for Plant.");
    }
    
    @Test
    void constructor_randomPosition_withinBounds() {
        Plant p = new Plant(worldWidth, worldHeight, 10, true, 500);
        assertTrue(p.getX() >= 0 && p.getX() < worldWidth, "X should be within world width.");
        assertTrue(p.getY() >= 0 && p.getY() < worldHeight, "Y should be within world height.");
    }

    @Test
    void move_doesNotChangePosition_agesAndLosesMinimalEnergy() {
        float initialX = plant.getX();
        float initialY = plant.getY();
        float initialEnergy = plant.getEnergy();
        int initialAge = plant.getAge();

        plant.move(worldWidth, worldHeight);

        assertEquals(initialX, plant.getX(), "Plant X position should not change.");
        assertEquals(initialY, plant.getY(), "Plant Y position should not change.");
        assertEquals(initialAge + 1, plant.getAge(), "Plant should age.");
        assertTrue(plant.getEnergy() < initialEnergy, "Plant energy should decrease slightly.");
    }

    @Test
    void getEaten_reducesHealth_returnsEnergyValue() {
        float initialHealth = plant.getHealth();
        float energyValue = plant.getEaten();

        assertTrue(plant.getHealth() < initialHealth, "Plant health should decrease after being eaten.");
        assertEquals(50, energyValue, "Should return correct energy value when eaten but alive."); // DEFAULT_PLANT_ENERGY_VALUE
    }

    @Test
    void getEaten_becomesNotAliveIfHealthDepleted() {
        plant.setHealth(20); // Set health low enough to be depleted in one go
        float energyValue = plant.getEaten(); // First bite
        
        assertTrue(plant.getHealth() <= 0, "Plant health should be zero or less.");
        assertFalse(plant.isAlive(), "Plant should not be alive after health is depleted.");
        assertEquals(25, energyValue, "Should return half energy value when killed by eating."); // DEFAULT_PLANT_ENERGY_VALUE / 2
        
        float moreEnergy = plant.getEaten(); // Try to eat again
        assertEquals(0, moreEnergy, "Should return 0 energy if already dead.");
    }

    @Test
    void solveEncounter_withHerbivore_plantGetsEaten() {
        // This test relies on Herbivore's solveEncounter to call plant.getEaten() or similar.
        // Here, we test Plant's perspective: its state changes if a Herbivore "eats" it.
        // The actual call to plant.getEaten() is initiated by the Herbivore.
        // So, this test is more about the outcome on the Plant rather than Plant.solveEncounter directly.
        
        Herbivore herbivore = new Herbivore(worldWidth, worldHeight, 1, 10, true, 0.1f, 1000, 0.05f, 1, 20);
        herbivore.setX(plant.getX()); // Place herbivore on the plant
        herbivore.setY(plant.getY());
        herbivore.setEnergy(50); // Make sure herbivore is hungry

        float initialPlantHealth = plant.getHealth();

        // Simulate Herbivore's action (which would be part of Herbivore's solveEncounter)
        // For this test, we directly call what Herbivore would do to the plant if it decided to eat.
        // This is a bit of a conceptual test for Plant's reaction.
        if (herbivore.getEnergy() < 80) { // Condition from Herbivore.solveEncounter
            plant.getEaten();
        }
        
        assertTrue(plant.getHealth() < initialPlantHealth, "Plant health should decrease after herbivore encounter.");
    }
    
    @Test
    void solveEncounter_withNonHerbivore_noSpecificAction() {
        // Example: Carnivore encounters a Plant. Plant's solveEncounter should do nothing.
        Carnivore carnivore = new Carnivore(worldWidth, worldHeight, 1, 10, true, 0.1f, 1000, 0.05f, 1, 30);
        carnivore.setX(plant.getX());
        carnivore.setY(plant.getY());
        
        float initialPlantHealth = plant.getHealth();
        float initialPlantEnergy = plant.getEnergy();

        plant.solveEncounter(carnivore, worldWidth, worldHeight); // Carnivore is 'other'

        assertEquals(initialPlantHealth, plant.getHealth(), "Plant health should not change after Carnivore encounter.");
        assertEquals(initialPlantEnergy, plant.getEnergy(), "Plant energy should not change after Carnivore encounter.");
    }
}
