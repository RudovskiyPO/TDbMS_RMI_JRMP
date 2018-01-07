package engine.service;

import engine.core.DataBase;
import engine.core.Table;
import engine.exceptions.*;
import engine.storageWorker.EntranceToStorage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Storage extends Remote {
    String BINDING_NAME = "StorageService";

    void createDatabase(DataBase dataBase) throws IOException, ColumnException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException;

    DataBase extractDatabase(File databaseFile) throws IOException, ColumnException, CellException, NoteException, TableException, DataBaseException, StorageException, ParserConfigurationException, SAXException;

    void changeCredentials(DataBase dataBase) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException;

    void saveTableToDB(Table table, File databaseFile) throws IOException, ColumnException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException;

    Table extractTable(File tableFile) throws IOException, ColumnException, CellException, NoteException, TableException, StorageException, SAXException, ParserConfigurationException;

    void exportTableFromDB(String tableName, File databaseFile) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException;

    void deleteTableFromDB(String tableName, File databaseFile) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException;

    ArrayList<File> getDatabaseFiles() throws RemoteException;

    ArrayList<String> getDatabasesList() throws RemoteException;

    File getDatabaseFileByName(String databaseName) throws RemoteException, StorageException;

    void deleteDatabase(String databaseName) throws RemoteException, StorageException;

    ArrayList<File> getExportFiles() throws RemoteException;

    ArrayList<String> getExportsList() throws RemoteException;

    File getExportFileByName(String fileName) throws RemoteException, StorageException;
}
