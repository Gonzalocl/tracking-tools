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

    public static void main(String[] args) {

        if (args.length != 2) {

            System.out.println("Need 2 arguments: input_directory output.kml");
            return;
        }

        try (var files = Files.list(Path.of(args[0]))) {

            var placemarks = files.filter(f -> f.toString().endsWith(".csv"))
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

            var state = csvState(csv);

            System.out.println(path + " " + state);

            if (state == TrackingCsvState.LAST_LINE_ERROR) {
                csv = fixCsvLastLine(csv);
            }

            if (state != TrackingCsvState.EMPTY) {

                var placemarkName = getPlacemarkName(path);

                var coordinates = csvFormat.parse(new StringReader(csv)).stream()
                        .map(r -> String.format("%s,%s,0", r.get(TrackingCsvHeaders.longitude), r.get(TrackingCsvHeaders.latitude)))
                        .collect(Collectors.joining("\n"));

                var placemark = String.format(PLACEMARK_TEMPLATE, placemarkName, coordinates);

                return Optional.of(placemark);
            }
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private static String getPlacemarkName(Path path) {
        return path.getFileName().toString().substring(0, 19);
    }

    private static String fixCsvLastLine(String csv) {
        return "";
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
