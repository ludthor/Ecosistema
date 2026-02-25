package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CreatureTest {

    private Creature creature;
    private final int worldWidth = 100;
    private final int worldHeight = 100;

    @BeforeEach
    void setUp() {
        // Basic creature for most tests
        creature = new Creature("TestCreature", 50, 50, 1, 10, true, Gender.MALE, 0.1f, 1000, 0.05f, 2, 20, worldWidth, worldHeight);
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        Creature c = new Creature("ConstructorTest", 10, 20, 2, 15, false, Gender.FEMALE, 0.2f, 500, 0.1f, 1, 10, worldWidth, worldHeight);
        assertNotNull(c.getId(), "ID should not be null"); // ID is auto-generated
        assertEquals("ConstructorTest", c.getName());
        assertEquals(10, c.getX());
        assertEquals(20, c.getY());
        assertEquals(2, c.getSpeed());
        assertEquals(15, c.getSize());
        assertFalse(c.isToroidal());
        assertEquals(Gender.FEMALE, c.getGender());
        assertEquals(0.2f, c.getDirVariation());
        assertEquals(500, c.getMaxAge());
        assertEquals(0.1f, c.getMutationRate());
        assertEquals(1, c.getMaxOffspring());
        assertEquals(10, c.getOffspringEnergy());

        assertEquals(100, c.getHealth(), "Default health should be 100.");
        assertEquals(100, c.getEnergy(), "Default energy should be 100.");
        assertEquals(0, c.getAge(), "Initial age should be 0.");
    }

    @Test
    void constructor_randomPosition_withinBounds() {
        Creature c = new Creature("RandomPos", worldWidth, worldHeight, 1, 10, true, Gender.MALE, 0.1f, 1000, 0.05f, 2, 20);
        assertTrue(c.getX() >= 0 && c.getX() < worldWidth, "X should be within world width.");
        assertTrue(c.getY() >= 0 && c.getY() < worldHeight, "Y should be within world height.");
    }

    @Test
    void move_changesPositionAndDecreasesEnergy() {
        float initialX = creature.getX();
        float initialY = creature.getY();
        float initialEnergy = creature.getEnergy();

        creature.move(worldWidth, worldHeight);

        assertTrue(creature.getX() != initialX || creature.getY() != initialY, "Position should change after move.");
        assertTrue(creature.getEnergy() < initialEnergy, "Energy should decrease after move.");
        assertEquals(1, creature.getAge(), "Age should increment after move.");
    }

    @Test
    void move_toroidalMovement_wrapsAround() {
        // Position creature near edge, moving out of bounds
        creature.setX(1); 
        creature.setY(1);
        // Set direction to move left and up (e.g. towards negative coordinates)
        creature.setDirection((float) Math.toRadians(225)); // Southwest
        creature.setSpeed(5); // Ensure it moves far enough in one step

        creature.move(worldWidth, worldHeight);
        // Check if wrapped around (e.g., x becomes worldWidth - something)
        assertTrue(creature.getX() > worldWidth - 5 && creature.getX() < worldWidth, "X should wrap around in toroidal world.");
        assertTrue(creature.getY() > worldHeight - 5 && creature.getY() < worldHeight, "Y should wrap around in toroidal world.");

        creature.setX(worldWidth - 1);
        creature.setY(worldHeight - 1);
        creature.setDirection((float) Math.toRadians(45)); // Northeast
        creature.setSpeed(5);
        
        creature.move(worldWidth,worldHeight);
        assertTrue(creature.getX() < 5 && creature.getX() >=0, "X should wrap around from positive edge.");
        assertTrue(creature.getY() < 5 && creature.getY() >=0, "Y should wrap around from positive edge.");
    }
    
    @Test
    void move_nonToroidalMovement_bounces() {
        Creature nonToroidalCreature = new Creature("NonToroidal", 5, 5, 2, 10, false, Gender.MALE, 0.1f, 100, 0.1f, 1, 10, worldWidth, worldHeight);
        nonToroidalCreature.setSpeed(10); // Large speed to ensure it hits boundary
        nonToroidalCreature.setDirVariation(0f);
        
        // Move towards negative X
        nonToroidalCreature.setDirection((float) Math.PI); // West
        float initialDx = nonToroidalCreature.getDx();
        nonToroidalCreature.move(worldWidth, worldHeight);
        assertEquals(0, nonToroidalCreature.getX(), "X should be clamped at 0.");
        assertTrue(nonToroidalCreature.getDx() == -initialDx || nonToroidalCreature.getDx() == initialDx, "dx should reverse or stay if clamped.");

        // Move towards positive X boundary
        nonToroidalCreature.setX(worldWidth - 5);
        nonToroidalCreature.setDirection(0); // East
        initialDx = nonToroidalCreature.getDx();
        nonToroidalCreature.move(worldWidth, worldHeight);
        assertEquals(worldWidth -1, nonToroidalCreature.getX(), "X should be clamped at worldWidth-1.");
        assertTrue(nonToroidalCreature.getDx() == -initialDx || nonToroidalCreature.getDx() == initialDx, "dx should reverse or stay if clamped.");
    }


    @Test
    void isAlive_trueWhenHealthAndEnergyPositiveAndAgeNotMax() {
        creature.setHealth(100);
        creature.setEnergy(100);
        creature.setAge(10);
        creature.setMaxAge(1000);
        assertTrue(creature.isAlive());
    }

    @Test
    void isAlive_falseWhenHealthZero() {
        creature.setHealth(0);
        assertFalse(creature.isAlive());
    }

    @Test
    void isAlive_falseWhenEnergyZero() {
        creature.setEnergy(0);
        assertFalse(creature.isAlive());
    }
    
    @Test
    void isAlive_falseWhenAgeExceedsMaxAge() {
        creature.setAge(1001);
        creature.setMaxAge(1000);
        assertFalse(creature.isAlive());
    }

    @Test
    void solveEncounter_baseVersion_reproductionIfPossible() {
        Creature partner = new Creature("Partner", 51, 51, 1, 10, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 2, 20, worldWidth, worldHeight);
        creature.setEnergy(100); // Ensure enough energy for reproduction
        partner.setEnergy(100);
        creature.setMateTimer(0);
        partner.setMateTimer(0);
        creature.setAge(creature.getMaxAge() / 2); // Mature enough
        partner.setAge(partner.getMaxAge() / 2);   // Mature enough

        List<Creature> offspring = creature.solveEncounter(partner, worldWidth, worldHeight);
        assertNotNull(offspring);
        assertFalse(offspring.isEmpty(), "Offspring should be produced if conditions are met.");
        assertTrue(creature.getMateTimer() > 0, "Creature's mate timer should be set after reproduction.");
        assertTrue(partner.getMateTimer() > 0, "Partner's mate timer should be set after reproduction.");
    }
    
    @Test
    void solveEncounter_baseVersion_noReproductionIfMateTimerActive() {
        Creature partner = new Creature("Partner", 51, 51, 1, 10, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 2, 20, worldWidth, worldHeight);
        creature.setEnergy(100);
        partner.setEnergy(100);
        creature.setMateTimer(10); // Mate timer active for 'creature'
        partner.setMateTimer(0);
        creature.setAge(creature.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = creature.solveEncounter(partner, worldWidth, worldHeight);
        assertTrue(offspring == null || offspring.isEmpty(), "No offspring should be produced if mate timer is active.");
    }

    @Test
    void solveEncounter_withSelf_returnsNull() {
        List<Creature> offspring = creature.solveEncounter(creature, worldWidth, worldHeight);
        assertNull(offspring, "Encounter with self should return null.");
    }

    @Test
    void solveEncounter_withDeadCreature_returnsNull() {
        Creature deadPartner = new Creature("DeadPartner", 51, 51, 1, 10, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 2, 20, worldWidth, worldHeight);
        deadPartner.setHealth(0); // Make it dead
        List<Creature> offspring = creature.solveEncounter(deadPartner, worldWidth, worldHeight);
        assertNull(offspring, "Encounter with a dead creature should return null.");
    }
    
    @Test
    void solveEncounter_differentGendersButTooFar_noReproduction() {
        Creature partner = new Creature("FarPartner", 90, 90, 1, 10, true, Gender.FEMALE, 0.1f, 1000, 0.05f, 2, 20, worldWidth, worldHeight); // Far away
        creature.setEnergy(100);
        partner.setEnergy(100);
        creature.setMateTimer(0);
        partner.setMateTimer(0);
        creature.setAge(creature.getMaxAge() / 2);
        partner.setAge(partner.getMaxAge() / 2);

        List<Creature> offspring = creature.solveEncounter(partner, worldWidth, worldHeight);
        assertTrue(offspring == null || offspring.isEmpty(), "No offspring if too far, even if other conditions met.");
    }
}
