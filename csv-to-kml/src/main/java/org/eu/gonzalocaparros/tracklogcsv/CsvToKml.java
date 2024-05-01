package org.eu.gonzalocaparros.tracklogcsv;

import org.apache.commons.csv.CSVFormat;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CsvToKml {

    private static final String INPUT_FOLDER = "in";
    private static final String OUTPUT_FOLDER = "out";

    public static void main(String[] args) {

        try {

            Files.list(Paths.get(INPUT_FOLDER)).forEach(CsvToKml::processFile);
        } catch (IOException e) {

            System.out.println("Failed to list files.");
            e.printStackTrace();
        }

    }

    private static void processFile(Path path) {

        if (Files.isDirectory(path)) {

            System.out.println("Skipping folder: " + path);
            return;
        }

        try (final FileReader fileReader = new FileReader(path.toString())) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setSkipHeaderRecord(true)
                    .setHeader(TrackHeaders.class)
                    .build();

            final String coordinatesString = csvFormat.parse(fileReader).stream()
                    .map(r -> String.format("%s,%s,0", r.get(TrackHeaders.longitude), r.get(TrackHeaders.latitude)))
                    .collect(Collectors.joining("\n"));

            final InputStream is = CsvToKml.class.getClassLoader()
                    .getResourceAsStream("simple-line-string-kml-document-template.kml");
            final String templateKml = new String(is.readAllBytes());
            is.close();

            final String outputFilename = path.getFileName().toString().split("\\.")[0];

            final String kmlString = String.format(templateKml, outputFilename, outputFilename, coordinatesString);

            final Path outputKmlFilePath = Paths.get(OUTPUT_FOLDER, outputFilename + ".kml");
            Files.writeString(outputKmlFilePath, kmlString);

        } catch (IOException e) {

            System.out.println("Failed to convert: " + path);
            e.printStackTrace();
        }
    }

    enum TrackHeaders {
        latitude, longitude, altitude, accuracy, timestamp
    }
}
