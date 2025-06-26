package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {

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
}
