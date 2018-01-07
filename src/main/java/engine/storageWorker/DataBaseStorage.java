package engine.storageWorker;

import engine.core.DataBase;
import engine.exceptions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static engine.storageWorker.XMLUtils.*;

class DataBaseStorage {
    private static final String root = "storage";

    /**
     * Create and save new database. Set database credentials. Set database tables root.
     */
    static void laydownNewDataBase(DataBase database) throws IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
        File file = createNewFileForDB(database.getName());

        Document doc = getNewDocument();

        Element rootElement = createRootElementOfDocument(doc, database.getName());
        rootElement.setAttribute("class", "database");

        saveDocument(doc, file);
    }

    static void setNewDataBaseCredentials(DataBase dataBase) throws ParserConfigurationException, IOException, SAXException, TransformerException, StorageException, XPathExpressionException {
        File file = getDataBaseFile(dataBase.getName());

        Document document = getExistingDocument(file);

        Element root = document.getDocumentElement();

        if(!root.getTagName().equals(dataBase.getName())) throw new StorageException("Wrong database. Expected: " + root.getTagName() + ". Actual: " + dataBase.getName());

        // Credentials
        Element credentials = addChildElementToParent(document, "credentials", root);
        credentials.setAttribute("class", "credentials");

        // Login
        Element login = addChildElementToParent(document, "login", credentials);
        login.setTextContent(dataBase.getCredentials().getLogin());

        // Password
        Element password = addChildElementToParent(document, "password", credentials);
        password.setTextContent(dataBase.getCredentials().getPassword());

        saveDocument(document, file);
    }

    static void setNewDataBaseTablesRoot(DataBase dataBase) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException {
        File file = getDataBaseFile(dataBase.getName());

        Document document = getExistingDocument(file);

        Element root = document.getDocumentElement();

        addChildElementToParent(document, "tables", root);

        saveDocument(document, file);
    }

    private static File createNewFileForDB(String databaseName) throws IOException {
        File file = getDataBaseFile(databaseName);

        if (file.exists()) throw new IOException("File '" + file.getName() + "' already exist.");
        if (!file.createNewFile()) throw new IOException("File '" + file.getName() + "' can not be created.");

        return file;
    }

    public static File getDataBaseFile(String databaseName) {
        String fileName = databaseName + ".xml";
        return new File(root + "/" + fileName);
    }

    /**
     * Create DataBase.class instance from database xml configuration file
     */
    static DataBase extractDataBase(File databaseFile) throws IOException, SAXException, ParserConfigurationException, StorageException, ColumnException, NoteException, TableException, CellException, DataBaseException {
        DataBase dataBase = new DataBase();

        // Get source document
        Document document = getExistingDocument(databaseFile);

        // Get database root
        Element databaseElement = document.getDocumentElement();
        // Set database name
        dataBase.setName(databaseElement.getTagName());

        // Get credentials element
        Element credentialsElement = getElementByTagName(databaseElement, "credentials");

        // Get login element
        Element loginElement = getElementByTagName(credentialsElement, "login");
        // Set database login
        dataBase.getCredentials().setLogin(loginElement.getTextContent());

        // Get password element
        Element passwordElement = getElementByTagName(credentialsElement, "password");
        // Set database password
        dataBase.getCredentials().setPassword(passwordElement.getTextContent());

        // Set database tables
        dataBase.setTables(TableStorage.extractTablesFromFile(databaseFile));

        return dataBase;
    }

    /**
     * Change database credentials
     */
    static void changeDataBaseCredentials(DataBase dataBase) throws IOException, SAXException, ParserConfigurationException, StorageException, TransformerException, XPathExpressionException {
        File file = getDataBaseFile(dataBase.getName());

        Document document = getExistingDocument(file);

        // Credentials
        Element credentials = getElementByTagName(document, "credentials");

        // Login
        Element login = getElementByTagName(credentials, "login");
        login.setTextContent(dataBase.getCredentials().getLogin());

        // Password
        Element password = getElementByTagName(credentials, "password");
        password.setTextContent(dataBase.getCredentials().getPassword());

        saveDocument(document, file);
    }
}
