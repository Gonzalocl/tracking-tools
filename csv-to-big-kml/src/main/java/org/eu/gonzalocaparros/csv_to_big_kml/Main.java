package org.eu.gonzalocaparros.csv_to_big_kml;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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

        try (var files = Files.list(Path.of(args[0]))) {

            files.filter(f -> f.toString().endsWith(".csv"))
                    .forEach(Main::processCsvFile);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private static Optional<String> processCsvFile(Path path) {

        try (var channel = Files.newByteChannel(path)) {

            var state = csvFileState(channel);

            System.out.println(path + " " + state);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public static TrackingCsvState csvFileState(SeekableByteChannel channel) throws IOException {

        var size = channel.size();

        if (size == 0) return TrackingCsvState.EMPTY;

        var byteBuffer = ByteBuffer.allocate(1024);

        var readBytes = channel.read(byteBuffer);
        byteBuffer.position(0);

        var i = 0;
        var newLineFound = 0;

        while (i < readBytes && newLineFound < 2) {
            i++;

            if (byteBuffer.get() == 0x0A) newLineFound++;
        }

        if (readBytes == i && newLineFound < 2) return TrackingCsvState.EMPTY;

        channel.position(size - 1);
        byteBuffer.position(0);
        readBytes = channel.read(byteBuffer);

        if (readBytes != 1) throw new RuntimeException();

        if (byteBuffer.get(0) == 0x0A) return TrackingCsvState.OK;

        return TrackingCsvState.LAST_LINE_ERROR;
    }

    enum TrackingCsvHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }

    enum TrackingCsvState {
        OK, EMPTY, LAST_LINE_ERROR
    }

}
