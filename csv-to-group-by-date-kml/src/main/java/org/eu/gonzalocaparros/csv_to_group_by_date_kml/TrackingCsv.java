package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.apache.commons.csv.CSVFormat;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TrackingCsv {

    private static final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setSkipHeaderRecord(true)
            .setHeader(TrackingCsvHeaders.class)
            .build();

    public static TrackingCsvData fixAndParseTrackingCsv(Path path, String csv) {

        var name = path.getFileName().toString().substring(0, 19);

        var state = csvState(csv);

        if (state == TrackingCsvState.EMPTY) return new TrackingCsvData(name, state, Collections.emptyList());

        if (state == TrackingCsvState.LAST_LINE_ERROR) csv = fixCsvLastLine(csv);

        var coordinates = parseCoordinates(csv);

        return new TrackingCsvData(name, state, coordinates);
    }

    private static List<Coordinates> parseCoordinates(String csv) {
        return Collections.emptyList();
    }

    public static String fixCsvLastLine(String csv) {

        var lastNewLine = csv.lastIndexOf("\n");

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

    record TrackingCsvData(String name, TrackingCsvState originalState, List<Coordinates> coordinates) {}

    enum TrackingCsvHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }

    enum TrackingCsvState {
        OK, EMPTY, LAST_LINE_ERROR
    }
}
