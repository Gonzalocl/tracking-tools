package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {

            System.out.println("Need 2 arguments: input_directory output.kml");
            return;
        }

        var inputDirectory = Path.of(args[0]);

        var dayLabels = readDayLabels(inputDirectory);
    }

    private static String readTracksDirectoryLabel(Path tracksDirectory) {
        try (var lines = Files.lines(tracksDirectory.resolve("properties"))) {
            return lines.findFirst().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Track parseTrack(Path path, String label) {

        var date = path.getFileName().toString().substring(0, 10);
        var track = TrackingCsv.processTrackingCsv(path);

        return new Track(date, label, track);
    }

    private static Map<String, String> readDayLabels(Path inputDirectory) {

        try (var reader = Files.newBufferedReader(inputDirectory.resolve("day_labels.csv"));
             var stream = CSVFormat.DEFAULT.parse(reader).stream()) {

            return stream.collect(Collectors.toMap(r -> r.get(0), r -> r.get(1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record Track(String date, String label, TrackingCsv.TrackingCsvData track) {}
}
