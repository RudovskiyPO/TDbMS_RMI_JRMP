package engine.core;

import engine.exceptions.CellException;
import engine.exceptions.ColumnException;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Cell implements Comparable, Serializable {
    /**
     * Attributes
     */
    private String content;
    private Column column;

    /**
     * Constructors
     */
    public Cell(String content, Column column) throws CellException {
        if (!checkType(content, column)) return;

        this.content = content;
        this.column = column;
    }

    /**
     * Getters
     */
    public String getContent() {
        return content;
    }

    public Column getColumn() {
        return column;
    }

    /**
     * Setters
     */
    public void setContent(String content) throws CellException {
        if (!checkType(content, column)) return;

        this.content = content;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * Check type
     */
    public static boolean checkType(String content, Column column) throws CellException {
        int type = column.getTypeInt();
        switch (type) {
            case Column.TYPE_INT:
                if (!isInteger(content) && !content.equals(""))
                    throw new CellException("This type of data not allowed in cell dependent on column '" + column.toString() +
                            "'. \nExpected: INTEGER. \nActual: " + content);
                return true;
            case Column.TYPE_REAL:
                if (!isReal(content) && !content.equals(""))
                    throw new CellException("This type of data not allowed in cell dependent on column '" + column.toString() +
                            "'. \nExpected: REAL. \nActual: " + content);
                return true;
            case Column.TYPE_CHAR:
                if (!isChar(content) && !content.equals(""))
                    throw new CellException("This type of data not allowed in cell dependent on column '" + column.toString() +
                            "'. \nExpected: CHAR. \nActual: " + content);
                return true;
            case Column.TYPE_CHAR_INTERVAL:
                if (!isCharInterval(content, column.getIntervalRegex()) && !content.equals(""))
                    throw new CellException("This type of data not allowed in cell dependent on column '" + column.toString() +
                            "'. \nExpected: CHAR_INTERVAL with interval regex '" + column.getIntervalRegex() + "'. \nActual: " + content);
                return true;
            case Column.TYPE_STRING_OF_CHAR_INTERVAL:
                if (!isStringOfCharInterval(content, column.getIntervalRegex()) && !content.equals(""))
                    throw new CellException("This type of data not allowed in cell dependent on column '" + column.toString() +
                            "'. \nExpected: STRING_OF_CHAR_INTERVAL with interval regex '" + column.getIntervalRegex() + "'. \nActual: " + content);
                return true;
        }
        return false;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    private static boolean isReal(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    private static boolean isChar(String s) {
        if (s.length() > 1) return false;
        char ch;
        if(s.isEmpty()) ch = ' ';
        else ch = s.toCharArray()[0];
        return Character.isLetterOrDigit(ch) || Character.isSpaceChar(ch);
    }

    private static boolean isCharInterval(String s, String intervalRegex) throws CellException {
        if (!isChar(s)) return false;

        Pattern p;
        try {
            p = Pattern.compile(intervalRegex);
        } catch (PatternSyntaxException | NullPointerException e) {
            throw new CellException("Wrong interval regex: \njava.util.regex.PatternSyntaxException: " + e.getMessage());
        }

        Matcher m = p.matcher(s);

        return m.find() && m.matches();
    }

    private static boolean isStringOfCharInterval(String s, String intervalRegex) {
        Pattern p = Pattern.compile(intervalRegex);

        char[] sToCharArray = s.toCharArray();
        for (char c : sToCharArray) {
            if (!p.matcher(Character.toString(c)).find()) return false;
        }

        return true;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        String cell_description = "(";
        try {
            cell_description += column.getTypeString();

            if (column.getTypeInt() == Column.TYPE_CHAR_INTERVAL || column.getTypeInt() == Column.TYPE_STRING_OF_CHAR_INTERVAL)
                cell_description += column.getIntervalRegex();

            cell_description += ") " + content;

        } catch (ColumnException e) {
            return "(" + e.getMessage() + ")" + content;
        }

        return cell_description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;

        Cell cell = (Cell) object;
        return cell.getContent().equals(content) && cell.getColumn().equals(column);
    }

    @Override
    public int compareTo(Object o) {
        Cell cell = (Cell) o;

        //return content.compareTo(cell.getContent());

        if (cell.getContent() == null || cell.getContent().isEmpty() ||
                content == null || content.isEmpty()) return -1;

        int type = cell.getColumn().getTypeInt();

        switch (type) {
            case Column.TYPE_INT:
            case Column.TYPE_REAL:
                if (Double.parseDouble(cell.getContent()) < Double.parseDouble(content)) return -1;
                else if (Double.parseDouble(cell.getContent()) == Double.parseDouble(content)) return 0;
                else return 1;
            case Column.TYPE_CHAR:
            case Column.TYPE_CHAR_INTERVAL:
                if (cell.getContent().charAt(0) < content.charAt(0)) return -1;
                else if (cell.getContent().charAt(0) == content.charAt(0)) return 0;
                else return 1;
            case Column.TYPE_STRING_OF_CHAR_INTERVAL:
                if(cell.getContent().length() < content.length()) return -1;
                else if(cell.getContent().length() == content.length()) return 0;
                else return 1;
         }
         return 0;
    }
}