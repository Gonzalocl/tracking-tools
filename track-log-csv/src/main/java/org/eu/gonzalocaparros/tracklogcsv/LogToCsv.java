package org.eu.gonzalocaparros.tracklogcsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class LogToCsv {

    public static void main(String[] args) throws IOException {

//        final String url = "";
//        final Document doc = Jsoup.connect(url).get();

        final String inputFilePath = "input.html";
        final Document doc = Jsoup.parse(Paths.get(inputFilePath).toFile());

        final String logHtml = doc.html();

        final List<String> headers = extractRow(doc.select("#content > table > thead > tr").first());

        final Elements elements = doc.select("#content > table > tbody > tr");

        final List<List<String>> rows = elements.stream()
                .filter(e -> !e.hasClass("header") && !e.hasClass("spacer"))
                .map(LogToCsv::extractRow)
                .toList();

        final String id = doc.select("#main > a").text();
        final String date = rows.get(0).get(1).split(" ")[0];

        final String outputFilename = String.format("%s %s", date, id);

        final String outputHtmlFilePath = outputFilename + ".html";
        Files.writeString(Paths.get(outputHtmlFilePath), logHtml);

        StringWriter sw = new StringWriter();
        CSVFormat aDefault = CSVFormat.DEFAULT.builder()
                .setHeader(headers.toArray(String[]::new))
                .build();

        CSVPrinter csvPrinter = new CSVPrinter(sw, aDefault);
        csvPrinter.printRecords(rows);

        final String outputCsvFilePath = outputFilename + ".csv";
        Files.writeString(Paths.get(outputCsvFilePath), sw.toString());

        csvPrinter.close();

        final String coordinatesString = rows.stream()
                .map(r -> String.format("%s,%s,0", r.get(3), r.get(2)))
                .collect(Collectors.joining("\n"));

        final InputStream is = LogToCsv.class.getClassLoader().getResourceAsStream("simple-line-string-kml-document-template.kml");
        final String templateKml = new String(is.readAllBytes());
        is.close();

        final String kmlString = String.format(templateKml, outputFilename, outputFilename, coordinatesString);

        final String outputKmlFilePath = outputFilename + ".kml";
        Files.writeString(Paths.get(outputKmlFilePath), kmlString);

    }

    private static List<String> extractRow(Element element) {
        return List.of(
                element.select("#idx").text(),
                element.select("#ts").text(),
                element.select("#lat").text(),
                element.select("#lon").text(),
                element.select("#fhd").text(),
                element.select("#fal").text(),
                element.select("#fgs").text(),
                element.select("#fvr").text(),
                element.select("#so").text(),
                element.select("#sq").text()
        );
    }

}
