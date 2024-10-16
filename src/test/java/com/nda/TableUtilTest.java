package com.nda;

import com.nda.util.TableUtil;
import com.nda.exception.InputValidationException;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TableUtilTest {

    private static Stream<Arguments> provideInvalidInputs() {
        return Stream.of(
                Arguments.of(List.of("0 0"), 0, "Invalid sprint length provided."),
                Arguments.of(List.of("0 7"), 8, "Invalid release day found. Expected value from 1 to 8"),
                Arguments.of(List.of("-10 1"), 6, "Invalid release day found. Expected value from 1 to 6"),
                Arguments.of(List.of("500 7"), 20, "Invalid release day found. Expected value from 1 to 20"),
                Arguments.of(List.of("5 0"), 10, "Negative or zero release estimation found"),
                Arguments.of(List.of("5 -1"), 10, "Negative or zero release estimation found"),
                Arguments.of(List.of("a t", "7 3"), 10, "Invalid integer on line 1"),
                Arguments.of(List.of("1 5", "a 3"), 10, "Invalid integer on line 2"),
                Arguments.of(List.of("1 5 6"), 10, "Invalid structure: expected 2 integers, found 3")
        );
    }

    private static Stream<Arguments> provideValidInputs() {
        return Stream.of(
                Arguments.of(Arrays.asList("1 2", "3 4", "5 6"),
                        6,
                        Arrays.asList(new int[]{1, 2}, new int[]{3, 4}, new int[]{5, 6})
                ),
                Arguments.of(Arrays.asList("1     20", "3    5  ", "   7    6"),
                        7,
                        Arrays.asList(new int[]{1, 20}, new int[]{3, 5}, new int[]{7, 6})
                ),
                Arguments.of(Arrays.asList("1 2", "", "5 6", "\t"),
                        10,
                        Arrays.asList(new int[]{1, 2}, new int[]{5, 6})
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInputs")
    public void shouldThrowExceptionForInvalidInputs(List<String> input, int springLength, String expectedMessage) {
        Executable executable = () -> TableUtil.parseInputToTable(input, springLength);

        InputValidationException exception = assertThrows(InputValidationException.class, executable);
        assertTrue(exception.getMessage().contains(expectedMessage), "Error message should contain text");
    }

    @ParameterizedTest
    @MethodSource("provideValidInputs")
    public void shouldParseProperlyWhenInputIsCorrect(List<String> input, int sprintLength, List<int[]> expected) {
        List<int[]> actual = TableUtil.parseInputToTable(input, sprintLength);

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

}
