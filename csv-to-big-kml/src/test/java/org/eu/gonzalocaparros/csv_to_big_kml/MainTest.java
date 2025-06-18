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
    public void csvFileState_okFileTest() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvFileState_lastLineNoNewLineTest() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineIncompleteTest() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineMissingColonTest() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineOneCharacterTest() throws IOException {

        String csv = """
                a,b,c
                d,e,f
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_oneLineOkFileTest() throws IOException {

        String csv = """
                a,b,c
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvFileState_oneLineNoNewLineTest() throws IOException {

        String csv = """
                a,b,c
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineIncompleteTest() throws IOException {

        String csv = """
                a,b,c
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineMissingColonTest() throws IOException {

        String csv = """
                a,b,c
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineOneCharacterTest() throws IOException {

        String csv = """
                a,b,c
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineOnlyTest() throws IOException {

        String csv = """
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineNoNewLineTest() throws IOException {

        String csv = """
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineIncompleteTest() throws IOException {

        String csv = """
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineMissingColonTest() throws IOException {

        String csv = """
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineOneCharacterTest() throws IOException {

        String csv = """
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    private void csvFileStateTest(String csv, Main.TrackingCsvState expected) throws IOException {

        Files.writeString(testCsvFilePath, csv);

        SeekableByteChannel byteChannel = Files.newByteChannel(testCsvFilePath);

        Main.TrackingCsvState actual = Main.csvFileState(byteChannel);

        Assertions.assertEquals(expected, actual);
    }
}
