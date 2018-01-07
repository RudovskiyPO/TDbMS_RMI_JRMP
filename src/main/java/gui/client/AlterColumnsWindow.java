package gui.client;

import engine.core.Cell;
import engine.core.Column;
import engine.core.Note;
import engine.core.Table;
import engine.exceptions.*;
import engine.service.Storage;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class AlterColumnsWindow extends Stage {
    /**
     * Attributes
     */
    private Scene scene;
    private Pane rootPanel;

    private Pane tablePanel;
    private ScrollPane tablePanelScroll;
    private ArrayList<TextField> tableColumns;
    private ArrayList<ComboBox> tableColumnsTypes;
    private ArrayList<TextField> tableColumnsRegexes;
    private ArrayList<CheckBox> tableColumnsIsPrimary;

    private Button saveChanges;
    private Button removeColumn;
    private Button addColumn;
    private Button cancelChanges;

    private static double TABLE_CELL_WIDTH = 150;
    private static double TABLE_CELL_HEIGHT = 20;
    private Table table;
    private File databaseFile;

    /**
     * Constructor
     */
    AlterColumnsWindow(Table table, File databaseFile, final Storage storage) {
        this.table = table;
        this.databaseFile = databaseFile;

        // Set table data
        try {
            setTableData();
        } catch (ColumnException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }

        // Set table panel
        tablePanel = new Pane();
        tablePanel.getChildren().addAll(tableColumns);
        tablePanel.getChildren().addAll(tableColumnsTypes);
        tablePanel.getChildren().addAll(tableColumnsRegexes);
        tablePanel.getChildren().addAll(tableColumnsIsPrimary);

        // Set scroll table panel panel
        tablePanelScroll = new ScrollPane(tablePanel);
        tablePanelScroll.setPrefSize(600, 110);
        tablePanelScroll.setLayoutX(10);
        tablePanelScroll.setLayoutY(10);

        // Set save button
        saveChanges = new Button("Save");
        saveChanges.getStyleClass().add("ok");
        saveChanges.setPrefSize(90, 33);
        saveChanges.setLayoutX(tablePanelScroll.getLayoutX() + 10);
        saveChanges.setLayoutY(tablePanelScroll.getLayoutY() + tablePanelScroll.getPrefHeight() + 20);
        saveChanges.setOnAction(event -> {
            try {
                saveChanges(storage);
                Stage stage = (Stage) saveChanges.getScene().getWindow();
                stage.close();
            } catch (ColumnException | NoteException | CellException e) {
                new MessageWindow(e.toString(), e.getMessage()).show();
            }
        });

        // Set remove column button
        removeColumn = new Button("Remove");
        removeColumn.getStyleClass().add("exit_button");
        removeColumn.setPrefSize(90, 33);
        removeColumn.setLayoutX(saveChanges.getLayoutX() + saveChanges.getPrefWidth() + 70);
        removeColumn.setLayoutY(saveChanges.getLayoutY());
        removeColumn.setOnAction(event -> removeColumn());

        // Set add column button
        addColumn = new Button("Add");
        addColumn.getStyleClass().add("other_button");
        addColumn.setPrefSize(90, 33);
        addColumn.setLayoutX(removeColumn.getLayoutX() + removeColumn.getPrefWidth() + 70);
        addColumn.setLayoutY(removeColumn.getLayoutY());
        addColumn.setOnAction(event -> addColumn());

        // Set cancel button
        cancelChanges = new Button("Cancel");
        cancelChanges.getStyleClass().add("exit_button");
        cancelChanges.setPrefSize(90, 33);
        cancelChanges.setLayoutX(tablePanelScroll.getLayoutX() + tablePanelScroll.getPrefWidth() - saveChanges.getPrefWidth() - 10);
        cancelChanges.setLayoutY(saveChanges.getLayoutY());
        cancelChanges.setOnAction(event -> {
            Stage stage = (Stage) cancelChanges.getScene().getWindow();
            stage.close();
        });

        // Set root panel
        rootPanel = new Pane();
        rootPanel.getChildren().addAll(tablePanelScroll, saveChanges, removeColumn, addColumn, cancelChanges);

        // Set scene
        scene = new Scene(rootPanel, tablePanelScroll.getPrefWidth() + 10, tablePanelScroll.getPrefHeight() + 80);
        scene.getStylesheets().add("/css/main.css");

        // Set stage
        setTitle(table.getName() + ":Alter columns");
        setScene(scene);
        setResizable(false);
        setX(570);
        setY(520);
    }

    /**
     * Methods
     */
    private void setTableData() throws ColumnException {
        tableColumns = new ArrayList<>(table.getColumnsAmount());
        tableColumnsTypes = new ArrayList<>(table.getColumnsAmount());
        tableColumnsRegexes = new ArrayList<>(table.getColumnsAmount());
        tableColumnsIsPrimary = new ArrayList<>(table.getColumnsAmount());

        for (int i = 0; i < table.getColumnsAmount(); i++) {
            // Get column
            Column column = table.getColumn(i);

            // Create and set table column text field
            TextField tableColumn = new TextField(column.getName());
            tableColumn.setFocusTraversable(false);
            tableColumn.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
            // Set relative location of text field
            if (i == 0) tableColumn.setLayoutX(0);
            else tableColumn.setLayoutX(i * TABLE_CELL_WIDTH);
            tableColumn.setLayoutY(0);

            // Create and set table column type combo box
            ComboBox<String> tableColumnType = new ComboBox<>(FXCollections.observableArrayList(Column.getTypesList()));
            tableColumnType.setFocusTraversable(false);
            tableColumnType.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
            tableColumnType.getSelectionModel().select(column.getTypeString());
            // Set relative location of text field
            if (i == 0) tableColumnType.setLayoutX(0);
            else tableColumnType.setLayoutX(i * TABLE_CELL_WIDTH);
            tableColumnType.setLayoutY(TABLE_CELL_HEIGHT + 5);

            // Create and set table column regex text field
            TextField tableColumnRegex = new TextField(column.getIntervalRegex());
            tableColumnRegex.setFocusTraversable(false);
            tableColumnRegex.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
            // Set relative location of text field
            if (i == 0) tableColumnRegex.setLayoutX(0);
            else tableColumnRegex.setLayoutX(i * TABLE_CELL_WIDTH);
            tableColumnRegex.setLayoutY(2 * (TABLE_CELL_HEIGHT + 5));
            // Set regex text field style
            if (column.getIntervalRegex().isEmpty()) tableColumnRegex.setVisible(false); //tableColumnRegex.setStyle("-fx-background-color: #E6E6E6;");

            // Create and set table column is primary check box
            CheckBox tableColumnIsPrimary = new CheckBox("PK");
            tableColumnIsPrimary.setFocusTraversable(false);
            tableColumnIsPrimary.setSelected(column.isPrimary());
            // Set relative location of check box
            if (i == 0) tableColumnIsPrimary.setLayoutX(50);
            else tableColumnIsPrimary.setLayoutX(i * TABLE_CELL_WIDTH + 50);
            tableColumnIsPrimary.setLayoutY(3 * (TABLE_CELL_HEIGHT + 5));
            // Set on checked
            tableColumnIsPrimary.setOnMouseClicked(event -> {
                for (CheckBox checkBox : tableColumnsIsPrimary) {
                    if(checkBox != tableColumnIsPrimary) checkBox.setSelected(false);
                }
            });

            // Add elements to table
            tableColumns.add(tableColumn);
            tableColumnsTypes.add(tableColumnType);
            tableColumnsRegexes.add(tableColumnRegex);
            tableColumnsIsPrimary.add(tableColumnIsPrimary);
        }
    }

    private void saveChanges(Storage storage) throws ColumnException, NoteException, CellException {
        for (int i = 0; i < tableColumns.size(); i++) {
            Column column = table.getColumn(i);

            column.setName(tableColumns.get(i).getText());

            column.setPrimary(tableColumnsIsPrimary.get(i).isSelected());

            int type = Column.getTypeIntFromTypeString((String) tableColumnsTypes.get(i).getSelectionModel().getSelectedItem());

            switch (type) {
                case Column.TYPE_INT:
                case Column.TYPE_REAL:
                case Column.TYPE_CHAR:
                    column.setType(type);
                    break;
                case Column.TYPE_CHAR_INTERVAL:
                case Column.TYPE_STRING_OF_CHAR_INTERVAL:
                    column.setType(type, tableColumnsRegexes.get(i).getText());
            }

            try {
                storage.saveTableToDB(table, databaseFile);
            } catch (ParserConfigurationException | TransformerException | IOException | XPathExpressionException | StorageException | SAXException e) {
                new MessageWindow(e.toString(), e.getMessage()).show();
            }
        }
    }

    private void removeColumn() {
        // Set combo box with columns
        ListView<Text> columnsListView = new ListView<>();
        for (Column column : table.getColumns()) {
            Text text = new Text(column.getName());
            text.getStyleClass().add("list_item");

            text.setOnMouseClicked(event -> {

                for (Note note : table.getNotes()) {
                    try {
                        note.getCells().remove(note.getCellByColumn(column));
                    } catch (NoteException e) {
                        new MessageWindow(e.toString(), e.getMessage()).show();
                    }
                }

                table.removeColumn(column);
                columnsListView.getItems().remove(text);
                refresh();
            });

            text.setOnMouseEntered(event -> text.setStyle("-fx-fill: red;"));
            text.setOnMouseExited(event -> text.setStyle("-fx-fill: blue;"));

            columnsListView.getItems().add(text);
        }
        columnsListView.setFocusTraversable(false);
        columnsListView.setPrefSize(90, 100);
        columnsListView.setLayoutX(removeColumn.getLayoutX());
        columnsListView.setLayoutY(removeColumn.getLayoutY() + removeColumn.getPrefHeight() + 20);

        // Set button for cancel deletion
        Button collapse = new Button("Curtail");
        collapse.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-font-weight: bold;");
        collapse.setPrefSize(90, 8);
        collapse.setLayoutX(columnsListView.getLayoutX());
        collapse.setLayoutY(columnsListView.getLayoutY() + columnsListView.getPrefHeight());
        collapse.setOnAction(event -> {
            rootPanel.getChildren().removeAll(columnsListView, collapse);
            scene.getWindow().setHeight(scene.getWindow().getHeight() - tablePanelScroll.getPrefHeight() - 20);
        });

        // Reset window
        rootPanel.getChildren().addAll(columnsListView, collapse);
        scene.getWindow().setHeight(scene.getWindow().getHeight() + tablePanelScroll.getPrefHeight() + 20);
    }

    private void addColumn() {
        // Set column name field
        TextField newColumnName = new TextField("Column name...");
        newColumnName.setFocusTraversable(false);
        newColumnName.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
        newColumnName.setLayoutX(addColumn.getLayoutX());
        newColumnName.setLayoutY(addColumn.getLayoutY() + addColumn.getPrefHeight() + 20);
        newColumnName.setOnMouseClicked(event -> newColumnName.setText(""));

        // Set table column type combo box
        ComboBox<String> tableColumnType = new ComboBox<>(FXCollections.observableArrayList(Column.getTypesList()));
        tableColumnType.setFocusTraversable(false);
        tableColumnType.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
        tableColumnType.getSelectionModel().select(0);
        tableColumnType.setLayoutX(newColumnName.getLayoutX());
        tableColumnType.setLayoutY(newColumnName.getLayoutY() + newColumnName.getPrefHeight() + 5);

        // Set table column regex text field
        TextField tableColumnRegex = new TextField("");
        tableColumnRegex.setFocusTraversable(false);
        tableColumnRegex.setPrefSize(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
        tableColumnRegex.setLayoutX(tableColumnType.getLayoutX());
        tableColumnRegex.setLayoutY(tableColumnType.getLayoutY() + tableColumnType.getPrefHeight() + 5);

        // Set add new column button
        Button add = new Button("Add");
        add.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: green;");
        add.setPrefSize(TABLE_CELL_WIDTH / 2, 8);
        add.setLayoutX(tableColumnRegex.getLayoutX());
        add.setLayoutY(tableColumnRegex.getLayoutY() + tableColumnRegex.getPrefHeight() + 5);
        add.setOnAction(event -> {
            int type = 0;
            try {
                type = Column.getTypeIntFromTypeString(tableColumnType.getSelectionModel().getSelectedItem());
            } catch (ColumnException e) {
                new MessageWindow(e.toString(), e.getMessage()).show();
            }

            switch (type) {
                case Column.TYPE_INT:
                case Column.TYPE_REAL:
                case Column.TYPE_CHAR:
                    try {
                        table.addColumn(new Column(newColumnName.getText(), type));
                    } catch (TableException | ColumnException e) {
                        new MessageWindow(e.toString(), e.getMessage()).show();
                    }
                    break;
                case Column.TYPE_CHAR_INTERVAL:
                case Column.TYPE_STRING_OF_CHAR_INTERVAL:
                    try {
                        table.addColumn(new Column(newColumnName.getText(), type, tableColumnRegex.getText()));
                    } catch (TableException | ColumnException e) {
                        new MessageWindow(e.toString(), e.getMessage()).show();
                    }
            }

            // Add empty cells for new column to each note
            for(Note note : table.getNotes()) {
                try {
                    note.getCells().add(new Cell("", table.getColumn(table.getColumnsAmount() - 1)));
                } catch (CellException e) {
                    new MessageWindow(e.toString(), e.getMessage()).show();
                }
            }

            refresh();
        });

        // Set delete column button
        Button cancel = new Button("Curtail");
        cancel.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: red;");
        cancel.setPrefSize(TABLE_CELL_WIDTH / 2, 8);
        cancel.setLayoutX(add.getLayoutX() + add.getPrefWidth());
        cancel.setLayoutY(add.getLayoutY());
        cancel.setOnAction(event -> {
            rootPanel.getChildren().removeAll(newColumnName, tableColumnType, tableColumnRegex, add, cancel);
            scene.getWindow().setHeight(scene.getWindow().getHeight() - tablePanelScroll.getPrefHeight() - 20);
        });

        // Reset window
        rootPanel.getChildren().addAll(newColumnName, tableColumnType, tableColumnRegex, add, cancel);
        scene.getWindow().setHeight(scene.getWindow().getHeight() + tablePanelScroll.getPrefHeight() + 20);
    }

    private void refresh() {
        try {
            setTableData();
        } catch (ColumnException e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }

        tablePanel = new Pane();
        tablePanel.getChildren().addAll(tableColumns);
        tablePanel.getChildren().addAll(tableColumnsTypes);
        tablePanel.getChildren().addAll(tableColumnsRegexes);
        tablePanel.getChildren().addAll(tableColumnsIsPrimary);

        tablePanelScroll.setContent(tablePanel);
    }
}
