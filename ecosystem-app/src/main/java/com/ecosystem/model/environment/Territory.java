package com.ecosystem.model.environment;

import com.ecosystem.model.creatures.Creature;
import java.util.ArrayList;
import java.util.List;

public class Territory {
    private TPlace[][] grid;
    private int width; // Number of columns
    private int height; // Number of rows
    private int placeSize; // Size of each TPlace in pixels (optional, for mapping to world coordinates)
    private List<Creature> allCreatures; // Keep track of all creatures in the territory

    public Territory(int width, int height, int placeSize) {
        this.width = width;
        this.height = height;
        this.placeSize = placeSize;
        this.grid = new TPlace[height][width];
        this.allCreatures = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new TPlace(j, i); // x is column (j), y is row (i)
            }
        }
    }

    // Method to add a creature to the territory and assign it to the correct TPlace
    public void addCreature(Creature creature) {
        if (creature == null) return;

        allCreatures.add(creature);
        // Determine which TPlace the creature belongs to based on its x, y coordinates
        // This assumes creature x,y are world coordinates
        int gridX = (int) (creature.getX() / placeSize);
        int gridY = (int) (creature.getY() / placeSize);

        if (isValidGridPosition(gridX, gridY)) {
            grid[gridY][gridX].addCreature(creature);
        } else {
            // Handle cases where creature is outside the defined grid,
            // e.g. by placing it at the nearest edge or logging an error.
            // For toroidal worlds, this might involve wrapping around.
            System.err.println("Creature " + creature.getName() + " is outside the territory grid. Position: (" + creature.getX() + "," + creature.getY() + ")");
            // Example: Clamp to edge if not toroidal, or wrap if toroidal
            if (creature.isToroidal()) {
                gridX = (gridX % width + width) % width; // Ensure positive modulo
                gridY = (gridY % height + height) % height; // Ensure positive modulo
                 grid[gridY][gridX].addCreature(creature);
            } else {
                 // Simple clamping for non-toroidal
                gridX = Math.max(0, Math.min(gridX, width - 1));
                gridY = Math.max(0, Math.min(gridY, height - 1));
                grid[gridY][gridX].addCreature(creature);
            }
        }
    }

    // Method to update a creature's position within the grid
    // This would be called after a creature moves
    public void updateCreaturePosition(Creature creature, float oldX, float oldY) {
        int oldGridX = (int) (oldX / placeSize);
        int oldGridY = (int) (oldY / placeSize);

        int newGridX = (int) (creature.getX() / placeSize);
        int newGridY = (int) (creature.getY() / placeSize);
        
        if (creature.isToroidal()) {
            oldGridX = (oldGridX % width + width) % width;
            oldGridY = (oldGridY % height + height) % height;
            newGridX = (newGridX % width + width) % width;
            newGridY = (newGridY % height + height) % height;
        }


        if (isValidGridPosition(oldGridX, oldGridY) && (oldGridX != newGridX || oldGridY != newGridY)) {
            grid[oldGridY][oldGridX].removeCreature(creature);
        }

        if (isValidGridPosition(newGridX, newGridY)) {
            // Check if the creature is already in the new TPlace's list (e.g. if it didn't move far enough to change TPlace)
            // This check might be redundant if removeCreature correctly handles non-existent creatures.
            if (!grid[newGridY][newGridX].getCreatures().contains(creature)) {
                 grid[newGridY][newGridX].addCreature(creature);
            }
        } else {
             // Similar handling as in addCreature if new position is outside grid
            System.err.println("Creature " + creature.getName() + " moved outside the territory grid. New Position: (" + creature.getX() + "," + creature.getY() + ")");
            // Clamp or wrap based on toroidal property
            if (creature.isToroidal()) {
                newGridX = (newGridX % width + width) % width;
                newGridY = (newGridY % height + height) % height;
                 if (!grid[newGridY][newGridX].getCreatures().contains(creature)) {
                    grid[newGridY][newGridX].addCreature(creature);
                 }
            } else {
                newGridX = Math.max(0, Math.min(newGridX, width - 1));
                newGridY = Math.max(0, Math.min(newGridY, height - 1));
                if (!grid[newGridY][newGridX].getCreatures().contains(creature)) {
                    grid[newGridY][newGridX].addCreature(creature);
                }
            }
        }
    }
    
    public void removeCreature(Creature creature) {
        if (creature == null) return;

        allCreatures.remove(creature);
        int gridX = (int) (creature.getX() / placeSize);
        int gridY = (int) (creature.getY() / placeSize);

        if (creature.isToroidal()) {
            gridX = (gridX % width + width) % width;
            gridY = (gridY % height + height) % height;
        }

        if (isValidGridPosition(gridX, gridY)) {
            grid[gridY][gridX].removeCreature(creature);
        } else {
             System.err.println("Trying to remove creature " + creature.getName() + " from outside the territory grid. Position: (" + creature.getX() + "," + creature.getY() + ")");
        }
    }


    public TPlace getPlace(int x, int y) {
        if (isValidGridPosition(x, y)) {
            return grid[y][x]; // y is row, x is column
        }
        return null;
    }

    public List<Creature> getAllCreatures() {
        return new ArrayList<>(allCreatures);
    }

    private boolean isValidGridPosition(int gridX, int gridY) {
        return gridX >= 0 && gridX < width && gridY >= 0 && gridY < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPlaceSize() {
        return placeSize;
    }

    // Method to simulate movement for all creatures
    // worldPixelWidth and worldPixelHeight are the total dimensions of the simulation area in pixels
    public void updateAllCreatures(int worldPixelWidth, int worldPixelHeight) {
        List<Creature> creaturesCopy = new ArrayList<>(allCreatures); // Avoid ConcurrentModificationException
        for (Creature creature : creaturesCopy) {
            float oldX = creature.getX();
            float oldY = creature.getY();
            creature.move(worldPixelWidth, worldPixelHeight); // Creature updates its own x,y
            updateCreaturePosition(creature, oldX, oldY); // Territory updates creature's TPlace
        }
    }
}
