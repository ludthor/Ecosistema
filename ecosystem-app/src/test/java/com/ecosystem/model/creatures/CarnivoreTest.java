package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CarnivoreTest {

    private Carnivore carnivore;
    private Herbivore herbivore;
    private Plant plant;
    private final int worldWidth = 100;
    private final int worldHeight = 100;

    @BeforeEach
    void setUp() {
        carnivore = new Carnivore("TestCarnivore", 50, 50, 1.5f, 15, true, Gender.MALE, 0.1f, 1200, 0.05f, 1, 40);
        herbivore = new Herbivore("TestHerbivore", 51, 51, 1, 12, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 1, 25);
        plant = new Plant(52, 52, 10, true, 500, worldWidth, worldHeight); // Plant, for testing ignore interaction
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        Carnivore c = new Carnivore(worldWidth, worldHeight, 1.8f, 18, false, 0.2f, 1500, 0.08f, 2, 50);
        assertNotNull(c.getId());
        assertTrue(c.getSpeed() > 0, "Carnivores should have speed.");
        assertEquals(18, c.getSize());
        assertFalse(c.isToroidal());
        assertEquals(1500, c.getMaxAge());
        assertNotNull(c.getGender()); // Gender is random
        assertEquals(100, c.getEnergy(), "Default energy.");
        assertEquals(100, c.getHealth(), "Default health.");
    }

    @Test
    void huntAndEat_herbivore_carnivoreGainsEnergy_herbivoreLosesHealthOrDies() {
        float initialCarnivoreEnergy = carnivore.getEnergy();
        float initialHerbivoreHealth = herbivore.getHealth();

        carnivore.huntAndEat(herbivore); // Carnivore attacks

        // Herbivore should lose health
        assertTrue(herbivore.getHealth() < initialHerbivoreHealth, "Herbivore health should decrease after being hunted.");

        if (!herbivore.isAlive()) {
            assertTrue(carnivore.getEnergy() > initialCarnivoreEnergy, "Carnivore energy should increase after eating herbivore.");
        } else {
            // Carnivore might not gain energy if herbivore is just damaged but not killed
            // Depending on implementation, energy might still be gained or not.
            // Current huntAndEat: gains energy only if herbivore dies.
            assertEquals(initialCarnivoreEnergy, carnivore.getEnergy(), "Carnivore energy should not change if herbivore is only damaged.");
        }
    }
    
    @Test
    void huntAndEat_herbivoreDies_carnivoreGainsFullEnergyFromHerbivore() {
        herbivore.setHealth(20); // Low health, will die in one hit (FIGHT_DAMAGE = 30)
        float initialCarnivoreEnergy = carnivore.getEnergy();

        carnivore.huntAndEat(herbivore);

        assertFalse(herbivore.isAlive(), "Herbivore should die.");
        assertEquals(initialCarnivoreEnergy + 75, carnivore.getEnergy(), "Carnivore should gain 75 energy."); // ENERGY_FROM_HERBIVORE
    }


    @Test
    void solveEncounter_withHerbivore_carnivoreHuntsHerbivore() {
        carnivore.setEnergy(50); // Ensure carnivore is hungry
        float initialCarnivoreEnergy = carnivore.getEnergy();
        float initialHerbivoreHealth = herbivore.getHealth();

        carnivore.solveEncounter(herbivore, worldWidth, worldHeight);

        assertTrue(herbivore.getHealth() < initialHerbivoreHealth || !herbivore.isAlive(), "Herbivore health should decrease or herbivore should die.");
        if (!herbivore.isAlive()) {
             assertTrue(carnivore.getEnergy() > initialCarnivoreEnergy, "Carnivore energy should increase if herbivore was eaten.");
        }
    }

    @Test
    void solveEncounter_withPlant_carnivoreIgnoresPlant() {
        float initialCarnivoreEnergy = carnivore.getEnergy();
        float initialPlantHealth = plant.getHealth();

        carnivore.solveEncounter(plant, worldWidth, worldHeight);

        assertEquals(initialCarnivoreEnergy, carnivore.getEnergy(), "Carnivore energy should not change after encountering plant.");
        assertEquals(initialPlantHealth, plant.getHealth(), "Plant health should not change.");
        assertTrue(plant.isAlive(), "Plant should remain alive.");
    }

    @Test
    void solveEncounter_withAnotherCarnivore_reproducesWhenConditionsMet() {
        Carnivore partner = new Carnivore("PartnerCarnivore", 51, 51, 1.5f, 15, true, Gender.FEMALE, 0.1f, 1200, 0.05f, 1, 40);
        carnivore.setEnergy(100);
        partner.setEnergy(100);
        carnivore.setMateTimer(0);
        partner.setMateTimer(0);
        carnivore.setAge(carnivore.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = carnivore.solveEncounter(partner, worldWidth, worldHeight);

        assertNotNull(offspring);
        assertFalse(offspring.isEmpty(), "Offspring should be produced.");
        assertTrue(carnivore.getMateTimer() > 0, "Carnivore's mate timer should be set.");
        assertTrue(partner.getMateTimer() > 0, "Partner's mate timer should be set.");
    }
    
    @Test
    void solveEncounter_withAnotherCarnivore_noReproductionIfTooFar() {
        Carnivore partner = new Carnivore("FarPartner", 90, 90, 1.5f, 15, true, Gender.FEMALE, 0.1f, 1200, 0.05f, 1, 40); // Far
        carnivore.setEnergy(100);
        partner.setEnergy(100);
        carnivore.setMateTimer(0);
        partner.setMateTimer(0);
        carnivore.setAge(carnivore.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = carnivore.solveEncounter(partner, worldWidth, worldHeight);
        assertTrue(offspring == null || offspring.isEmpty(), "No offspring if too far.");
    }
}
