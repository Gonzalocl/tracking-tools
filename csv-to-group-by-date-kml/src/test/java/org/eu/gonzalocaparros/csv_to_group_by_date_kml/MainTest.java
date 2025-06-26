package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void csvState_okFileTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h,i
                """;

        csvStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvState_lastLineNoNewLineTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h,i""";

        csvStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvState_lastLineIncompleteTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h,""";

        csvStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvState_lastLineMissingColonTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h""";

        csvStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvState_lastLineOneCharacterTest() {

        var csv = """
                a,b,c
                d,e,f
                g""";

        csvStateTest(csv, Main.TrackingCsvState.LAST_LINE_ERROR);
    }

    @Test
    public void csvState_oneLineOkFileTest() {

        var csv = """
                a,b,c
                g,h,i
                """;

        csvStateTest(csv, Main.TrackingCsvState.OK);
    }

    @Test
    public void csvState_oneLineNoNewLineTest() {

        var csv = """
                a,b,c
                g,h,i""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_oneLineIncompleteTest() {

        var csv = """
                a,b,c
                g,h,""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_oneLineMissingColonTest() {

        var csv = """
                a,b,c
                g,h""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_oneLineOneCharacterTest() {

        var csv = """
                a,b,c
                g""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_headerLineOnlyTest() {

        var csv = """
                g,h,i
                """;

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_headerLineNoNewLineTest() {

        var csv = """
                g,h,i""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_headerLineIncompleteTest() {

        var csv = """
                g,h,""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_headerLineMissingColonTest() {

        var csv = """
                g,h""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_headerLineOneCharacterTest() {

        var csv = """
                g""";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_onlyNewLineFileTest() {

        var csv = "\n";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    @Test
    public void csvState_emptyFileTest() {

        var csv = "";

        csvStateTest(csv, Main.TrackingCsvState.EMPTY);
    }

    private void csvStateTest(String csv, Main.TrackingCsvState expected) {

        var actual = Main.csvState(csv);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void fixCsvLastLine_lastLineNoNewLineTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h,i""";

        var expected = """
                a,b,c
                d,e,f
                """;

        fixCsvLastLineTest(csv, expected);
    }

    @Test
    public void fixCsvLastLine_lastLineIncompleteTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h,""";

        var expected = """
                a,b,c
                d,e,f
                """;

        fixCsvLastLineTest(csv, expected);
    }

    @Test
    public void fixCsvLastLine_lastLineMissingColonTest() {

        var csv = """
                a,b,c
                d,e,f
                g,h""";

        var expected = """
                a,b,c
                d,e,f
                """;

        fixCsvLastLineTest(csv, expected);
    }

    @Test
    public void fixCsvLastLine_lastLineOneCharacterTest() {

        var csv = """
                a,b,c
                d,e,f
                g""";

        var expected = """
                a,b,c
                d,e,f
                """;

        fixCsvLastLineTest(csv, expected);
    }

    private void fixCsvLastLineTest(String csv, String expected) {

        var actual = Main.fixCsvLastLine(csv);

        Assertions.assertEquals(expected, actual);
    }
}
