package com.nda;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.nda.util.FileUtil;

public class FileUtilTest {

    @Test
    public void shouldReadInputFileWhenSuccess(@TempDir Path tempDir) throws IOException {
        String inputFileName = "testInput.txt";
        Path inputFile = tempDir.resolve(inputFileName);
        Files.write(inputFile, Arrays.asList("1 2", "3 4"));

        List<String> lines = assertDoesNotThrow(() -> FileUtil.readInputFile(inputFile.toString()),
                "File reading should not throw an exception.");

        assertAll("input file",
                () -> assertEquals(2, lines.size(), "The file should contain 2 lines."),
                () -> assertEquals("1 2", lines.get(0), "First line should be '1 2'."),
                () -> assertEquals("3 4", lines.get(1), "Second line should be '3 4'.")
        );
    }

    @Test
    public void shouldReadInputFileWhenFileEmpty(@TempDir Path tempDir) throws IOException {
        String inputFileName = "testEmptyInput.txt";
        Path inputFile = tempDir.resolve(inputFileName);
        Files.createFile(inputFile);

        List<String> lines = assertDoesNotThrow(() -> FileUtil.readInputFile(inputFile.toString()),
                "File reading should not throw an exception.");

        assertTrue(lines.isEmpty(), "The file should be empty.");
    }

    @Test
    public void shouldThrowFileNotFoundExceptionWhenFileNotExist() {
        String inputFileName = "noSuchFile.txt";

        assertThrows(FileNotFoundException.class, () -> FileUtil.readInputFile(inputFileName),
                "Reading a non-existent file should throw FileNotFoundException.");
    }

    @Test
    public void shouldWriteResultTableToFileWhenSuccess(@TempDir Path tempDir) throws IOException {
        List<int[]> resultTable = Stream.of(
                new int[]{1, 2},
                new int[]{3, 4},
                new int[]{5, 6}
        ).collect(Collectors.toList());
        Path outputFile = tempDir.resolve("testOutput.txt");

        assertDoesNotThrow(() -> FileUtil.writeResultTableToFile(resultTable, outputFile.toString()));

        try (BufferedReader br = new BufferedReader(new FileReader(outputFile.toString()))) {
            assertAll("output file",
                    () -> assertEquals("3", br.readLine()),
                    () -> assertEquals("1 2", br.readLine()),
                    () -> assertEquals("3 4", br.readLine()),
                    () -> assertEquals("5 6", br.readLine())
            );
        }
    }

    @Test
    public void shouldWriteResultTableToFileWhenResultEmpty(@TempDir Path tempDir) throws IOException {
        List<int[]> resultTable = new ArrayList<>();
        Path outputFile = tempDir.resolve("testEmptyOutput.txt");

        assertDoesNotThrow(() -> FileUtil.writeResultTableToFile(resultTable, outputFile.toString()));

        try (BufferedReader br = new BufferedReader(new FileReader(outputFile.toString()))) {
            assertAll("empty output file",
                    () -> assertEquals("0", br.readLine()),
                    () -> assertNull(br.readLine())
            );
        }
    }

}
