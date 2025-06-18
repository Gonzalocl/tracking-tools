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

        csvFileStateTest(csv, Main.TrackingCsvState.OK);
    }

    private void csvFileStateTest(String csv, Main.TrackingCsvState expected) throws IOException {

        Files.writeString(testCsvFilePath, csv);

        SeekableByteChannel byteChannel = Files.newByteChannel(testCsvFilePath);

        Main.TrackingCsvState actual = Main.csvFileState(byteChannel);

        Assertions.assertEquals(expected, actual);
    }
}
