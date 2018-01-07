package engine.storageWorker;

import engine.exceptions.StorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

class XMLUtils {
    /**
     * Document methods
     */
    static Document getNewDocument() throws ParserConfigurationException {
        // Create DocumentBuilder
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        // Create document
        return docBuilder.newDocument();
    }

    static Document getExistingDocument(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document document = docBuilder.parse(file);

        document.getDocumentElement().normalize();

        return document;
    }

    static void saveDocument(Document sourceDocument, File targetFile) throws TransformerException, XPathExpressionException {
        // Create Transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Pretty print

        // Remove text blank elements
        XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//text()[normalize-space(.) = '']");
        NodeList blankTextNodes = (NodeList) xpath.evaluate(sourceDocument, XPathConstants.NODESET);

        for (int i = 0; i < blankTextNodes.getLength(); i++) {
            blankTextNodes.item(i).getParentNode().removeChild(blankTextNodes.item(i));
        }
        // Set indents
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //

        // Create source and target
        DOMSource domSource = new DOMSource(sourceDocument);
        StreamResult streamResult = new StreamResult(targetFile);

        // Save xml document
        transformer.transform(domSource, streamResult);
    }

    /**
     * Element methods
     */
    static Element addChildElementToParent(Document document, String childName, Element parent) {
        Element child = document.createElement(childName);
        parent.appendChild(child);

        return child;
    }

    static Element createRootElementOfDocument(Document document, String rootName) {
        // Create root element of document
        Element rootElement = document.createElement(rootName);
        document.appendChild(rootElement);

        return rootElement;
    }

    static boolean isElementExist(Document document, String tagName) {
        return document.getElementsByTagName(tagName).getLength() != 0;
    }

    static Element getElementByTagName(Document document, String tagName) throws StorageException {
        if (document.getElementsByTagName(tagName).getLength() > 1) throw new StorageException("Tag " + tagName + " is not unique in document " + document.getDocumentURI());

        return (Element) document.getElementsByTagName(tagName).item(0);
    }

    static Element getElementByTagName(Element parentElement, String tagName) throws StorageException {
        if (parentElement.getElementsByTagName(tagName).getLength() > 1)
            throw new StorageException("Tag " + tagName + " is not unique in element '" + parentElement.getTagName() + "' in " + parentElement.getBaseURI());

        return (Element) parentElement.getElementsByTagName(tagName).item(0);
    }
}
