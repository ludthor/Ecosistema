package com.ecosystem.model.environment;

import com.ecosystem.model.creatures.Creature;
import java.util.ArrayList;
import java.util.List;

public class TPlace {
    private List<Creature> creatures;
    private int x, y; // Grid coordinates of this place

    public TPlace(int x, int y) {
        this.x = x;
        this.y = y;
        this.creatures = new ArrayList<>();
    }

    public void addCreature(Creature creature) {
        this.creatures.add(creature);
    }

    public void removeCreature(Creature creature) {
        this.creatures.remove(creature);
    }

    public List<Creature> getCreatures() {
        return new ArrayList<>(creatures); // Return a copy to prevent external modification
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "TPlace{" +
                "x=" + x +
                ", y=" + y +
                ", creatureCount=" + creatures.size() +
                '}';
    }
}
