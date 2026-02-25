package com.ecosystem.model.creatures;

import com.ecosystem.model.enums.Gender;
import com.ecosystem.utils.EcoUtils; // For name and gender generation
import com.ecosystem.utils.SimulationRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Creature {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final int id;
    private String name;
    private float health;
    private float energy;
    private float speed;
    private Gender gender;
    private float x, y; // Position
    private float dx, dy; // Speed components
    private float direction; // Radians
    private float dirVariation; // How much direction can change
    private int size;
    private boolean toroidal; // World wraps around?
    private String color;
    private String status;

    // Effective world dimensions for this creature's movement and positioning
    private float creatureEffectiveWorldWidth;
    private float creatureEffectiveWorldHeight;

    // New attributes from the description
    private int age;
    private int maxAge; // Example: 1000 simulation steps
    private float mutationRate; // Example: 0.05f (5% chance)
    private int mateTimer; // Cooldown after mating
    private int maxOffspring;
    private float offspringEnergy; // Energy given to offspring

    // Constructor for creating creatures with specific initial parameters
    public Creature(String name, float x, float y, float speed, int size, boolean toroidal, Gender gender, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy, float effectiveWorldWidth, float effectiveWorldHeight) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        this.x = x;
        this.y = y;
        this.creatureEffectiveWorldWidth = effectiveWorldWidth; // Initialize effective world dimensions
        this.creatureEffectiveWorldHeight = effectiveWorldHeight; // Initialize effective world dimensions
        this.speed = speed;
        this.size = size;
        this.toroidal = toroidal;
        this.color = EcoUtils.generateHexColor();
        this.gender = gender;
        this.dirVariation = dirVariation;
        this.status = "Roaming";

        this.health = 100; // Initial health
        this.energy = 100; // Initial energy
        this.age = 0;
        this.maxAge = maxAge;
        this.mutationRate = mutationRate;
        this.mateTimer = 0; // Can mate immediately
        this.maxOffspring = maxOffspring;
        this.offspringEnergy = offspringEnergy;

        this.direction = SimulationRandom.current().nextFloat() * 2 * (float) Math.PI; // Random initial direction
        updateSpeedComponents();
    }

    // Constructor for creating creatures with random initial position within world bounds
    public Creature(String name, float worldWidth, float worldHeight, float speed, int size, boolean toroidal, Gender gender, float dirVariation, int maxAge, float mutationRate, int maxOffspring, float offspringEnergy) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        // Store the passed worldWidth and worldHeight as the effective dimensions for this creature
        this.creatureEffectiveWorldWidth = worldWidth;
        this.creatureEffectiveWorldHeight = worldHeight;
        this.x = SimulationRandom.current().nextFloat() * this.creatureEffectiveWorldWidth;
        this.y = SimulationRandom.current().nextFloat() * this.creatureEffectiveWorldHeight;
        this.speed = speed;
        this.size = size;
        this.toroidal = toroidal;
        this.color = EcoUtils.generateHexColor();
        this.gender = gender;
        this.dirVariation = dirVariation;
        this.status = "Roaming";

        this.health = 100;
        this.energy = 100;
        this.age = 0;
        this.maxAge = maxAge;
        this.mutationRate = mutationRate;
        this.mateTimer = 0;
        this.maxOffspring = maxOffspring;
        this.offspringEnergy = offspringEnergy;
        
        this.direction = SimulationRandom.current().nextFloat() * 2 * (float) Math.PI;
        updateSpeedComponents();
    }

    private void updateSpeedComponents() {
        this.dx = (float) Math.cos(this.direction) * this.speed;
        this.dy = (float) Math.sin(this.direction) * this.speed;
    }

    // The 'changeAngle' logic is part of move now
    public void move(int ignoredWorldWidth, int ignoredWorldHeight) { // Parameters now potentially ignored if using stored effective dimensions
        if (!isAlive()) return;

        // Change direction slightly based on dirVariation
        // dirVariation could be an angle in radians e.g. Math.toRadians(15) for +/- 15 degrees
        this.direction += (SimulationRandom.current().nextFloat() * 2 - 1) * this.dirVariation * 0.35f;
        updateSpeedComponents();

        this.x += this.dx;
        this.y += this.dy;

        // Energy cost for moving
        this.energy -= Math.max(0.03f, this.speed * 0.05f); // Scaled movement cost

        if (toroidal) {
            if (this.x < 0) this.x += this.creatureEffectiveWorldWidth;
            if (this.y < 0) this.y += this.creatureEffectiveWorldHeight;
            if (this.x >= this.creatureEffectiveWorldWidth) this.x -= this.creatureEffectiveWorldWidth;
            if (this.y >= this.creatureEffectiveWorldHeight) this.y -= this.creatureEffectiveWorldHeight;
        } else {
            // Bounce off walls or clamp to edges if not toroidal
            if (this.x < 0) {
                this.x = 0;
                this.dx *= -1;
                this.direction = (float) Math.atan2(this.dy, this.dx);
            } else if (this.x >= this.creatureEffectiveWorldWidth) {
                this.x = this.creatureEffectiveWorldWidth - 1;
                this.dx *= -1;
                this.direction = (float) Math.atan2(this.dy, this.dx);
            }

            if (this.y < 0) {
                this.y = 0;
                this.dy *= -1;
                this.direction = (float) Math.atan2(this.dy, this.dx);
            } else if (this.y >= this.creatureEffectiveWorldHeight) {
                this.y = this.creatureEffectiveWorldHeight - 1;
                this.dy *= -1;
                this.direction = (float) Math.atan2(this.dy, this.dx);
            }
        }
        this.age++;
        if (this.mateTimer > 0) this.mateTimer--;
    }

    public List<Creature> solveEncounter(Creature other, int worldWidth, int worldHeight) {
        if (!this.isAlive() || !other.isAlive() || this == other) {
            return null; // No interaction if one is dead or comparing to self
        }

        List<Creature> offspring = new ArrayList<>();
        float distance = (float) Math.hypot(this.x - other.x, this.y - other.y);

        // Check for mating conditions
        if (this.canMateWith(other) && distance < (this.size + other.size) * 0.7f) { // Example mating distance
            offspring.addAll(this.reproduce(other, worldWidth, worldHeight));
            // Optional: Add energy cost for reproduction or other effects
        }
        // Placeholder for other interactions (e.g., fighting, fleeing)
        // For example, if creatures are aggressive:
        // else if (distance < (this.size + other.size) * 0.5f) {
        //     fight(other);
        // }

        return offspring; // Return list of new creatures (if any)
    }

    public boolean canMateWith(Creature other) {
        if (this.gender == Gender.NEUTRAL || other.getGender() == Gender.NEUTRAL) {
            return false; // Neutral creatures cannot mate
        }
        return this.gender != other.getGender() &&
               this.energy > 50 && other.getEnergy() > 50 && // Energy requirement
               this.age > this.maxAge * 0.2 && other.getAge() > other.getMaxAge() * 0.2 && // Maturity
               this.mateTimer == 0 && other.getMateTimer() == 0; // Not in mating cooldown
    }

    public List<Creature> reproduce(Creature partner, int worldWidth, int worldHeight) {
        List<Creature> offspringList = new ArrayList<>();
        int numOffspring = 1 + SimulationRandom.current().nextInt(this.maxOffspring); // 1 to maxOffspring

        for (int i = 0; i < numOffspring; i++) {
            if (this.energy < this.offspringEnergy || partner.getEnergy() < partner.offspringEnergy) {
                break; // Not enough energy for more offspring
            }

            this.energy -= this.offspringEnergy;
            partner.setEnergy(partner.getEnergy() - partner.offspringEnergy);

            String childName = EcoUtils.nameGenerator();
            Gender childGender = EcoUtils.binaryGender();
            
            // Inherit and mutate properties
            float childSpeed = (this.speed + partner.getSpeed()) / 2;
            if (SimulationRandom.current().nextFloat() < this.mutationRate) childSpeed *= (0.8f + SimulationRandom.current().nextFloat() * 0.4f); // +/- 20%

            int childSize = (this.size + partner.getSize()) / 2;
            if (SimulationRandom.current().nextFloat() < this.mutationRate) childSize = Math.max(5, childSize + (SimulationRandom.current().nextInt(5) - 2));


            float childDirVariation = (this.dirVariation + partner.getDirVariation()) / 2;
             if (SimulationRandom.current().nextFloat() < this.mutationRate) childDirVariation *= (0.8f + SimulationRandom.current().nextFloat() * 0.4f);


            int childMaxAge = (this.maxAge + partner.getMaxAge()) / 2;
            if (SimulationRandom.current().nextFloat() < this.mutationRate) childMaxAge = Math.max(100, childMaxAge + (SimulationRandom.current().nextInt(200) - 100));


            float childMutationRate = (this.mutationRate + partner.getMutationRate()) / 2;
            if (SimulationRandom.current().nextFloat() < this.mutationRate) childMutationRate = Math.max(0.01f, childMutationRate * (0.8f + SimulationRandom.current().nextFloat() * 0.4f));
            
            int childMaxOffspring = (this.maxOffspring + partner.getMaxOffspring()) / 2;
             if (SimulationRandom.current().nextFloat() < this.mutationRate) childMaxOffspring = Math.max(1, childMaxOffspring + (SimulationRandom.current().nextInt(3)-1));

            float childOffspringEnergy = (this.offspringEnergy + partner.getOffspringEnergy()) / 2;
            if (SimulationRandom.current().nextFloat() < this.mutationRate) childOffspringEnergy = Math.max(10, childOffspringEnergy * (0.8f + SimulationRandom.current().nextFloat() * 0.4f));


            Creature child = new Creature(
                childName,
                this.x + (SimulationRandom.current().nextFloat() * 20 - 10), // Spawn near parent, ensure it's clamped if needed or use effective dimensions for spawn logic too
                this.y + (SimulationRandom.current().nextFloat() * 20 - 10), // Spawn near parent
                childSpeed,
                childSize,
                this.toroidal, // Inherit toroidal nature
                childGender,
                childDirVariation,
                childMaxAge,
                childMutationRate,
                childMaxOffspring,
                childOffspringEnergy,
                this.creatureEffectiveWorldWidth, // Pass effective dimensions to offspring
                this.creatureEffectiveWorldHeight // Pass effective dimensions to offspring
            );
            child.setEnergy(this.offspringEnergy); // Start with initial energy portion
            offspringList.add(child);
        }

        this.mateTimer = 200; // Cooldown period for this parent
        partner.setMateTimer(200); // Cooldown period for partner

        return offspringList;
    }


    public boolean isAlive() {
        return health > 0 && energy > 0 && age < maxAge;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        updateSpeedComponents(); 
    }

    public int getId() {
        return id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
        updateSpeedComponents(); 
    }
    
    public float getDirVariation() {
        return dirVariation;
    }

    public void setDirVariation(float dirVariation) {
        this.dirVariation = dirVariation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isToroidal() {
        return toroidal;
    }

    public void setToroidal(boolean toroidal) {
        this.toroidal = toroidal;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }
    
    public int getMateTimer() {
        return mateTimer;
    }

    public void setMateTimer(int mateTimer) {
        this.mateTimer = mateTimer;
    }

    public int getMaxOffspring() {
        return maxOffspring;
    }

    public void setMaxOffspring(int maxOffspring) {
        this.maxOffspring = maxOffspring;
    }

    public float getOffspringEnergy() {
        return offspringEnergy;
    }

    public void setOffspringEnergy(float offspringEnergy) {
        this.offspringEnergy = offspringEnergy;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Creature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", health=" + health +
                ", energy=" + energy +
                ", age=" + age +
                ", gender=" + gender +
                (isAlive() ? "" : ", DEAD") +
                '}';
    }
}
