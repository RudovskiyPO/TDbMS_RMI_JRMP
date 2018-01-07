package engine.core;

import engine.exceptions.NoteException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Note implements Serializable {
    /**
     * Attributes
     */
    private ArrayList<Cell> cells;

    /**
     * Constructors
     */
    public Note() {
        cells = new ArrayList<Cell>();
    }

    public Note(ArrayList<Cell> cells) {
        this.cells = cells;
    }

    public Note(Cell... cells) {
        this.cells = new ArrayList<Cell>(Arrays.asList(cells));
    }

    /**
     * Getters
     */
    public ArrayList<Cell> getCells() {
        return cells;
    }

    public Cell getCellByColumn(Column column) throws NoteException {
        for (Cell cell : cells) {
            if(cell.getColumn().equals(column)) return cell;
        }

        return null;
        //throw new NoteException("Column '" + column.toString() + "' does not exist.");
    }

    public Cell getCellByColumnName(String columnName) throws NoteException {
        for (Cell cell : cells) {
            if(cell.getColumn().getName().equals(columnName.toLowerCase())) return cell;
        }

        return null;
        //throw new NoteException("Column '" + columnName + "' does not exist or wrong column name.");
    }

    public Cell getCellByPrimaryColumn() throws NoteException {
        for (Cell cell : cells) {
            if(cell.getColumn().isPrimary()) return cell;
        }

        return null;
        //throw new NoteException("No column set as primary.");
    }

    public Cell getCellByContent(String content) throws NoteException {
        for (Cell cell : cells) {
            if(cell.getContent().equals(content)) return cell;
        }

        return null;
        //throw new NoteException("No cell contains '" + content + "'.");
    }
    /**
     * Setters
     */
    public void setCells(ArrayList<Cell> cells) {
        this.cells = cells;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        String note = "";
        for (Cell cell : cells) {
            if (cell != cells.get(0)) note += ", ";
            note += "[" + cell.getContent() + "]";
        }
        return note;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;


        Note note = (Note) object;
        return note.getCells().equals(cells);
    }
}
