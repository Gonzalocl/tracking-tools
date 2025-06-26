package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.apache.commons.csv.CSVFormat;

public class TrackingCsv {

    private static final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setSkipHeaderRecord(true)
            .setHeader(TrackingCsvHeaders.class)
            .build();

    public static String fixCsvLastLine(String csv) {

        int lastNewLine = csv.lastIndexOf("\n");

        return csv.substring(0, lastNewLine + 1);
    }

    public static TrackingCsvState csvState(String csv) {

        var length = csv.length();
        var i = 0;
        var newLineFound = 0;

        while (i < length && newLineFound < 2) {

            if (csv.charAt(i) == 0x0A) newLineFound++;

            i++;
        }

        if (i == length && newLineFound < 2) return TrackingCsvState.EMPTY;

        if (csv.charAt(length - 1) != 0x0A) return TrackingCsvState.LAST_LINE_ERROR;

        return TrackingCsvState.OK;
    }

    enum TrackingCsvHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }

    enum TrackingCsvState {
        OK, EMPTY, LAST_LINE_ERROR
    }
}
