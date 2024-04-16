package org.eu.gonzalocaparros.tracklogcsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
                .skip(1)
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
