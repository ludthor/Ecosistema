package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import com.ecosystem.utils.EcoUtils;

import java.util.List;
import java.util.Random;

public class Herbivore extends Creature {
    private static final float ENERGY_FROM_PLANT = 50; // Energy gained from eating a plant
    private static final float EAT_REACH = 5.0f; // How close to eat a plant
    private static final float FORAGE_SENSE = 75.0f;
    private static final float EVADE_SENSE = 90.0f;

    private static float inferWorldDimension(float x, float y) {
        return Math.max(100f, Math.max(x + 1f, y + 1f));
    }

    public Herbivore(String name, float x, float y, float speed, int size, boolean toroidal, Gender gender, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy, float effectiveWorldWidth, float effectiveWorldHeight) {
        super(name, x, y, speed, size, toroidal, gender, dirVariation, maxAge, mutationRate, maxOffspring, offspringEnergy, effectiveWorldWidth, effectiveWorldHeight);
        // Color is set in super constructor
    }

    // Backward-compatible constructor used by older tests/callers
    public Herbivore(String name, float x, float y, float speed, int size, boolean toroidal, Gender gender, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy) {
        this(name, x, y, speed, size, toroidal, gender, dirVariation, maxAge, mutationRate, maxOffspring, offspringEnergy, inferWorldDimension(x, y), inferWorldDimension(x, y));
    }

    // Constructor for random world position
    public Herbivore(float effectiveWorldWidth, float effectiveWorldHeight, float speed, int size, boolean toroidal, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy) {
        super(EcoUtils.nameGenerator(), effectiveWorldWidth, effectiveWorldHeight, speed, size, toroidal, EcoUtils.binaryGender(), dirVariation, maxAge, mutationRate, maxOffspring, offspringEnergy);
        // Color is set in super constructor
    }
    
    public void eat(Plant plant) {
        if (plant.isAlive()) {
            float energyGained = plant.getEaten(); // Plant reduces its health and returns energy value
            this.setEnergy(this.getEnergy() + energyGained);
            this.setStatus("Foraging");
            // Optional: Cap energy at max
            // System.out.println(this.getName() + " ate " + plant.getName() + " and gained " + energyGained + " energy.");
        }
    }

    @Override
    public List<Creature> solveEncounter(Creature other, int worldWidth, int worldHeight) {
        if (!this.isAlive() || !other.isAlive()) {
            return null;
        }

        // 1. Interaction with Plants
        if (other instanceof Plant) {
            Plant plant = (Plant) other;
            float distance = (float) Math.hypot(this.getX() - plant.getX(), this.getY() - plant.getY());
            if (distance < FORAGE_SENSE && this.getEnergy() < 92) {
                float directionToPlant = (float) Math.atan2(plant.getY() - this.getY(), plant.getX() - this.getX());
                this.setDirection(directionToPlant);
                this.setStatus("Seeking food");
            }
            // Herbivores eat plants if close enough and hungry enough (e.g. energy < 80%)
            if (distance < (this.getSize() / 2.0f + plant.getSize() / 2.0f + EAT_REACH) && this.getEnergy() < 80) {
                 eat(plant);
            }
            return null; // No offspring from eating
        }

        // 2. Interaction with Carnivores
        // The Carnivore's solveEncounter will handle attacking this Herbivore.
        // This Herbivore might try to flee if it detects a Carnivore nearby,
        // but that's more advanced logic for the 'move' method or a specific 'detectThreat' method.
        // For now, if 'other' is Carnivore, no action from Herbivore's side in this method.
        if (other instanceof Carnivore) {
            float distance = (float) Math.hypot(this.getX() - other.getX(), this.getY() - other.getY());
            if (distance < EVADE_SENSE) {
                float directionAway = (float) Math.atan2(this.getY() - other.getY(), this.getX() - other.getX());
                this.setDirection(directionAway);
                this.setStatus("Evading");
            }
            return null;
        }

        // 3. Interaction with other Herbivores (potential reproduction)
        if (other instanceof Herbivore) {
            Herbivore otherHerbivore = (Herbivore) other;
            if (this.canMateWith(otherHerbivore)) {
                 float distance = (float) Math.hypot(this.getX() - other.getX(), this.getY() - other.getY());
                 this.setDirection((float) Math.atan2(other.getY() - this.getY(), other.getX() - this.getX()));
                 this.setStatus("Mate-seeking");
                 if (distance < (this.getSize() + other.getSize()) * 0.7f) { // Mating distance
                    return this.reproduce(otherHerbivore, worldWidth, worldHeight);
                }
            }
            // If not mating, they can just avoid each other (default Creature behavior might handle this, or add specific logic)
            // For example, make them turn away slightly, which is already in base Creature's move via direction change.
            // The base solveEncounter in Creature.java has a turn away logic, which is fine.
            // return super.solveEncounter(other, worldWidth, worldHeight); // This would cause them to turn away
        }
        
        // Default: if no specific interaction, do nothing or rely on base class (which is currently null)
        return null;
    }
}
