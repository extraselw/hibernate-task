package edu.epam.fop.web.jpa.entity;

import java.util.Arrays;

/**
 * How a discipline is assessed within a course.
 */
public enum Grading {
    EXAM,
    TEST,
    REPORT;

    /**
     * Resolves a grading by its name, ignoring case.
     *
     * @param value grading name, e.g. {@code "exam"}
     * @return the matching grading
     * @throws IllegalArgumentException if no grading has this name
     */
    public static Grading parse(String value) {
        return Arrays.stream(values())
                .filter(grading -> grading.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown grading '" + value + "', expected one of " + Arrays.toString(values())));
    }
}
