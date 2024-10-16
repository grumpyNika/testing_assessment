package com.nda.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil {
    private static final Logger LOGGER = LogManager.getLogger(FileUtil.class);

    public static List<String> readInputFile(String inputFileName) throws IOException {
        LOGGER.info("Reading input file. File path: {}.", inputFileName);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = createBufferedReader(inputFileName)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            LOGGER.info("Finished reading input file.");
        } catch (FileNotFoundException e) {
            LOGGER.error("The file {} does not exist.", inputFileName);
            throw e;
        } catch (IOException e) {
            LOGGER.error("An I/O error occurred while reading the file {}: {}", inputFileName, e.getMessage());
            throw e;
        }

        return lines;
    }

    public static void writeResultTableToFile(List<int[]> resultTable, String outputFileName) throws IOException {
        LOGGER.info("Writing output file...");
        Path outputPath = Paths.get(outputFileName);
        Path parentDir = outputPath.getParent();
        try {
            createParentDirectoriesIfNotExist(parentDir);
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                writer.write(resultTable.size() + "\n");
                for (int[] row : resultTable) {
                    writeRow(writer, row);
                }
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the output file: ", e);
            throw e;
        }
        LOGGER.info("Finished writing output file. File path: {}.", outputPath);
    }

    private static void createParentDirectoriesIfNotExist(Path parentDir) throws IOException {
        if (parentDir == null) {
            LOGGER.warn("Parent directory path is null");
            return;
        }
        if (Files.notExists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
                LOGGER.info("Created directory: {}", parentDir);
            } catch (IOException e) {
                LOGGER.error("Failed to create directory: {} due to {}", parentDir, e);
                throw e;
            }
        }
    }

    private static void writeRow(BufferedWriter writer, int[] row) throws IOException {
        try {
            String rowString = Arrays.stream(row)
                    .mapToObj(String::valueOf)
                    .reduce((a, b) -> a + " " + b)
                    .orElse("") + "\n";
            writer.write(rowString);
        } catch (IOException e) {
            throw new IOException("Failed to write row: " + Arrays.toString(row), e);
        }
    }

    private static BufferedReader createBufferedReader(String inputFileName) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(inputFileName);
        return new BufferedReader(new InputStreamReader(inputStream));
    }

}
