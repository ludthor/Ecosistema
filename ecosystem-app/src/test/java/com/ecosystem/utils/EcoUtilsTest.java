package com.ecosystem.utils;

import com.ecosystem.model.enums.Gender;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

public class EcoUtilsTest {

    @Test
    void nameGenerator_returnsNonNullNonEmptyString() {
        String name = EcoUtils.nameGenerator();
        assertNotNull(name, "Generated name should not be null.");
        assertFalse(name.isEmpty(), "Generated name should not be empty.");
        assertTrue(Character.isUpperCase(name.charAt(0)), "First letter of name should be uppercase.");
    }

    @Test
    void binaryGender_returnsMaleOrFemale() {
        Gender gender = EcoUtils.binaryGender();
        assertNotNull(gender, "Generated gender should not be null.");
        assertTrue(gender == Gender.MALE || gender == Gender.FEMALE, "Generated gender should be MALE or FEMALE.");
    }

    @Test
    void generateHexColor_returnsValidHexFormat() {
        String hexColor = EcoUtils.generateHexColor();
        assertNotNull(hexColor, "Generated hex color should not be null.");
        assertTrue(Pattern.matches("^#[0-9a-fA-F]{6}$", hexColor), "Generated hex color should be in #RRGGBB format. Got: " + hexColor);
    }
}
