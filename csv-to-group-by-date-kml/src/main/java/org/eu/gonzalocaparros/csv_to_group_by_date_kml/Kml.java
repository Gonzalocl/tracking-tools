package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Kml {

    private final Document xmlDocument;
    private final Element kmlDocument;

    private Kml(Document xmlDocument, Element kmlDocument) {

        this.xmlDocument = xmlDocument;
        this.kmlDocument = kmlDocument;
    }

    public static Kml newDocument(String name) {
        var xmlDocument = getDocumentBuilder().newDocument();
        var kmlElement = xmlDocument.createElement("kml");
        var kmlDocument = xmlDocument.createElement("Document");
        var nameElement = xmlDocument.createElement("name");

        kmlElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
        kmlElement.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
        kmlElement.setAttribute("xmlns:kml", "http://www.opengis.net/kml/2.2");
        kmlElement.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");

        nameElement.setTextContent(name);

        xmlDocument.appendChild(kmlElement)
                .appendChild(kmlDocument)
                .appendChild(nameElement);

        return new Kml(xmlDocument, kmlDocument);
    }

    public void addStyle(String id, String color, int width) {
        var styleElement = xmlDocument.createElement("Style");
        var lineStyleElement = xmlDocument.createElement("LineStyle");
        var colorElement = xmlDocument.createElement("color");
        var widthElement = xmlDocument.createElement("width");

        styleElement.setAttribute("id", id);
        colorElement.setTextContent(color);
        widthElement.setTextContent(Integer.toString(width));

        kmlDocument.appendChild(styleElement)
                .appendChild(lineStyleElement);
        lineStyleElement.appendChild(colorElement);
        lineStyleElement.appendChild(widthElement);
    }

    public Node newFolder(String name) {
        var folderElement = xmlDocument.createElement("Folder");
        var nameElement = xmlDocument.createElement("name");

        nameElement.setTextContent(name);

        folderElement.appendChild(nameElement);

        return folderElement;
    }

    public Node newLineStringPlacemark(String name, String styleId, String coordinates) {
        var placemarkElement = xmlDocument.createElement("Placemark");
        var nameElement = xmlDocument.createElement("name");
        var styleUrlElement = xmlDocument.createElement("styleUrl");
        var lineStringElement = xmlDocument.createElement("LineString");
        var coordinatesElement = xmlDocument.createElement("coordinates");

        nameElement.setTextContent(name);
        styleUrlElement.setTextContent("#" + styleId);
        coordinatesElement.setTextContent(coordinates);

        placemarkElement.appendChild(nameElement);
        placemarkElement.appendChild(styleUrlElement);
        placemarkElement.appendChild(lineStringElement)
                .appendChild(coordinatesElement);

        return placemarkElement;
    }

    public Node appendChild(Node folder) {
        return kmlDocument.appendChild(folder);
    }

    public static String formatCoordinates(List<Coordinates> coordinates) {

        try (var stream = coordinates.stream()) {

            return stream.map(c -> String.format("%s,%s,0", c.longitude(), c.latitude()))
                    .collect(Collectors.joining("\n"));
        }
    }

    public void writeToFile(Path path) {
        try {
            TransformerFactory.newInstance().newTransformer()
                    .transform(new DOMSource(xmlDocument), new StreamResult(Files.newOutputStream(path)));
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
