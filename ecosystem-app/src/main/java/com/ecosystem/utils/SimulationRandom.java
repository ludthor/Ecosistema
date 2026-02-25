package com.ecosystem.utils;

import java.util.Random;

public final class SimulationRandom {
    private static volatile Random random = new Random();
    private static volatile Long currentSeed = null;

    private SimulationRandom() {
    }

    public static Random current() {
        return random;
    }

    public static synchronized void reseed(long seed) {
        currentSeed = seed;
        random = new Random(seed);
    }

    public static synchronized void reset() {
        currentSeed = null;
        random = new Random();
    }

    public static Long getCurrentSeed() {
        return currentSeed;
    }
}