package org.eu.gonzalocaparros.tracklogcsv;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
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
        final List<String> idx = extractColumn(doc, "#idx");
        final List<String> ts = extractColumn(doc, "#ts");
        final List<String> lat = extractColumn(doc, "#lat");
        final List<String> lon = extractColumn(doc, "#lon");
        final List<String> fhd = extractColumn(doc, "#fhd > div");
        final List<String> fal = extractColumn(doc, "#fal");
        final List<String> fgs = extractColumn(doc, "#fgs");
        final List<String> fvr = extractColumn(doc, "#fvr > div");
        final List<String> so = extractColumn(doc, "#so");
        final List<String> sq = extractColumn(doc, "#sq");

        final String id = doc.select("#main > a").text();
        final String date = ts.get(1).split(" ")[0];

        final int numRows = idx.size();
        if (ts.size() != numRows ||
                lat.size() != numRows ||
                lon.size() != numRows ||
                fhd.size() != numRows ||
                fal.size() != numRows ||
                fgs.size() != numRows ||
                fvr.size() != numRows ||
                so.size() != numRows ||
                sq.size() != numRows) {

            System.out.println("WARNING: the number of rows of each column does not match.");
        }

        final String outputFilename = String.format("%s %s", date, id);

        final String outputHtmlFilePath = outputFilename + ".html";
        Files.writeString(Paths.get(outputHtmlFilePath), logHtml);

    }

    private static List<String> extractColumn(Document doc, String selector) {
        return doc.select(selector).stream()
                .map(Element::text)
                .toList();
    }

}
