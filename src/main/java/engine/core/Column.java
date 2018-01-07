package engine.core;

import engine.exceptions.ColumnException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Column implements Serializable {
    /**
     * Attributes
     */
    private String name;
    private int type;
    private boolean primary;
    private String intervalRegex;

    /**
     * Constants
     */
    public static final int TYPE_INT = 1;
    public static final int TYPE_REAL = 2;
    public static final int TYPE_CHAR = 3;
    public static final int TYPE_CHAR_INTERVAL = 4;
    public static final int TYPE_STRING_OF_CHAR_INTERVAL = 5;

    /**
     * Constructors
     */
    public Column(String name, int type) throws ColumnException {
        if(type == Column.TYPE_CHAR_INTERVAL || type == Column.TYPE_STRING_OF_CHAR_INTERVAL)
            throw new ColumnException("This column has CHAR_INTERVAL type. Use 'Column(String name, int type, String intervalRegex)' constructor in this case.");

        this.name = name.toLowerCase();
        this.type = type;
        intervalRegex = "";
        primary = false;
    }

    public Column(String name, int type, String intervalRegex) throws ColumnException {
        if(type == Column.TYPE_INT || type == Column.TYPE_REAL || type == Column.TYPE_CHAR)
            throw new ColumnException("This constructor is used for CHAR_INTERVAL and STRING_OF_CHAR_INTERVAL types. " +
                    "Forbidden to use it for INTEGER, REAL and CHAR types instead of this recommended 'Column(String name, int type)' constructor.");

        if(intervalRegex.equals(""))
            throw new ColumnException("This column has CHAR_INTERVAL type. 'intervalRegex' param can not be null or empty. Set it with regex.");

        try {
            Pattern.compile(intervalRegex);
        }catch (PatternSyntaxException e) {
            throw new ColumnException("Wrong interval regex: \njava.util.regex.PatternSyntaxException: " + e.getMessage());
        }


        this.name = name.toLowerCase();
        this.type = type;
        this.intervalRegex = intervalRegex.trim();
        primary = false;
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public int getTypeInt() {
        return type;
    }

    public String getTypeString() throws ColumnException {
        return getTypeString(type);
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getIntervalRegex() {
        return intervalRegex;
    }

    /**
     * Setters
     */
    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setType(int type) throws ColumnException {
        if(type == Column.TYPE_CHAR_INTERVAL || type == Column.TYPE_STRING_OF_CHAR_INTERVAL)
            throw new ColumnException("This column has CHAR_INTERVAL type. Use 'setType(int type, String intervalRegex)' method in this case.");

        this.type = type;
    }

    public void setType(int type, String intervalRegex) throws ColumnException {
        if(type == Column.TYPE_INT || type == Column.TYPE_REAL || type == Column.TYPE_CHAR)
            throw new ColumnException("This method is used for CHAR_INTERVAL and STRING_OF_CHAR_INTERVAL types. " +
                    "Forbidden to use it for INTEGER, REAL and CHAR types instead of this recommended 'setType(int type)' method.");

        if(intervalRegex.equals(""))
            throw new ColumnException("This column has CHAR_INTERVAL type. 'intervalRegex' param can not be null or empty. Set it with regex.");

        try {
            Pattern.compile(intervalRegex);
        }catch (PatternSyntaxException e) {
            throw new ColumnException("Wrong interval regex: \njava.util.regex.PatternSyntaxException: " + e.getMessage());
        }

        this.intervalRegex = intervalRegex;
        this.type = type;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setIntervalRegex(String intervalRegex) {
        this.intervalRegex = intervalRegex;
    }

    /**
     * Static methods
     */
    public static String getTypeString(int type) throws ColumnException {
        switch (type) {
            case TYPE_INT:
                return "int";
            case TYPE_REAL:
                return "real";
            case TYPE_CHAR:
                return "char";
            case TYPE_CHAR_INTERVAL:
                return "char_interval";
            case TYPE_STRING_OF_CHAR_INTERVAL:
                return "string_of_char_interval";
            default:
                throw new ColumnException("Type was not set up or wrong type of column");
        }
    }

    public static int getTypeIntFromTypeString(String type) throws ColumnException {
        switch (type) {
            case "int":
                return TYPE_INT;
            case "real":
                return TYPE_REAL;
            case "char":
                return TYPE_CHAR;
            case "char_interval":
                return TYPE_CHAR_INTERVAL;
            case "string_of_char_interval":
                return TYPE_STRING_OF_CHAR_INTERVAL;
            default:
                throw new ColumnException("Type was not set up or wrong type of column");
        }
    }

    public static ArrayList<String> getTypesList() {
        ArrayList<String> typesList = new ArrayList<>(5);

        typesList.add("int");
        typesList.add("real");
        typesList.add("char");
        typesList.add("char_interval");
        typesList.add("string_of_char_interval");

        return typesList;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        String column_description = "";
        try {
            column_description += name + "(" + getTypeString();

            if (type == Column.TYPE_CHAR_INTERVAL || type == Column.TYPE_STRING_OF_CHAR_INTERVAL)
                column_description += intervalRegex;

            column_description += ")";
        } catch (ColumnException e) {
            column_description += name + "(" + e.getMessage() + ")";
        }

        if (primary) column_description += "*";

        return column_description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;

        Column column = (Column) object;
        return column.getName().equals(name) && column.getTypeInt() == type && column.isPrimary() == primary;
    }
}
