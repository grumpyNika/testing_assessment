package com.nda;

import com.nda.logic.ReleaseFinder;
import com.nda.util.FileUtil;
import com.nda.util.TableUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

import static com.nda.common.Constants.*;

public class Runner {

    private static final Logger LOGGER = LogManager.getLogger(Runner.class);

    public static void main(String[] args) {
        try {
            List<String> inputFile = readInputFile();
            List<int[]> inputTable = convertInputToTable(inputFile);
            //main task
            List<int[]> releasesWithoutShift = calculateMaxReleasesWithoutShiftPerSprint(inputTable);
            writeOutputToFile(releasesWithoutShift, OUTPUT_FILE_NAME);

            //bonus task
            List<int[]> releasesWithShift = calculateMaxReleasesWithShiftPerSprint(inputTable);
            writeOutputToFile(releasesWithShift, BONUS_TASK_OUTPUT_FILE_NAME);

        } catch (Exception e) {
            LOGGER.error("Error processing the file: {}", e.getMessage(), e);
        }
    }

    //here and below I use files and sprint length as constants, but it can actually be parameterized if needed
    private static List<String> readInputFile() throws IOException {
        return FileUtil.readInputFile(INPUT_FILE_NAME);
    }

    private static List<int[]> convertInputToTable(List<String> inputFile) {
        return TableUtil.parseInputToTable(inputFile, SPRINT_LENGTH);
    }

    private static List<int[]> calculateMaxReleasesWithoutShiftPerSprint(List<int[]> inputTable) {
        return ReleaseFinder.findMaxReleasesWithoutShiftPerSprint(inputTable, SPRINT_LENGTH);
    }

    private static List<int[]> calculateMaxReleasesWithShiftPerSprint(List<int[]> inputTable) {
        return ReleaseFinder.findMaxReleasesWithShiftPerSprint(inputTable, SPRINT_LENGTH);
    }

    private static void writeOutputToFile(List<int[]> outputTable, String fileName) throws IOException {
        FileUtil.writeResultTableToFile(outputTable, fileName);
    }

}
