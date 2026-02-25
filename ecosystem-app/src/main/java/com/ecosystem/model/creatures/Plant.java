package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender; // Though plants might not use it
import com.ecosystem.utils.EcoUtils;

import java.util.List;
import java.util.Random;

public class Plant extends Creature {
    private static final float DEFAULT_PLANT_ENERGY_VALUE = 50; // Energy a herbivore gets from eating this plant

    // Constructor for specific position (less common for initial plant setup, but could be used)
    public Plant(float x, float y, int size, boolean toroidal, int maxAge, float effectiveWorldWidth, float effectiveWorldHeight) {
        // Plants might not need speed, dirVariation, complex mutation rates for these, maxOffspring, or offspringEnergy
        // Using NEUTRAL gender for plants
        super(EcoUtils.nameGenerator(), x, y, 0, size, toroidal, Gender.NEUTRAL, 0, maxAge, 0.01f, 0, 0, effectiveWorldWidth, effectiveWorldHeight);
        setEnergy(100); // Plants start with some energy
        setHealth(50); // Plants have health
        setStatus("Growing");
        // Color is set in super constructor
    }

    // Constructor for random world position
    public Plant(float effectiveWorldWidth, float effectiveWorldHeight, int size, boolean toroidal, int maxAge) {
        super(EcoUtils.nameGenerator(), effectiveWorldWidth, effectiveWorldHeight, 0, size, toroidal, Gender.NEUTRAL, 0, maxAge, 0.01f, 0, 0);
        setEnergy(100);
        setHealth(50);
        setStatus("Growing");
        // Color is set in super constructor
    }
    
    @Override
    public void move(int worldWidth, int worldHeight) {
        // Plants typically don't move, or have very limited movement (e.g. growth/spreading)
        // For now, do nothing.
        // They still age and lose a tiny bit of energy
        setAge(getAge() + 1);
        // Photosynthesis: plants slowly regain energy (capped at 100)
        float newEnergy = getEnergy() + 0.05f;
        if (newEnergy > 100) newEnergy = 100;
        setEnergy(newEnergy);
        if (getMateTimer() > 0) setMateTimer(getMateTimer() - 1); // Though plants might not use this
    }

    /**
     * Called when a herbivore eats this plant.
     * Reduces the plant's health. If health is depleted, the plant dies.
     * @return The amount of energy the herbivore gained.
     */
    public float getEaten() {
        if (!isAlive()) {
            return 0;
        }
        setHealth(getHealth() - 25); // Example: reduce health by a fixed amount
        if (getHealth() <= 0) {
            setEnergy(0); // Plant dies, no more energy
            return DEFAULT_PLANT_ENERGY_VALUE / 2; // Return some energy if it was killed
        }
        return DEFAULT_PLANT_ENERGY_VALUE; // Return full energy if still alive
    }

    @Override
    public List<Creature> solveEncounter(Creature other, int worldWidth, int worldHeight) {
        // Plants don't initiate encounters in this model.
        // Their interaction is typically being eaten, which is handled by Herbivore's solveEncounter
        // or by a specific getEaten() method called by the Herbivore.
        // The base Creature.solveEncounter might make them turn away, which is fine if a non-herbivore bumps into them.
        
        // If the other creature is a Herbivore, it might try to eat this plant.
        // The Herbivore's solveEncounter should handle the eating action.
        // This method is called for both creatures in an encounter.
        // So, if 'other' is a Herbivore, its solveEncounter will handle eating this Plant.
        
        // For now, no specific action needed from Plant's perspective in solveEncounter,
        // as the active part (eating) is done by the Herbivore.
        // If we wanted plants to have defensive mechanisms, we could add logic here.
        return null; // No offspring produced by this interaction
    }
    
    @Override
    public boolean canMateWith(Creature other) {
        // Plants in this model might not reproduce via mating with another creature in the same way.
        // They might spread seeds (could be a separate mechanic) or not reproduce in this simulation.
        // For now, disable mating.
        return false;
    }
}
