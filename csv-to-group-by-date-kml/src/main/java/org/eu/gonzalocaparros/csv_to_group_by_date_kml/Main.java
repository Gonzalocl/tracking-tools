package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {

            System.out.println("Need 2 arguments: input_directory output.kml");
            return;
        }

        var inputDirectory = Path.of(args[0]);

        var tracks = parseTracks(inputDirectory);
        var dayLabels = readDayLabels(inputDirectory);

        buildKmlDocument(tracks, dayLabels, Path.of(args[1]));
    }

    private static Collection<Track> parseTracks(Path inputDirectory) {

        try (var directories = Files.list(inputDirectory)) {

            return directories.filter(Files::isDirectory)
                    .flatMap(Main::parseTracksDirectory)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Track> parseTracksDirectory(Path path) {

        var label = readTracksDirectoryLabel(path);

        try {
            var files = Files.list(path);
            return files.filter(f -> f.toString().endsWith(".csv"))
                    .map(f -> parseTrack(f, label));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static void buildKmlDocument(Collection<Track> tracks, Map<String, String> dayLabels, Path outputFile) {

        var groupedTracks = removeEmptyAndGroupTracks(tracks);
    }

    private static Map<String, Map<String, List<Track>>> removeEmptyAndGroupTracks(Collection<Track> tracks) {
        try (var stream = tracks.stream()) {
            return stream.filter(t -> t.track().originalState() != TrackingCsv.TrackingCsvState.EMPTY)
                    .collect(Collectors.groupingBy(Track::date, Collectors.groupingBy(Track::label)));
        }
    }

    record Track(String date, String label, TrackingCsv.TrackingCsvData track) {}
}
