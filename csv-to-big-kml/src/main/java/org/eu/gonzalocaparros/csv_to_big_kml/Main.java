package org.eu.gonzalocaparros.csv_to_big_kml;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {

    private static final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setSkipHeaderRecord(true)
            .setHeader(TrackingCsvHeaders.class)
            .build();

    public static void main(String[] args) {

        if (args.length != 2) {

            System.out.println("Need 2 arguments: input_directory output.kml");
            return;
        }

        try (Stream<Path> files = Files.list(Path.of(args[0]))) {

            files.filter(f -> f.toString().endsWith(".csv"))
                    .forEach(Main::processCsvFile);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    private static void processCsvFile(Path path) {

    }

    public static TrackingCsvState csvFileState(SeekableByteChannel channel) {

        return TrackingCsvState.EMPTY;
    }

    enum TrackingCsvHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }

    enum TrackingCsvState {
        OK, EMPTY, LAST_LINE_ERROR
    }

}
