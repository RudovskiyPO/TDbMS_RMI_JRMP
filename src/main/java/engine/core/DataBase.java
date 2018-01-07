package engine.core;

import engine.exceptions.DataBaseException;
import engine.exceptions.NoteException;
import engine.exceptions.TableException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DataBase implements Serializable {
    /**
     * Attributes
     */
    private String name;
    private ArrayList<Table> tables;
    private Credentials credentials;

    /**
     * Constructors
     */
    public DataBase() {
        name = "untitled";
        tables = new ArrayList<>();
        credentials = new Credentials();
    }

    public DataBase(String name) {
        this.name = name;
        tables = new ArrayList<>();
        credentials = new Credentials(name, "");
    }

    public DataBase(String name, Credentials credentials) {
        this.name = name;
        tables = new ArrayList<>();
        this.credentials = credentials;
    }

    public DataBase(String name, Credentials credentials, ArrayList<Table> tables) throws DataBaseException {
        if(isTableNameDuplicates(tables)) throw new DataBaseException("Duplicate table name");

        this.name = name;
        this.tables = tables;
        this.credentials = credentials;
    }

    public DataBase(String name, Credentials credentials, Table... tables) throws DataBaseException {
        ArrayList<Table> tablesArrayList = new ArrayList<>(Arrays.asList(tables));

        if(isTableNameDuplicates(tablesArrayList)) throw new DataBaseException("Duplicate table name");

        this.name = name;
        this.tables = tablesArrayList;
        this.credentials = credentials;
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public String getTableList() {
        String list = "";
        for (Table table : tables) {
            if (table != tables.get(0)) list += ", \n";
            list += table.getName() + " {" + table.getColumnList() + "}";
        }
        return list;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Setters
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setTables(ArrayList<Table> tables) throws DataBaseException {
        if(isTableNameDuplicates(tables)) throw new DataBaseException("Duplicate table name");

        this.tables = tables;
    }

    public void setTables(Table... tables) throws DataBaseException {
        ArrayList<Table> tablesArrayList = new ArrayList<>(Arrays.asList(tables));

        if(isTableNameDuplicates(tablesArrayList)) throw new DataBaseException("Duplicate table name");

        this.tables = tablesArrayList;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Table methods
     */
    public Table getTable(int index) {
        return tables.get(index);
    }

    public Table getTableByName(String tableName) throws DataBaseException {
        for(Table table : tables) {
            if(table.getName().equals(tableName)) return table;
        }

        //throw new DataBaseException("Table '" + tableName  + "' did not found.");
        return null;
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public void removeTable(int index) {
        tables.remove(index);
    }

    public void removeTable(String tableName) throws DataBaseException {
        Iterator<Table> iterator = tables.iterator();

        while (iterator.hasNext()) {
            Table table = iterator.next();

            if(table.getName().equals(tableName)) {
                iterator.remove();
                return;
            }
        }
        throw new DataBaseException("Table with name '" + tableName + "' was not found.");
    }

    public void updateTable(String tableName, Table newTable) throws DataBaseException, TableException, NoteException {
        getTableByName(tableName).setName(newTable.getName());
        getTableByName(tableName).setColumns(newTable.getColumns());
        getTableByName(tableName).setNotes(newTable.getNotes());
    }


    /**
     * Static methods
     */
    private static boolean isTableNameDuplicates(ArrayList<Table> tables) {
        for (int i = 0; i < tables.size(); i++) {
            for (int j = i + 1; j < tables.size(); j++) {
                if(tables.get(i).getName().equals(tables.get(j).getName())) return true;
            }
        }
        return false;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        String database_desctription = name + "[" + credentials.getLogin() + ", " + credentials.getPassword() + "] {\n";

        for (Table table : tables) {
            if (table == tables.get(0))
                database_desctription += "\t";
            else
                database_desctription += ", \n\t";

            database_desctription += table.getName() + " [" + table.getColumnList() + "]";
        }

        database_desctription += "\n}";

        return database_desctription;
    }
}
