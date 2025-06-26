package org.eu.gonzalocaparros.csv_to_group_by_date_kml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

        xmlDocument.appendChild(kmlElement);
        kmlElement.appendChild(kmlDocument);
        kmlDocument.appendChild(nameElement);

        return new Kml(xmlDocument, kmlDocument);
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
