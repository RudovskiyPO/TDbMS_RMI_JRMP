package engine.storageWorker;

import engine.core.*;
import engine.exceptions.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static engine.storageWorker.XMLUtils.*;

public class TableStorage {
    /**
     * Save table to database
     */
    static void laydownTableToDataBase(Table table, File databaseFile) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ColumnException, StorageException {
        Document document = getExistingDocument(databaseFile);

        Element tables = getElementByTagName(document, "tables");

        // Set table element
        Element tableElement;
        if (isElementExist(document, table.getName())) {
            tables.replaceChild(document.createElement(table.getName()), getElementByTagName(document, table.getName()));
            tableElement = getElementByTagName(document, table.getName());
        } else tableElement = addChildElementToParent(document, table.getName(), tables);

        tableElement.setAttribute("class", "table");

        // Set table columns
        Element columnsElement = addChildElementToParent(document, "columns", tableElement);
        // Set each column element
        for (Column column : table.getColumns()) {
            // Create column element
            Element columnElement = addChildElementToParent(document, column.getName(), columnsElement);
            columnElement.setAttribute("class", "column");
            // Set column element type
            columnElement.setAttribute("type", column.getTypeString());
            // Set column element isPrimary
            columnElement.setAttribute("isPrimary", Boolean.toString(column.isPrimary()));
            // Set column element intervalRegex
            columnElement.setAttribute("intervalRegex", column.getIntervalRegex());
        }

        // Set table notes
        Element notesElement = addChildElementToParent(document, "notes", tableElement);
        // Set each note element
        for (Note note : table.getNotes()) {
            // Create note element
            Element noteElement = addChildElementToParent(document, "note", notesElement);
            noteElement.setAttribute("class", "note");
            // Set each note cell
            for (Cell cell : note.getCells()) {
                // Create cell element
                Element cellElement = addChildElementToParent(document, "cell", noteElement);
                cellElement.setAttribute("class", "cell");
                // Set cell element column
                cellElement.setAttribute("column", cell.getColumn().getName());
                // Set cell element content
                cellElement.setTextContent(cell.getContent());
            }
        }

        saveDocument(document, databaseFile);
    }

    /**
     * Export table from database
     */
    static void exportTable(File databaseFile, String tableName) throws IOException, SAXException, ParserConfigurationException, StorageException, TransformerException, XPathExpressionException {
        Document sourceDocument = getExistingDocument(databaseFile);

        // Get table element
        Element table = getElementByTagName(sourceDocument, tableName);

        // Create new document
        Document exportDocument = getNewDocument();
        // Create root as table
        Node tableExportDocument = table.cloneNode(true);

        // Set node in document
        exportDocument.adoptNode(tableExportDocument);
        exportDocument.appendChild(tableExportDocument);

        // Create exportFile
        File exportFile = new File(databaseFile.getParent() + "/export/" + tableName + ".xml");
        // Check export directory
        if(!exportFile.getParentFile().exists() && !exportFile.getParentFile().mkdir())
            throw new StorageException("Exception during creation directory: " + exportFile.getParent());
        // Check export file
        if(exportFile.exists() && !exportFile.delete())
            throw new StorageException("Exception during deletion file: " + exportFile);
        // Save export file
        if(!exportFile.createNewFile()) throw new StorageException("Exception during creation file: " + exportFile);

        saveDocument(exportDocument, exportFile);
    }

    /**
     * Delete table
     */
    static void deleteTableFromDataBase(String tableName, File databaseFile) throws IOException, SAXException, ParserConfigurationException, StorageException, TransformerException, XPathExpressionException {
        Document document = getExistingDocument(databaseFile);

        // Get tables root element
        Element tables = getElementByTagName(document, "tables");

        // Get table element
        Element table = getElementByTagName(tables, tableName);

        tables.removeChild(table);

        saveDocument(document, databaseFile);
    }

    /**
     * Create Table.class instance from table element
     */
    private static Table extractTable(Element tableElement) throws IOException, SAXException, ParserConfigurationException, StorageException, ColumnException, TableException, CellException, NoteException {
        if(!tableElement.getAttribute("class").equals("table"))
            throw new StorageException("Can not extract table from element '" + tableElement.getTagName() + "' with not table class attribute");

        // Create table and set name
        Table table = new Table(tableElement.getTagName());

        // Get columns root
        Element columnsElement = getElementByTagName(tableElement, "columns");
        // Get columns
        NodeList columnsElements = columnsElement.getChildNodes();
        for(int i = 0; i < columnsElements.getLength(); i++) {
            Node columnNode = columnsElements.item(i);
            if(columnNode.getNodeType() == Node.ELEMENT_NODE) {
                // Get column element
                Element columnElement = (Element) columnNode;
                // Get column type
                Attr columnType = columnElement.getAttributeNode("type");
                // Get column isPrimary
                Attr columnIsPrimary = columnElement.getAttributeNode("isPrimary");
                // Get column intervalRegex
                Attr columnIntervalRegex = columnElement.getAttributeNode("intervalRegex");

                // Set column
                Column column;
                if(columnType.getValue().equals(Column.getTypeString(Column.TYPE_INT))
                        || columnType.getValue().equals(Column.getTypeString(Column.TYPE_REAL))
                        || columnType.getValue().equals(Column.getTypeString(Column.TYPE_CHAR))) {
                    column = new Column(columnElement.getTagName(), Column.getTypeIntFromTypeString(columnType.getValue()));
                }
                else column = new Column(columnElement.getTagName(), Column.getTypeIntFromTypeString(columnType.getValue()), columnIntervalRegex.getValue());
                column.setPrimary(Boolean.parseBoolean(columnIsPrimary.getValue()));

                // Add column to table
                table.addColumn(column);
            }
        }

        // Get notes root
        Element notesElement = getElementByTagName(tableElement, "notes");
        // Get notes
        NodeList notesElements = notesElement.getChildNodes();
        for(int i = 0; i < notesElements.getLength(); i++) {
            Node noteNode = notesElements.item(i);
            if(noteNode.getNodeType() == Node.ELEMENT_NODE) {
                // Get note element
                Element noteElement = (Element) noteNode;
                // Set note
                Note note = new Note();

                // Get cells
                NodeList cellsElements = noteElement.getChildNodes();
                for(int j = 0; j < cellsElements.getLength(); j++) {
                    Node cellNode = cellsElements.item(j);
                    if(cellNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Get cell element
                        Element cellElement = (Element) cellNode;
                        // Get cell content
                        String cellContent = cellElement.getTextContent();
                        // Get cell column
                        Attr cellColumn = cellElement.getAttributeNode("column");

                        // Set cell
                        Cell cell = new Cell(cellContent, table.getColumnByName(cellColumn.getValue()));

                        // Add column to note
                        note.getCells().add(cell);
                    }
                }

                // Add note to table
                table.addNote(note);
            }
        }

        return table;
    }

    /**
     * Create ArrayList of Table.class instance from file
     */
    static ArrayList<Table> extractTablesFromFile(File file) throws IOException, SAXException, ParserConfigurationException, ColumnException, StorageException, NoteException, TableException, CellException {
        ArrayList<Table> tableArrayList = new ArrayList<>();

        // Get document
        Document document = getExistingDocument(file);
        // Get document root
        Element root = document.getDocumentElement();

        if(root.getAttribute("class").equals("table")) {
            // Add table to list
            tableArrayList.add(extractTable(root));
        }
        else if(root.getAttribute("class").equals("database")) {
            Element tablesElement = getElementByTagName(root, "tables");
            // Get notes
            NodeList tablesElements = tablesElement.getChildNodes();
            for(int i = 0; i < tablesElements.getLength(); i++) {
                // Get table node
                Node tableNode = tablesElements.item(i);
                if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                    // Get table element
                    Element tableElement = (Element) tableNode;

                    // Add table to list
                    tableArrayList.add(extractTable(tableElement));
                }

            }
        }
        else throw new StorageException("Can not extract table from file '" + file + "', with document root element class" + root.getAttribute("class"));

        return tableArrayList;
    }
}






















/*
static Table extractTable(File tableFile) throws IOException, SAXException, ParserConfigurationException, StorageException, ColumnException, TableException, CellException, NoteException {
        Table table = new Table();

        Document document = getExistingDocument(tableFile);
        // Get table root
        Element tableElement = document.getDocumentElement();
        table.setName(tableElement.getTagName());

        // Get columns root
        Element columnsElement = getElementByTagName(tableElement, "columns");
        // Get columns
        NodeList columnsElements = columnsElement.getChildNodes();
        for(int i = 0; i < columnsElements.getLength(); i++) {
            Node columnNode = columnsElements.item(i);
            if(columnNode.getNodeType() == Node.ELEMENT_NODE) {
                // Get column element
                Element columnElement = (Element) columnNode;
                // Get column type
                Attr columnType = columnElement.getAttributeNode("type");
                // Get column isPrimary
                Attr columnIsPrimary = columnElement.getAttributeNode("isPrimary");
                // Get column intervalRegex
                Attr columnIntervalRegex = columnElement.getAttributeNode("intervalRegex");

                // Set column
                Column column;
                if(columnType.getValue().equals(Column.getTypeString(Column.TYPE_INT))
                        || columnType.getValue().equals(Column.getTypeString(Column.TYPE_REAL))
                        || columnType.getValue().equals(Column.getTypeString(Column.TYPE_CHAR))) {
                    column = new Column(columnElement.getTagName(), Column.getTypeIntFromTypeString(columnType.getValue()));
                }
                else column = new Column(columnElement.getTagName(), Column.getTypeIntFromTypeString(columnType.getValue()), columnIntervalRegex.getValue());
                column.setPrimary(Boolean.parseBoolean(columnIsPrimary.getValue()));

                // Add column to table
                table.addColumn(column);
            }
        }

        // Get notes root
        Element notesElement = getElementByTagName(tableElement, "notes");
        // Get notes
        NodeList notesElements = notesElement.getChildNodes();
        for(int i = 0; i < notesElements.getLength(); i++) {
            Node noteNode = notesElements.item(i);
            if(noteNode.getNodeType() == Node.ELEMENT_NODE) {
                // Get note element
                Element noteElement = (Element) noteNode;
                // Set note
                Note note = new Note();

                // Get cells
                NodeList cellsElements = noteElement.getChildNodes();
                for(int j = 0; j < cellsElements.getLength(); j++) {
                    Node cellNode = cellsElements.item(j);
                    if(cellNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Get cell element
                        Element cellElement = (Element) cellNode;
                        // Get cell content
                        String cellContent = cellElement.getTextContent();
                        // Get cell column
                        Attr cellColumn = cellElement.getAttributeNode("column");

                        // Set cell
                        Cell cell = new Cell(cellContent, table.getColumnByName(cellColumn.getValue()));

                        // Add column to note
                        note.getCells().add(cell);
                    }
                }

                // Add note to table
                table.addNote(note);
            }
        }

        return table;
    }
*/
