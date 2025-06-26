package org.eu.gonzalocaparros.csv_to_big_kml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class MainTest {

    private static final Path testCsvFilePath = Path.of("test.csv");

    @Test
    public void csvFileState_okFileTest() throws IOException {

        var csv = """
                a,b,c
                d,e,f
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvFileState_lastLineNoNewLineTest() throws IOException {

        var csv = """
                a,b,c
                d,e,f
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineIncompleteTest() throws IOException {

        var csv = """
                a,b,c
                d,e,f
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineMissingColonTest() throws IOException {

        var csv = """
                a,b,c
                d,e,f
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_lastLineOneCharacterTest() throws IOException {

        var csv = """
                a,b,c
                d,e,f
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvFileState_oneLineOkFileTest() throws IOException {

        var csv = """
                a,b,c
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvFileState_oneLineNoNewLineTest() throws IOException {

        var csv = """
                a,b,c
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineIncompleteTest() throws IOException {

        var csv = """
                a,b,c
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineMissingColonTest() throws IOException {

        var csv = """
                a,b,c
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_oneLineOneCharacterTest() throws IOException {

        var csv = """
                a,b,c
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineOnlyTest() throws IOException {

        var csv = """
                g,h,i
                """;

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineNoNewLineTest() throws IOException {

        var csv = """
                g,h,i""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineIncompleteTest() throws IOException {

        var csv = """
                g,h,""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineMissingColonTest() throws IOException {

        var csv = """
                g,h""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_headerLineOneCharacterTest() throws IOException {

        var csv = """
                g""";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_onlyNewLineFileTest() throws IOException {

        var csv = "\n";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvFileState_emptyFileTest() throws IOException {

        var csv = "";

        csvFileStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    private void csvFileStateTest(String csv, Main.TrackingCsvState expected) {

        var actual = Main.csvState(csv);

        Assertions.assertEquals(expected, actual);
    }
}
