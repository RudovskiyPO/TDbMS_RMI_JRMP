package engine.storageWorker;

import engine.core.DataBase;
import engine.core.Table;
import engine.exceptions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class EntranceToStorage {
    /**
     * Constant
     */
    private static final File storage = new File(System.getProperty("user.dir") + "/storage");

    /**
     * Static storage file functions
     */
    public static ArrayList<File> getDataBaseListFiles() {
        ArrayList<File> result = new ArrayList<>();

        if (storage.list() == null) return result;

        for (int i = 0; i < storage.list().length; i++) {
            File databaseFile = new File(storage.getAbsolutePath() + "/" + storage.list()[i]);

            if (databaseFile.isFile()) result.add(databaseFile);
        }

        return result;
    }

    public static ArrayList<String> getDataBaseList() {
        ArrayList<String> result = new ArrayList<>();

        for (File file : getDataBaseListFiles()) {
            result.add(file.getName().replace(".xml", ""));
        }

        return result;
    }

    public static File getDataBaseFileByName(String databaseName) throws StorageException {
        for (File file : getDataBaseListFiles()) {
            if (file.getName().replace(".xml", "").equals(databaseName)) return file;
        }

        throw new StorageException("Database " + databaseName + " does not exist in storage.");
    }

    public static void deleteDataBase(String databaseName) throws StorageException {
        for(String database : getDataBaseList()) {
            if(database.equals(databaseName) && !getDataBaseFileByName(databaseName).delete())
                throw new StorageException("Error during deletion " + database);
        }
    }

    public static ArrayList<File> getExportListFiles() {
        ArrayList<File> result = new ArrayList<>();

        File export = new File(storage.getAbsolutePath() + "/export");
        if (!isFileInDirectory(export.getName(), storage)) return result;

        for (int i = 0; i < export.list().length; i++) {
            File exportFile = new File(export.getAbsolutePath() + "/" + export.list()[i]);

            if (exportFile.isFile()) result.add(exportFile);
        }

        return result;
    }

    public static ArrayList<String> getExportsList() {
        ArrayList<String> result = new ArrayList<>();

        for (File file : getExportListFiles()) {
            result.add(file.getName().replace(".xml", ""));
        }

        return result;
    }

    public static File getExportFileByName(String fileName) throws StorageException {
        for (File file : getExportListFiles()) {
            if (file.getName().replace(".xml", "").equals(fileName)) return file;
        }

        throw new StorageException("File " + fileName + " does not exist in storage.");
    }

    /**
     * Static storage xml file content functions
     */
    public static void createNewDataBase(DataBase dataBase) throws ParserConfigurationException, TransformerException, IOException, StorageException, SAXException, XPathExpressionException, ColumnException {
        DataBaseStorage.laydownNewDataBase(dataBase);
        DataBaseStorage.setNewDataBaseCredentials(dataBase);
        DataBaseStorage.setNewDataBaseTablesRoot(dataBase);

        for(Table table : dataBase.getTables()) {
            TableStorage.laydownTableToDataBase(table, DataBaseStorage.getDataBaseFile(dataBase.getName()));
        }
    }

    public static DataBase extractDataBaseFromFile(File databaseFile) throws ParserConfigurationException, SAXException, IOException, NoteException, StorageException, CellException, DataBaseException, ColumnException, TableException {
        return DataBaseStorage.extractDataBase(databaseFile);
    }

    public static void changeDataBaseCredentials(DataBase dataBase) throws SAXException, StorageException, ParserConfigurationException, IOException, TransformerException, XPathExpressionException {
        DataBaseStorage.changeDataBaseCredentials(dataBase);
    }

    public static void saveTableToDatabase(Table table, File databaseFile) throws ParserConfigurationException, TransformerException, IOException, XPathExpressionException, StorageException, SAXException, ColumnException {
        TableStorage.laydownTableToDataBase(table, databaseFile);
    }

    public static void exportTableFromDataBase(String tableName, File databaseFile) throws XPathExpressionException, ParserConfigurationException, TransformerException, IOException, StorageException, SAXException {
        TableStorage.exportTable(databaseFile, tableName);
    }

    public static Table extractTableFromFile(File tableFile) throws ParserConfigurationException, NoteException, IOException, StorageException, CellException, SAXException, ColumnException, TableException {
        return TableStorage.extractTablesFromFile(tableFile).get(0);
    }

    public static void deleteTableFromDataBase(String tableName, File databaseFile) throws XPathExpressionException, ParserConfigurationException, TransformerException, IOException, StorageException, SAXException {
        TableStorage.deleteTableFromDataBase(tableName, databaseFile);
    }

    /**
     * Static additional function
     */
    private static boolean isFileInDirectory(String fileName, File directory) {
        File[] listFiles = directory.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].getName().equals(fileName)) return true;
        }

        return false;
    }

}
