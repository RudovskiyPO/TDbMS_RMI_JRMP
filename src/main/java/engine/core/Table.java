package engine.core;

import engine.exceptions.CellException;
import engine.exceptions.ColumnException;
import engine.exceptions.NoteException;
import engine.exceptions.TableException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Table implements Serializable {
    /**
     * Attributes
     */
    private String name;
    private ArrayList<Column> columns;
    private ArrayList<Note> notes;

    /**
     * Constructors
     */
    public Table() {
        name = "Untitled";
        columns = new ArrayList<Column>();
        notes = new ArrayList<Note>();
    }

    public Table(String name) {
        this.name = name.trim();
        columns = new ArrayList<Column>();
        notes = new ArrayList<Note>();
    }

    public Table(String name, ArrayList<Column> columns) throws TableException {
        if (isColumnNameDuplicates(columns)) throw new TableException("Duplicate column name");

        this.name = name.trim();
        this.columns = columns;
        notes = new ArrayList<Note>();
    }

    public Table(String name, Column... columns) throws TableException {
        ArrayList<Column> columnsArrayList = new ArrayList<>(Arrays.asList(columns));

        if (isColumnNameDuplicates(columnsArrayList)) throw new TableException("Duplicate column name");

        this.name = name.trim();
        this.columns = columnsArrayList;
        notes = new ArrayList<Note>();
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public String getColumnList() {
        String list = "";
        for (Column column : columns) {
            if (column != columns.get(0)) list += ", ";
            list += column.toString();
        }
        return list;
    }

    public int getColumnsAmount() {
        return columns.size();
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public String getNoteList() {
        String list = "";
        for (Note note : notes) {
            if (note != notes.get(0)) list += ", ";
            list += note.toString() + " \n";
        }
        return list;
    }

    public int getNotesAmount() {
        return notes.size();
    }

    /**
     * Setters
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    public void setColumns(ArrayList<Column> columns) throws TableException {
        if (isColumnNameDuplicates(columns)) throw new TableException("Duplicate column name");

        this.columns = columns;
    }

    public void setColumns(Column... columns) throws TableException {
        ArrayList<Column> columnsArrayList = new ArrayList<>(Arrays.asList(columns));

        if (isColumnNameDuplicates(columnsArrayList)) throw new TableException("Duplicate column name");

        this.columns = columnsArrayList;
    }

    public void setNotes(ArrayList<Note> notes) throws TableException {
        if (isPrimaryKeysDuplicates(notes)) throw new TableException("Duplicate primary keys");
        this.notes = notes;
    }

    public void setNotes(Note... notes) throws TableException {
        ArrayList<Note> notesArrayList = new ArrayList<Note>(Arrays.asList(notes));

        if (isPrimaryKeysDuplicates(notesArrayList)) throw new TableException("Duplicate primary keys");

        this.notes = notesArrayList;
    }

    /**
     * Column methods
     */
    public void setPrimaryColumn(boolean primary) {
        for (Column column : columns) {
            if (column.isPrimary()) column.setPrimary(false);
        }

        columns.get(0).setPrimary(primary);
    }

    public void setPrimaryColumn(String columnName, boolean primary) throws TableException {
        for (Column column : columns) {
            if (column.isPrimary()) column.setPrimary(false);
        }

        getColumnByName(columnName).setPrimary(primary);
    }

    public void addColumn(Column column) throws TableException {
        columns.add(column);

        if (isColumnNameDuplicates(columns)) {
            columns.remove(column);
            throw new TableException("Duplicate column name");
        }
    }

    public void removeColumn(int index) {
        columns.remove(index);
    }

    public void removeColumn(Column column) {
        columns.remove(column);
    }

    public void removeColumn(String columnName) throws TableException {
        Iterator<Column> iterator = columns.iterator();

        while (iterator.hasNext()) {
            Column column = iterator.next();

            if (column.getName().equals(columnName)) {
                iterator.remove();
                return;
            }
        }
        throw new TableException("Column with name '" + columnName + "' was not found.");
    }

    public void updateColumn(String columnName, Column newColumn) throws TableException, ColumnException {
        getColumnByName(columnName).setName(newColumn.getName());
        getColumnByName(columnName).setType(newColumn.getTypeInt());
    }

    public void updateColumnName(String columnName, String newColumnName) throws TableException {
        getColumnByName(columnName).setName(newColumnName);
    }

    public void updateColumnType(String columnName, int newColumnType) throws TableException, ColumnException {
        getColumnByName(columnName).setType(newColumnType);
    }

    /**
     * Column select methods
     */
    public Column getColumn(int index) {
        return columns.get(index);
    }

    public Column getColumnByName(String columnName) throws TableException {
        for (Column column : columns) {
            if (column.getName().equals(columnName.toLowerCase())) return column;
        }

        throw new TableException("Column '" + columnName + "' did not found.");
    }

    /**
     * Note methods
     */
    public void addNote(Note note) throws TableException {
        notes.add(note);

        if (isPrimaryKeysDuplicates(notes)) {
            notes.remove(note);
            throw new TableException("Duplicate primary keys");
        }
    }

    public void addNote(String... cellContent) throws CellException, TableException {
        if (cellContent.length < columns.size()) {

            String[] copyOfCellContent = Arrays.copyOf(cellContent, columns.size());

            for (int i = cellContent.length; i < columns.size(); i++) {
                copyOfCellContent[i] = "";
            }

            cellContent = copyOfCellContent;
        }

        Note note = new Note();

        for (int i = 0; i < columns.size(); i++) {
            note.getCells().add(new Cell(cellContent[i], columns.get(i)));
        }

        notes.add(note);

        if (isPrimaryKeysDuplicates(notes)) {
            notes.remove(note);
            throw new TableException("Duplicate primary keys");
        }
    }

    public void removeNote(int index) {
        notes.remove(index);
    }

    public void removeNote(String primaryKey) throws TableException, NoteException {
        Iterator<Note> iterator = notes.iterator();

        while (iterator.hasNext()) {
            Note note = iterator.next();

            if (note.equals(getNoteByPrimaryKey(primaryKey))) {
                iterator.remove();
                return;
            }
        }
        throw new TableException("Note with primary key '" + primaryKey + "' was not found.");
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public void updateNote(String primaryKey, String targetColumnName, String targetContent) throws NoteException, TableException, CellException {
        getNoteByPrimaryKey(primaryKey).getCellByColumnName(targetColumnName).setContent(targetContent);
    }

    public void updateNote(String primaryKey, Note note) throws NoteException, TableException {
        getNoteByPrimaryKey(primaryKey).setCells(note.getCells());
    }

    public void sortTableNotesByColumn(Column column) throws NoteException {
        if(getDataByColumn(column.getName()).size() < 1) return;

        // Create array list of key cells to sorting notes by this cells
        ArrayList<Cell> keyCells = new ArrayList<>(getNotesAmount());
        // Fill key cells
        for (Note note : getNotes()) {
            if(note.getCellByColumn(column) != null) keyCells.add(note.getCellByColumn(column));
        }

        // Create string of key cells status before sorting to compare with it string after sorting
        StringBuilder keyCellsBefore = new StringBuilder();
        for (Cell cell : keyCells) {
            keyCellsBefore.append(cell.toString());
        }

        // Sort key cells and table notes
        ArrayList<Note> notes = getNotes();
        for (int i = 0; i < keyCells.size(); i++) {
            for (int j = 1; j < (keyCells.size() - i); j++) {
                if (keyCells.get(j).getContent().isEmpty()) continue;

                if (keyCells.get(j - 1).compareTo(keyCells.get(j)) == -1) {
                    Cell temp_cell = keyCells.get(j - 1);
                    keyCells.set(j - 1, keyCells.get(j));
                    keyCells.set(j, temp_cell);

                    Note temp_note = notes.get(j - 1);
                    notes.set(j - 1, notes.get(j));
                    notes.set(j, temp_note);
                }
            }
        }

        // Create string of key cells status after sorting to compare with it string before sorting
        StringBuilder keyCellsAfter = new StringBuilder();
        for (Cell cell : keyCells) {
            keyCellsAfter.append(cell.toString());
        }

        // If the before sorting is equal to the after sorting, then sort in the reverse order
        if (keyCellsAfter.toString().equals(keyCellsBefore.toString())) {
            for(int i = 0; i < notes.size()/2; i++) {
                Note temp_note = notes.get(i);
                notes.set(i, notes.get(notes.size() - 1 - i));
                notes.set((notes.size() - 1 - i), temp_note);
            }
        }
    }

    /**
     * Data select methods
     */
    public Note getNote(int index) {
        return notes.get(index);
    }

    public ArrayList<Note> getNotesWhereColumnEqual(String columnName, String value) throws NoteException {
        ArrayList<Note> result = new ArrayList<Note>();

        for (Note note : notes) {
            if (note.getCellByColumnName(columnName).getContent().equals(value)) result.add(note);
        }
        return result;
    }

    public ArrayList<Note> getNotesWhereCellEqual(String value) throws NoteException {
        ArrayList<Note> result = new ArrayList<Note>();

        for (Note note : notes) {
            if (note.getCellByContent(value) != null) result.add(note);
        }
        return result;
    }

    public Note getNoteByPrimaryKey(String key) throws NoteException, TableException {
        for (Note note : notes) {
            if (note.getCellByPrimaryColumn().getContent().equals(key)) return note;
        }

        throw new TableException("Note with primary key '" + key + "' did not found.");
    }

    public ArrayList<String> getDataByColumn(String columnName) throws NoteException {
        ArrayList<String> result = new ArrayList<>();

        for (Note note : notes) {
            if(note.getCellByColumnName(columnName) != null) result.add(note.getCellByColumnName(columnName).getContent());
        }

        return result;
    }

    public ArrayList<String> getPrimaryKeys() throws NoteException {
        return getPrimaryKeys(notes);
    }

    /**
     * Static methods
     */
    public static boolean isColumnNameDuplicates(ArrayList<Column> columns) {
        for (int i = 0; i < columns.size(); i++) {
            for (int j = i + 1; j < columns.size(); j++) {
                if (columns.get(i).getName().equals(columns.get(j).getName())) return true;
            }
        }
        return false;
    }

    private static ArrayList<String> getPrimaryKeys(ArrayList<Note> notes) {
        ArrayList<String> result = new ArrayList<>();

        try {
            for (Note note : notes) {
                if(note.getCellByPrimaryColumn() != null) result.add(note.getCellByPrimaryColumn().getContent());
            }
        } catch (NoteException e) {
            return result;
        }

        return result;
    }

    public static boolean isPrimaryKeysDuplicates(ArrayList<Note> notes) {
        ArrayList<String> keys = getPrimaryKeys(notes);

        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                if (keys.get(i).equals(keys.get(j))) return true;
            }
        }
        return false;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        String table_description = name + " {\n";

        for (Column column : columns) {
            if (column == columns.get(0))
                table_description += "\t";
            else
                table_description += ", ";

            table_description += column.toString();
        }

        for (Note note : notes) {
            if (note == notes.get(0))
                table_description += " \n\t";
            else
                table_description += " \n\t";

            table_description += note.toString();
        }

        table_description += "\n}";

        return table_description;
    }
}
