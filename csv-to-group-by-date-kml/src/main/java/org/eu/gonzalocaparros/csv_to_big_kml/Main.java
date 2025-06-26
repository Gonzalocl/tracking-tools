package org.eu.gonzalocaparros.csv_to_big_kml;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {

    private static final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setSkipHeaderRecord(true)
            .setHeader(TrackingCsvHeaders.class)
            .build();

    private static final String PLACEMARK_TEMPLATE = getResourceAsString("templates/placemark.kml");
    private static final String DOCUMENT_TEMPLATE = getResourceAsString("templates/document.kml");

    private static String placemarkNameSuffix = "";

    public static void main(String[] args) {

        if (args.length != 2 && args.length != 3) {

            System.out.println("Need at least 2 arguments: input_directory output.kml [placemarkNameSuffix]");
            return;
        }

        if (args.length == 3) placemarkNameSuffix = args[2];

        try (var files = Files.list(Path.of(args[0]))) {

            var placemarks = files.filter(f -> f.toString().endsWith(".csv"))
                    .sorted()
                    .map(Main::processCsvFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.joining("\n"));

            var document = String.format(DOCUMENT_TEMPLATE, placemarks);

            Files.writeString(Path.of(args[1]), document);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> processCsvFile(Path path) {

        try {
            var csv = Files.readString(path);

            return processCsv(path, csv);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> processCsv(Path path, String csv) {

        var state = csvState(csv);

        System.out.println(path + " " + state);

        if (state == TrackingCsvState.LAST_LINE_ERROR) csv = fixCsvLastLine(csv);

        if (state != TrackingCsvState.EMPTY) {

            var placemarkName = getPlacemarkName(path);

            var placemark = csvToKmlPlacemark(csv, placemarkName);

            return Optional.of(placemark);
        }

        return Optional.empty();
    }

    private static String csvToKmlPlacemark(String csv, String placemarkName) {

        try (var reader = new StringReader(csv)) {

            var coordinates = csvFormat.parse(reader).stream()
                    .map(r -> String.format("%s,%s,0", r.get(TrackingCsvHeaders.longitude), r.get(TrackingCsvHeaders.latitude)))
                    .collect(Collectors.joining("\n"));

            return String.format(PLACEMARK_TEMPLATE, placemarkName, coordinates);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPlacemarkName(Path path) {
        return path.getFileName().toString().substring(0, 19) + placemarkNameSuffix;
    }

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

    private static String getResourceAsString(String resourceName) {

        try (var resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            return new String(resourceAsStream.readAllBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    enum TrackingCsvHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }

    enum TrackingCsvState {
        OK, EMPTY, LAST_LINE_ERROR
    }

}
