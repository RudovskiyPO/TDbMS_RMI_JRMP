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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class StorageImpl extends UnicastRemoteObject implements Storage {

    private EntranceToStorage storage;

    public StorageImpl() throws RemoteException {
        storage = new EntranceToStorage();
    }

    @Override
    public void createDatabase(DataBase dataBase) throws IOException, ColumnException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        EntranceToStorage.createNewDataBase(dataBase);
    }

    @Override
    public DataBase extractDatabase(File databaseFile) throws IOException, ColumnException, CellException, NoteException, TableException, DataBaseException, StorageException, ParserConfigurationException, SAXException {
        return EntranceToStorage.extractDataBaseFromFile(databaseFile);
    }

    @Override
    public void changeCredentials(DataBase dataBase) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        EntranceToStorage.changeDataBaseCredentials(dataBase);
    }

    @Override
    public void saveTableToDB(Table table, File databaseFile) throws IOException, ColumnException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        EntranceToStorage.saveTableToDatabase(table, databaseFile);
    }

    @Override
    public Table extractTable(File tableFile) throws IOException, ColumnException, CellException, NoteException, TableException, StorageException, SAXException, ParserConfigurationException {
        return EntranceToStorage.extractTableFromFile(tableFile);
    }

    @Override
    public void exportTableFromDB(String tableName, File databaseFile) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        EntranceToStorage.exportTableFromDataBase(tableName, databaseFile);
    }

    @Override
    public void deleteTableFromDB(String tableName, File databaseFile) throws IOException, StorageException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        EntranceToStorage.deleteTableFromDataBase(tableName, databaseFile);
    }

    @Override
    public ArrayList<File> getDatabaseFiles() throws RemoteException {
        return EntranceToStorage.getDataBaseListFiles();
    }

    @Override
    public ArrayList<String> getDatabasesList() throws RemoteException {
        return EntranceToStorage.getDataBaseList();
    }

    @Override
    public File getDatabaseFileByName(String databaseName) throws RemoteException, StorageException {
        return EntranceToStorage.getDataBaseFileByName(databaseName);
    }

    @Override
    public void deleteDatabase(String databaseName) throws RemoteException, StorageException {
        EntranceToStorage.deleteDataBase(databaseName);
    }

    @Override
    public ArrayList<File> getExportFiles() throws RemoteException {
        return EntranceToStorage.getExportListFiles();
    }

    @Override
    public ArrayList<String> getExportsList() throws RemoteException {
        return EntranceToStorage.getExportsList();
    }

    @Override
    public File getExportFileByName(String fileName) throws RemoteException, StorageException {
        return EntranceToStorage.getExportFileByName(fileName);
    }


}
