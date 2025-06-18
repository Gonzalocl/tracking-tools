package org.eu.gonzalocaparros.csv_to_big_kml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTest {

    private static final Path testCsvFilePath = Path.of("test.csv");

    @Test
    public void csvFileStateFileOk() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g,h,i
                """;

        Main.TrackingCsvState result = Main.csvFileState(getTestCsvStringAsByteChannel(csv));

        Assertions.assertEquals(Main.TrackingCsvState.OK, result);
    }

    private SeekableByteChannel getTestCsvStringAsByteChannel(String csv) throws IOException {

        Files.writeString(testCsvFilePath, csv);

        return Files.newByteChannel(testCsvFilePath);
    }
}
