package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class HerbivoreTest {

    private Herbivore herbivore;
    private Plant plant;
    private final int worldWidth = 100;
    private final int worldHeight = 100;

    @BeforeEach
    void setUp() {
        herbivore = new Herbivore("TestHerbivore", 50, 50, 1, 12, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 1, 25);
        plant = new Plant(51, 51, 10, true, 500); // Plant close to herbivore
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        Herbivore h = new Herbivore(worldWidth, worldHeight, 1.2f, 15, false, 0.2f, 800, 0.1f, 2, 30);
        assertNotNull(h.getId());
        assertTrue(h.getSpeed() > 0, "Herbivores should have speed.");
        assertEquals(15, h.getSize());
        assertFalse(h.isToroidal());
        assertEquals(800, h.getMaxAge());
        // Gender is random, so just check it's not null
        assertNotNull(h.getGender());
        assertEquals(100, h.getEnergy(), "Default energy.");
        assertEquals(100, h.getHealth(), "Default health.");
    }

    @Test
    void eat_plant_increasesHerbivoreEnergy_plantGetsEaten() {
        float initialHerbivoreEnergy = herbivore.getEnergy();
        float initialPlantHealth = plant.getHealth();

        herbivore.eat(plant);

        assertTrue(herbivore.getEnergy() > initialHerbivoreEnergy, "Herbivore energy should increase after eating.");
        assertTrue(plant.getHealth() < initialPlantHealth || !plant.isAlive(), "Plant health should decrease or plant should die.");
    }

    @Test
    void solveEncounter_withPlant_herbivoreEatsPlant() {
        herbivore.setEnergy(50); // Ensure herbivore is hungry
        float initialHerbivoreEnergy = herbivore.getEnergy();
        float initialPlantHealth = plant.getHealth();

        herbivore.solveEncounter(plant, worldWidth, worldHeight);

        assertTrue(herbivore.getEnergy() > initialHerbivoreEnergy, "Herbivore energy should increase after encountering and eating plant.");
        assertTrue(plant.getHealth() < initialPlantHealth || !plant.isAlive(), "Plant health should decrease or plant should die after being eaten.");
    }

    @Test
    void solveEncounter_withAnotherHerbivore_reproducesWhenConditionsMet() {
        Herbivore partner = new Herbivore("PartnerHerbivore", 51, 51, 1, 12, true, Gender.MALE, 0.1f, 1000, 0.05f, 1, 25);
        herbivore.setEnergy(100);
        partner.setEnergy(100);
        herbivore.setMateTimer(0);
        partner.setMateTimer(0);
        herbivore.setAge(herbivore.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = herbivore.solveEncounter(partner, worldWidth, worldHeight);

        assertNotNull(offspring);
        assertFalse(offspring.isEmpty(), "Offspring should be produced.");
        assertTrue(herbivore.getMateTimer() > 0, "Herbivore's mate timer should be set.");
        assertTrue(partner.getMateTimer() > 0, "Partner's mate timer should be set.");
    }
    
    @Test
    void solveEncounter_withAnotherHerbivore_noReproductionIfTooFar() {
        Herbivore partner = new Herbivore("FarPartner", 90, 90, 1, 12, true, Gender.MALE, 0.1f, 1000, 0.05f, 1, 25); // Far away
        herbivore.setEnergy(100);
        partner.setEnergy(100);
        herbivore.setMateTimer(0);
        partner.setMateTimer(0);
        herbivore.setAge(herbivore.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = herbivore.solveEncounter(partner, worldWidth, worldHeight);
        assertTrue(offspring == null || offspring.isEmpty(), "No offspring if too far.");
    }

    @Test
    void solveEncounter_withCarnivore_herbivoreLosesHealth() {
        // This test depends on Carnivore's solveEncounter behavior.
        // We're testing the outcome on the Herbivore.
        Carnivore carnivore = new Carnivore("TestCarnivore", 50, 50, 1.5f, 15, true, Gender.MALE, 0.1f, 1200, 0.05f, 1, 40);
        carnivore.setX(herbivore.getX()); // Place carnivore on the herbivore
        carnivore.setY(herbivore.getY());
        carnivore.setEnergy(50); // Make sure carnivore is hungry

        float initialHerbivoreHealth = herbivore.getHealth();

        // Simulate Carnivore's action (which would be part of Carnivore's solveEncounter)
        // This is a bit of a conceptual test for Herbivore's reaction to a Carnivore.
        // The Carnivore's solveEncounter method will call its huntAndEat method.
        carnivore.solveEncounter(herbivore, worldWidth, worldHeight); // Herbivore is 'other'

        assertTrue(herbivore.getHealth() < initialHerbivoreHealth || !herbivore.isAlive(), "Herbivore health should decrease or herbivore should die after carnivore encounter.");
    }
}
