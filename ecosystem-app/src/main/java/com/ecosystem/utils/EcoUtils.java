package com.ecosystem.utils;

import com.ecosystem.model.enums.Gender;
import java.util.Random;

public class EcoUtils {

    private static final String[] SYLLABLES_1 = {"Ab", "Ac", "Ad", "Af", "Ag", "Ak", "Al", "Am", "An", "Ap", "Ar", "As", "At", "Av", "Ax", "Az"};
    private static final String[] SYLLABLES_2 = {"ba", "be", "bi", "bo", "bu"};
    private static final String[] SYLLABLES_3 = {"ca", "ce", "ci", "co", "cu"};
    private static final String[] SYLLABLES_4 = {"da", "de", "di", "do", "du"};
    private static final String[] SYLLABLES_5 = {"fa", "fe", "fi", "fo", "fu"};
    private static final String[] SYLLABLES_6 = {"ga", "ge", "gi", "go", "gu"};
    private static final String[] SYLLABLES_7 = {"ha", "he", "hi", "ho", "hu"};
    private static final String[] SYLLABLES_8 = {"ka", "ke", "ki", "ko", "ku"};
    private static final String[] SYLLABLES_9 = {"la", "le", "li", "lo", "lu"};
    private static final String[] SYLLABLES_10 = {"ma", "me", "mi", "mo", "mu"};
    private static final String[] SYLLABLES_11 = {"na", "ne", "ni", "no", "nu"};
    private static final String[] SYLLABLES_12 = {"pa", "pe", "pi", "po", "pu"};
    private static final String[] SYLLABLES_13 = {"ra", "re", "ri", "ro", "ru"};
    private static final String[] SYLLABLES_14 = {"sa", "se", "si", "so", "su"};
    private static final String[] SYLLABLES_15 = {"ta", "te", "ti", "to", "tu"};
    private static final String[] SYLLABLES_16 = {"va", "ve", "vi", "vo", "vu"};
    private static final String[] SYLLABLES_17 = {"xa", "xe", "xi", "xo", "xu"};
    private static final String[] SYLLABLES_18 = {"za", "ze", "zi", "zo", "zu"};

    private static final String[][] ALL_SYLLABLES = {
            SYLLABLES_1, SYLLABLES_2, SYLLABLES_3, SYLLABLES_4, SYLLABLES_5, SYLLABLES_6, SYLLABLES_7,
            SYLLABLES_8, SYLLABLES_9, SYLLABLES_10, SYLLABLES_11, SYLLABLES_12, SYLLABLES_13, SYLLABLES_14,
            SYLLABLES_15, SYLLABLES_16, SYLLABLES_17, SYLLABLES_18
    };

    private static Random random = new Random();

    public static String nameGenerator() {
        StringBuilder name = new StringBuilder();
        int numSyllables = 2 + random.nextInt(3); // Names will have 2 to 4 syllables

        for (int i = 0; i < numSyllables; i++) {
            String[] syllableSet = ALL_SYLLABLES[random.nextInt(ALL_SYLLABLES.length)];
            name.append(syllableSet[random.nextInt(syllableSet.length)]);
        }
        // Capitalize the first letter
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static Gender binaryGender() {
        if (random.nextBoolean()) {
            return Gender.FEMALE;
        } else {
            return Gender.MALE;
        }
    }

    public static String generateHexColor() {
        Random randomInstance = new Random(); // It's better to reuse the class-level 'random' if possible, or pass it
        int nextInt = randomInstance.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }
}
