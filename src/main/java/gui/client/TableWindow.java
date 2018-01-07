package gui.client;

import engine.core.*;
import engine.core.Cell;
import engine.exceptions.*;
import engine.service.Storage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class TableWindow extends Stage {
    /**
     * Attributes
     */
    private Scene scene;
    private Pane rootPanel;

    private TextField tableNameField;
    private Button saveButton;
    private Button removeTable;
    private Button addButton;
    private Button deleteButton;
    private Button alterColumns;
    private Button closeButton;
    private Button refreshButton;

    private Pane tablePanel;
    private ScrollPane tablePanelScroll;
    private ArrayList<TextField> tableColumns;
    private ArrayList<ArrayList<TextField>> tableRows;

    private static double TABLE_CELL_WIDTH = 150;
    private static double TABLE_CELL_HEIGHT = 20;
    private final Storage storage;
    private Table table;
    private File databaseFile;

    /**
     * Constructor
     */
    TableWindow(Table table, File databaseFile, Storage storage) {
        // Set storage
        this.storage = storage;
        // Set table
        this.table = table;
        // Set database file with table
        this.databaseFile = databaseFile;

        // Initialize elements
        tableNameField = new TextField(table.getName());
        saveButton = new Button("Save");
        removeTable = new Button("Remove");
        tablePanel = new Pane();
        tablePanelScroll = new ScrollPane(tablePanel);
        refreshButton = new Button("Refresh");
        deleteButton = new Button("Delete");
        addButton = new Button("Add");
        alterColumns = new Button("Alter");
        closeButton = new Button("Close");

        // Set table name field
        tableNameField.setLayoutX(10);
        tableNameField.setLayoutY(20);
        tableNameField.setPrefSize(200, 30);
        tableNameField.setFocusTraversable(false);
        tableNameField.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        // Set save button
        saveButton.getStyleClass().add("ok");
        saveButton.setLayoutX(tableNameField.getLayoutX() + tableNameField.getPrefWidth() + 20);
        saveButton.setLayoutY(15);
        saveButton.setPrefSize(90, 33);
        saveButton.setFocusTraversable(false);
        saveButton.setOnAction(event -> saveTable());

        // Set remove button
        removeTable.getStyleClass().add("exit_button");
        removeTable.setPrefSize(90, 33);
        removeTable.setLayoutX(saveButton.getLayoutX() + saveButton.getPrefWidth() + (saveButton.getLayoutX() - tableNameField.getLayoutX() - tableNameField.getPrefWidth()));
        removeTable.setLayoutY(saveButton.getLayoutY());
        removeTable.setOnAction(event -> removeTable());

        // Set table data
        tableColumns = new ArrayList<>(table.getColumnsAmount());
        setColumns(tableColumns);
        try {
            setRows();
        } catch (NoteException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }
        // Set table panel

        tablePanel.getChildren().addAll(tableColumns);
        for (ArrayList<TextField> cells : tableRows) {
            tablePanel.getChildren().addAll(cells);
        }
        // Set scroll table panel panel
        tablePanelScroll.setPrefSize(600, 450);
        tablePanelScroll.setLayoutX(tableNameField.getLayoutX());
        tablePanelScroll.setLayoutY(tableNameField.getLayoutY() * 2 + tableNameField.getPrefHeight());

        // Set refresh button
        refreshButton.getStyleClass().add("refresh");
        refreshButton.setPrefSize(60, 25);
        refreshButton.setLayoutX(tablePanelScroll.getLayoutX() + tablePanelScroll.getPrefWidth() - refreshButton.getPrefWidth());
        refreshButton.setLayoutY(tablePanelScroll.getLayoutY() - refreshButton.getPrefHeight() - 3);
        refreshButton.setFocusTraversable(false);
        refreshButton.setOnAction(event -> refresh());

        // Set delete button
        deleteButton.getStyleClass().add("exit_button");
        deleteButton.setPrefSize(90, 33);
        deleteButton.setLayoutX(tablePanelScroll.getLayoutX() + 14);
        deleteButton.setLayoutY(tablePanelScroll.getLayoutY() + tablePanelScroll.getPrefHeight() + 20);
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(event -> deleteNote());

        // Set add button
        addButton.getStyleClass().add("ok");
        addButton.setPrefSize(90, 33);
        addButton.setLayoutX(deleteButton.getLayoutX() + deleteButton.getPrefWidth() + 70);
        addButton.setLayoutY(deleteButton.getLayoutY());
        addButton.setFocusTraversable(false);
        addButton.setOnAction(event -> addNote());

        // Set alter button
        alterColumns.getStyleClass().add("other_button");
        alterColumns.setPrefSize(90, 33);
        alterColumns.setLayoutX(addButton.getLayoutX() + addButton.getPrefWidth() + 70);
        alterColumns.setLayoutY(addButton.getLayoutY());
        alterColumns.setOnAction(event -> alterColumns());

        // Set exit button
        closeButton.getStyleClass().add("exit_button");
        closeButton.setPrefSize(90, 33);
        closeButton.setLayoutX(alterColumns.getLayoutX() + alterColumns.getPrefWidth() + 70);
        closeButton.setLayoutY(alterColumns.getLayoutY());
        closeButton.setFocusTraversable(false);
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Set root panel
        rootPanel = new Pane();
        rootPanel.getChildren().addAll(tablePanelScroll, tableNameField, saveButton, removeTable, refreshButton, deleteButton, addButton, alterColumns, closeButton);

        // Set scene
        scene = new Scene(rootPanel, tablePanelScroll.getPrefWidth() + 10, tablePanelScroll.getPrefHeight() + 140);
        scene.getStylesheets().add("/css/main.css");

        // Set stage
        setTitle(table.getName());
        setScene(scene);
        setResizable(false);
        setX(550);
        setY(150);
    }

    /**
     * Methods
     */
    private void setColumns(ArrayList<TextField> tableColumns) {
        for (int i = 0; i < table.getColumnsAmount(); i++) {
            // Get column
            Column column = table.getColumn(i);

            // Create and set table column text field
            TextField tableColumn = new TextField(column.getName());
            tableColumn.setEditable(false);
            tableColumn.setFocusTraversable(false);
            tableColumn.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
            tableColumn.setStyle("-fx-background-color: #E6E6E6; -fx-font-weight: bold; -fx-cursor: hand;");

            if (column.isPrimary()) tableColumn.setStyle(tableColumn.getStyle() + "-fx-text-fill: blue");
            else tableColumn.setStyle(tableColumn.getStyle() + "-fx-text-fill: #1f64ca;");


            // Set relative location of text field
            if (i == 0) tableColumn.setLayoutX(0);
            else tableColumn.setLayoutX(i * TABLE_CELL_WIDTH);
            tableColumn.setLayoutY(0);

            // Set mouse hover effect
            tableColumn.setOnMouseEntered(event -> tableColumn.setText(column.toString()));
            tableColumn.setOnMouseExited(event -> tableColumn.setText(column.getName()));

            // Set mouse click effect
            tableColumn.setOnMouseClicked(event -> {
                try {
                    sortTable(column);
                } catch (NoteException | TableException e) {
                    new MessageWindow(e.toString(), e.getMessage()).show();
                }
            });

            // Add table column to table columns array
            tableColumns.add(tableColumn);
        }
    }

    private void setRows() throws NoteException {
        tableRows = new ArrayList<>(table.getNotesAmount());

        for (int i = 0; i < table.getNotesAmount(); i++) {
            // Get note
            Note note = table.getNote(i);

            // Create and set row cells array
            ArrayList<TextField> rowCells = new ArrayList<>(note.getCells().size());
            for (int j = 0; j < note.getCells().size(); j++) {
                // Get cell dependent by column
                Cell cell = note.getCellByColumnName(tableColumns.get(j).getText());

                // Create and set row cell text field
                TextField rowCell = new TextField(cell.getContent());
                rowCell.setFocusTraversable(false);
                rowCell.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);

                // Set relative location of text field
                rowCell.setLayoutX(j * TABLE_CELL_WIDTH);
                if (i == 0) rowCell.setLayoutY(TABLE_CELL_HEIGHT + 5);
                else rowCell.setLayoutY((i + 1) * TABLE_CELL_HEIGHT + 5 * (i + 1));

                // Set delete context menu
                MenuItem deleteNote = new MenuItem("Delete note");
                deleteNote.getStyleClass().add(".menu-item");
                deleteNote.setOnAction(event -> {
                    table.removeNote(note);
                    refresh();
                });
                if (deleteButton.getText().equals("Delete")) {
                    deleteNote.setVisible(false);

                    rowCell.setEditable(true);
                    rowCell.setStyle("-fx-cursor: text;");
                } else {
                    deleteNote.setVisible(true);

                    rowCell.setEditable(false);
                    rowCell.setStyle("-fx-cursor: hand;");
                }

                rowCell.setContextMenu(new ContextMenu(deleteNote));

                // Add row cell to row cells array
                rowCells.add(rowCell);
            }

            // Add row row cells array to table rows
            tableRows.add(rowCells);
        }
    }

    private void refresh() {
        tableColumns = new ArrayList<>();
        setColumns(tableColumns);
        try {
            setRows();
        } catch (NoteException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
            e.printStackTrace();
        }

        tablePanel = new Pane();
        tablePanel.getChildren().addAll(tableColumns);
        for (ArrayList<TextField> cells : tableRows) {
            tablePanel.getChildren().addAll(cells);
        }

        tablePanelScroll.setContent(tablePanel);
    }

    private void sortTable(Column column) throws NoteException, TableException {
        table.sortTableNotesByColumn(column);
        refresh();
    }

    private void saveTable() {
        for (int i = 0; i < tableRows.size(); i++) {
            ArrayList<TextField> rowCells = tableRows.get(i);
            Note note = table.getNote(i);
            ArrayList<Cell> cells = note.getCells();
            for (int j = 0; j < rowCells.size(); j++) {
                try {
                    cells.get(j).setContent(rowCells.get(j).getText());
                } catch (CellException e) {
                    new MessageWindow(e.toString(), e.getMessage()).show();
                }
            }
        }

        if (Table.isPrimaryKeysDuplicates(table.getNotes())) {
            new MessageWindow("engine.TableException", "Duplicates primary keys.").show();
            return;
        }
        if (Table.isColumnNameDuplicates(table.getColumns())) {
            new MessageWindow("engine.TableException", "Duplicates column names.").show();
            return;
        }

        table.setName(tableNameField.getText());

        try {
            storage.saveTableToDB(table, databaseFile);
        } catch (ParserConfigurationException | TransformerException | IOException | XPathExpressionException | StorageException | SAXException | ColumnException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }

        MessageWindow successWindow = new MessageWindow("Success",
                "Table '" + table.getName() + "' was successfully saved to database '" + databaseFile.getName() + "'.");
        successWindow.getMessageArea().setStyle("-fx-text-fill: green;");
        successWindow.setWidth(400);
        successWindow.setHeight(100);
        successWindow.show();
    }

    private void removeTable() {
        try {
            storage.deleteTableFromDB(table.getName(), databaseFile);
        } catch (ParserConfigurationException | TransformerException | IOException | XPathExpressionException | StorageException | SAXException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }

        MessageWindow successWindow = new MessageWindow("Success",
                "Table '" + table.getName() + "' was successfully removed from database '" + databaseFile.getName() + "'.");
        successWindow.getMessageArea().setStyle("-fx-text-fill: green;");
        successWindow.setWidth(400);
        successWindow.setHeight(100);
        successWindow.setOnCloseRequest(event -> this.close());
        successWindow.show();
    }

    private void alterColumns() {
        new AlterColumnsWindow(table, databaseFile, storage).show();
    }

    private void deleteNote() {
        if (deleteButton.getText().equals("Delete")) {
            deleteButton.setText("Edit");
            deleteButton.getStyleClass().remove("exit_button");
            deleteButton.getStyleClass().add("other_button");

            for (ArrayList<TextField> rows : tableRows) {
                for (TextField cell : rows) {
                    cell.setEditable(false);
                    cell.setStyle("-fx-cursor: hand;");

                    cell.getContextMenu().getItems().get(0).setVisible(true);
                }
            }
        } else {
            deleteButton.setText("Delete");
            deleteButton.getStyleClass().remove("other_button");
            deleteButton.getStyleClass().add("exit_button");

            for (ArrayList<TextField> rows : tableRows) {
                for (TextField cell : rows) {
                    cell.setEditable(true);
                    cell.setStyle("-fx-cursor: text;");

                    cell.getContextMenu().getItems().get(0).setVisible(false);
                }
            }
        }
    }

    private void addNote() {
        // Set note columns
        ArrayList<TextField> newNoteColumns = new ArrayList<>();
        setColumns(newNoteColumns);

        // Set panel and add columns
        Pane newNotePanel = new Pane();
        newNotePanel.getChildren().addAll(newNoteColumns);
        ScrollPane scrollPane = new ScrollPane(newNotePanel);
        scrollPane.setPrefSize(600, 65);
        scrollPane.setLayoutX(10);
        scrollPane.setLayoutY(addButton.getLayoutY() + addButton.getPrefHeight() + 30);

        // Set text fields for new note
        ArrayList<TextField> row = new ArrayList<>(table.getColumnsAmount());
        for (int i = 0; i < table.getColumnsAmount(); i++) {
            TextField rowCell = new TextField();
            rowCell.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
            rowCell.setLayoutX(i * TABLE_CELL_WIDTH);
            rowCell.setLayoutY(TABLE_CELL_HEIGHT + 5);

            row.add(rowCell);
        }
        newNotePanel.getChildren().addAll(row);

        Button add = new Button("Add");
        add.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: green;");
        add.setPrefSize(TABLE_CELL_WIDTH / 2, 8);
        add.setLayoutX(scrollPane.getLayoutX());
        add.setLayoutY(scrollPane.getLayoutY() + scrollPane.getPrefHeight() + 5);
        add.setOnAction(event -> {
            Note note = new Note();

            for (int i = 0; i < table.getColumnsAmount(); i++) {
                try {
                    Cell cell = new Cell(row.get(i).getText(), table.getColumn(i));
                    note.getCells().add(cell);
                } catch (CellException e) {
                    new MessageWindow(e.toString(), e.getMessage()).show();
                }
            }

            try {
                table.addNote(note);
            } catch (TableException e) {
                new MessageWindow(e.toString(), e.getMessage()).show();
            }

            refresh();
        });

        Button collapse = new Button("Curtail");
        collapse.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: red;");
        collapse.setPrefSize(TABLE_CELL_WIDTH / 2, 8);
        collapse.setLayoutX(scrollPane.getLayoutX() + scrollPane.getPrefWidth() - collapse.getPrefWidth());
        collapse.setLayoutY(add.getLayoutY());
        collapse.setOnAction(event -> {
            rootPanel.getChildren().removeAll(scrollPane, add, collapse);
            scene.getWindow().setHeight(scene.getWindow().getHeight() - 100);
        });

        // Reset window
        rootPanel.getChildren().addAll(scrollPane, add, collapse);
        scene.getWindow().setHeight(scene.getWindow().getHeight() + 100);
    }
}