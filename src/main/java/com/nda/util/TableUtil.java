package com.nda.util;

import com.nda.exception.InputValidationException;

import java.util.ArrayList;
import java.util.List;

import static com.nda.common.Constants.*;

public class TableUtil {

    public static List<int[]> parseInputToTable(List<String> lines, int sprintLength) throws InputValidationException {
        validateSprintLength(sprintLength);

        List<int[]> parsedTable = new ArrayList<>();
        int lineNumber = 0;

        for (String line : lines) {
            lineNumber++;
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            try {
                int[] row = processLine(trimmedLine, lineNumber, sprintLength);
                parsedTable.add(row);
            } catch (InputValidationException ex) {
                throw new InputValidationException("Error at line " + lineNumber + ": " + ex.getMessage());
            }
        }
        return parsedTable;
    }


    private static int[] processLine(String line, int lineNumber, int sprintLength) throws InputValidationException {
        String[] parts = line.split("\\s+");
        int partsCount = parts.length;

        if (partsCount != NUM_COLUMNS) {
            throw new InputValidationException("Invalid structure: expected " + NUM_COLUMNS + " integers, " +
                    "found " + partsCount);
        }

        int[] currentRelease = new int[NUM_COLUMNS];

        try {
            int releaseDay = Integer.parseInt(parts[START_DAY_INDEX]);
            int estimation = Integer.parseInt(parts[FINISH_INDEX]);

            validateReleaseDay(releaseDay, sprintLength);
            validateEstimation(estimation);

            currentRelease[START_DAY_INDEX] = releaseDay;
            currentRelease[FINISH_INDEX] = estimation;
        } catch (NumberFormatException e) {
            throw new InputValidationException("Invalid integer on line " + lineNumber + ": " + e.getMessage());
        }
        return currentRelease;
    }

    private static void validateSprintLength(int sprintLength) throws InputValidationException {
        if(sprintLength < 1) {
            throw new InputValidationException("Invalid sprint length provided. " +
                    "Expected value from 1, found: " + sprintLength);
        }
    }

    private static void validateReleaseDay(int releaseDay, int sprintLength) throws InputValidationException {
        if (releaseDay < SPRINT_START_DAY || releaseDay > sprintLength) {
            throw new InputValidationException("Invalid release day found. " +
                    "Expected value from " + SPRINT_START_DAY + " to " + sprintLength + ", found: " + releaseDay);
        }
    }

    private static void validateEstimation(int estimation) throws InputValidationException {
        if (estimation <= 0) {
            throw new InputValidationException("Negative or zero release estimation found: " + estimation );
        }
    }

}
