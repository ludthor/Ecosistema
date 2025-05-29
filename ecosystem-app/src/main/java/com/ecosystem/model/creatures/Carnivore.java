package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import com.ecosystem.utils.EcoUtils;

import java.util.List;
import java.util.Random;

public class Carnivore extends Creature {
    private static final float ENERGY_FROM_HERBIVORE = 75; // Energy gained from eating a herbivore
    private static final float HUNT_REACH = 10.0f; // How close to hunt a herbivore
    private static final float FIGHT_DAMAGE = 30; // Damage dealt in a fight

    public Carnivore(String name, float x, float y, float speed, int size, boolean toroidal, Gender gender, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy, float effectiveWorldWidth, float effectiveWorldHeight) {
        super(name, x, y, speed, size, toroidal, gender, dirVariation, maxAge, mutationRate, maxOffspring, offspringEnergy, effectiveWorldWidth, effectiveWorldHeight);
        // Color is set in super constructor
    }
    
    // Constructor for random world position
    public Carnivore(float effectiveWorldWidth, float effectiveWorldHeight, float speed, int size, boolean toroidal, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy) {
        super(EcoUtils.nameGenerator(), effectiveWorldWidth, effectiveWorldHeight, speed, size, toroidal, EcoUtils.binaryGender(), dirVariation, maxAge, mutationRate, maxOffspring, offspringEnergy);
        // Color is set in super constructor
    }

    public void huntAndEat(Herbivore herbivore) {
        if (herbivore.isAlive()) {
            // Simulate a fight or hunt
            // Herbivore loses health
            herbivore.setHealth(herbivore.getHealth() - FIGHT_DAMAGE); 
            // System.out.println(this.getName() + " attacked " + herbivore.getName() + ". " + herbivore.getName() + " health: " + herbivore.getHealth());

            if (!herbivore.isAlive()) { // If herbivore died from the attack
                this.setEnergy(this.getEnergy() + ENERGY_FROM_HERBIVORE);
                // System.out.println(this.getName() + " ate " + herbivore.getName() + " and gained " + ENERGY_FROM_HERBIVORE + " energy.");
            } else {
                 // Herbivore might fight back or flee - for now, only Carnivore actively damages in this interaction
                 // Carnivore might also take damage if the herbivore fights back (not implemented here)
            }
        }
    }

    @Override
    public List<Creature> solveEncounter(Creature other, int worldWidth, int worldHeight) {
        if (!this.isAlive() || !other.isAlive()) {
            return null;
        }

        // 1. Interaction with Herbivores
        if (other instanceof Herbivore) {
            Herbivore herbivore = (Herbivore) other;
            float distance = (float) Math.hypot(this.getX() - herbivore.getX(), this.getY() - herbivore.getY());
            // Carnivores hunt Herbivores if close enough and hungry (e.g. energy < 70%)
            if (distance < (this.getSize() / 2.0f + herbivore.getSize() / 2.0f + HUNT_REACH) && this.getEnergy() < 70) {
                huntAndEat(herbivore);
            }
            return null; // No offspring from hunting
        }

        // 2. Interaction with Plants (ignore)
        if (other instanceof Plant) {
            // Carnivores do not interact with plants
            return null;
        }

        // 3. Interaction with other Carnivores (potential reproduction or fight)
        if (other instanceof Carnivore) {
            Carnivore otherCarnivore = (Carnivore) other;
            if (this.canMateWith(otherCarnivore)) {
                 float distance = (float) Math.hypot(this.getX() - other.getX(), this.getY() - other.getY());
                 if (distance < (this.getSize() + other.getSize()) * 0.7f) { // Mating distance
                    return this.reproduce(otherCarnivore, worldWidth, worldHeight);
                }
            }
            // Add fight logic here if desired for Carnivore vs Carnivore
            // For now, they might just turn away based on base Creature's behavior or do nothing specific
            // return super.solveEncounter(other, worldWidth, worldHeight);
        }
        
        // Default: if no specific interaction, do nothing or rely on base class
        return null;
    }
}
