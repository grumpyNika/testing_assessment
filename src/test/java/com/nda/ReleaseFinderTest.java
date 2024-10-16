package com.nda;

import com.nda.logic.ReleaseFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ReleaseFinderTest {

    private static Stream<Arguments> provideValidInputsWithoutShiftPossible() {
        return Stream.of(
                Arguments.of("one correct option is possible",
                        Arrays.asList(
                                new int[]{1, 2},
                                new int[]{3, 4},
                                new int[]{5, 6}
                        ),
                        6,
                        Arrays.asList(new int[]{1, 2}, new int[]{3, 6})
                ),
                Arguments.of("multiple options possible, but one returned",
                        Arrays.asList(
                                new int[]{3, 3},
                                new int[]{3, 6},
                                new int[]{1, 2},
                                new int[]{1, 4},
                                new int[]{3, 2}
                        ),
                        8,
                        Arrays.asList(new int[]{1, 2}, new int[]{3, 4})
                )
        );
    }

    private static Stream<Arguments> provideValidInputsWithShiftPossible() {
        return Stream.of(
                Arguments.of("one correct option is possible",
                        Arrays.asList(
                                new int[]{1, 2},
                                new int[]{3, 2},
                                new int[]{4, 2}
                        ),
                        6,
                        Arrays.asList(new int[]{1, 2}, new int[]{3, 4}, new int[]{5, 6})
                ),
                Arguments.of("multiple options possible, but one returned",
                        Arrays.asList(
                                new int[]{3, 3},
                                new int[]{3, 5},
                                new int[]{1, 2},
                                new int[]{1, 4},
                                new int[]{3, 2}
                        ),
                        8,
                        Arrays.asList(new int[]{1, 2}, new int[]{3, 4}, new int[]{5, 7})
                )
        );
    }

    private static Stream<Arguments> provideInvalidInputs() {
        return Stream.of(
                Arguments.of("no releases fit sprint length",
                        Arrays.asList(
                                new int[]{1, 6},
                                new int[]{2, 7},
                                new int[]{3, 8}
                        ),
                        3,
                        List.of()
                ),
                Arguments.of("releases list is empty",
                        List.of(),
                        8,
                        List.of()
                ),
                Arguments.of("releases start after sprint end",
                        Arrays.asList(
                                new int[]{6, 2},
                                new int[]{7, 1}
                        ),
                        5,
                        List.of()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidInputsWithoutShiftPossible")
    public void shouldReturnCorrectReleasesWithoutShiftWhenValidInput(
            String message,
            List<int[]> input,
            int sprintLength,
            List<int[]> expected) {

        List<int[]> actualReleases = ReleaseFinder.findMaxReleasesWithoutShiftPerSprint(input, sprintLength);

        assertArrayEquals(expected.toArray(), actualReleases.toArray(), message);
    }

    @ParameterizedTest
    @MethodSource("provideValidInputsWithShiftPossible")
    public void shouldReturnCorrectReleasesWithShiftWhenValidInput(String message,
                                                                   List<int[]> input,
                                                                   int sprintLength,
                                                                   List<int[]> expected) {

        List<int[]> actualReleases = ReleaseFinder.findMaxReleasesWithShiftPerSprint(input, sprintLength);

        assertArrayEquals(expected.toArray(), actualReleases.toArray(), message);
    }


    @ParameterizedTest
    @MethodSource("provideInvalidInputs")
    public void shouldReturnEmptyListWhenInvalidInputProvided(String message,
                                          List<int[]> input,
                                          int sprintLength,
                                          List<int[]> expected) {

        List<int[]> actualReleases = ReleaseFinder.findMaxReleasesWithoutShiftPerSprint(input, sprintLength);

        assertArrayEquals(expected.toArray(), actualReleases.toArray(), message);
    }
}
