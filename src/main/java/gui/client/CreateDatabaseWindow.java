package gui.client;

import engine.core.DataBase;
import engine.core.Table;
import engine.exceptions.*;
import engine.service.Storage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import java.rmi.RemoteException;

class CreateDatabaseWindow extends Stage {
    /**
     * Attributes
     */
    private Scene scene;
    private Pane rootPanel;

    private Text databaseNameText;
    private TextField databaseNameField;

    private Pane credentialsPanel;
    private Text credentialsText;
    private TextField loginField;
    private TextField passwordField;

    private Text tablesText;
    private ListView<Text> tablesListView;

    private Button importButton;
    private Button saveDatabase;
    private Button refreshButton;
    private Button openTable;
    private Button createTable;
    private Button exitButton;

    private Pane importPanel;

    private DataBase dataBase;
    private static DataBase extractedDatabase;
    private final Storage storage;

    /**
     * Constructor
     */
    CreateDatabaseWindow(Storage storage) {
        // Set storage
        this.storage = storage;
        // Set database name text
        databaseNameText = new Text("Database name:");
        databaseNameText.getStyleClass().add("header_of_list");
        databaseNameText.setLayoutX(10);
        databaseNameText.setLayoutY(20);

        // Set database name field
        String nameFieldPlaceholder = "Enter database name here...";
        databaseNameField = new TextField(nameFieldPlaceholder);
        databaseNameField.setPrefSize(300, 20);
        databaseNameField.setLayoutX(10);
        databaseNameField.setLayoutY(23);
        databaseNameField.setOnMouseClicked(event -> {
            if (databaseNameField.getText().equals(nameFieldPlaceholder))
                databaseNameField.setText("");
        });

        // Set import button
        importButton = new Button("Import");
        importButton.getStyleClass().add("other_button");
        importButton.setPrefSize(90, 33);
        importButton.setLayoutX(325);
        importButton.setLayoutY(18);
        importButton.setOnAction(event -> importDatabase());

        // Set credentials text
        credentialsText = new Text("Credentials:");
        credentialsText.getStyleClass().add("header_of_list");
        credentialsText.setLayoutX(10);
        credentialsText.setLayoutY(78);

        // Set login field
        String loginFieldPlaceholder = "Enter database login here...";
        loginField = new TextField(loginFieldPlaceholder);
        loginField.setPrefSize(300, 20);
        loginField.setLayoutX(5);
        loginField.setLayoutY(5);
        loginField.setFocusTraversable(false);
        loginField.setOnMouseClicked(event -> {
            if (loginField.getText().equals(loginFieldPlaceholder))
                loginField.setText("");
        });

        // Set password field
        String passwordFieldPlaceholder = "Enter database password here...";
        passwordField = new TextField(passwordFieldPlaceholder);
        passwordField.setPrefSize(300, 20);
        passwordField.setLayoutX(5);
        passwordField.setLayoutY(35);
        passwordField.setFocusTraversable(false);
        passwordField.setOnMouseClicked(event -> {
            if (passwordField.getText().equals(passwordFieldPlaceholder))
                passwordField.setText("");
        });

        // Set save credentials button
        saveDatabase = new Button("Save");
        saveDatabase.getStyleClass().add("ok");
        saveDatabase.setLayoutX(325);
        saveDatabase.setLayoutY(95);
        saveDatabase.setPrefSize(90, 33);
        saveDatabase.setFocusTraversable(false);
        saveDatabase.setOnAction(event -> saveDatabase());

        // Set credentials panel
        credentialsPanel = new Pane();
        credentialsPanel.getStyleClass().add("credentials_panel");
        credentialsPanel.setPrefSize(310, 65);
        credentialsPanel.setLayoutX(10);
        credentialsPanel.setLayoutY(80);
        credentialsPanel.getChildren().addAll(loginField, passwordField);

        // Set tables text
        tablesText = new Text("Tables:");
        tablesText.getStyleClass().add("header_of_list");
        tablesText.setLayoutX(10);
        tablesText.setLayoutY(170);

        // Set refresh button
        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("refresh");
        refreshButton.setPrefSize(60, 25);
        refreshButton.setLayoutX(254);
        refreshButton.setLayoutY(147);
        refreshButton.setFocusTraversable(false);
        refreshButton.setOnAction(event -> refreshWindow());

        // Set tables list view
        tablesListView = new ListView<>();
        tablesListView.setLayoutX(10);
        tablesListView.setLayoutY(172);
        tablesListView.setPrefSize(304, 250);

        // Set open table button
        openTable = new Button("Open");
        openTable.getStyleClass().add("other_button");
        openTable.setPrefSize(90, 33);
        openTable.setLayoutX(325);
        openTable.setLayoutY(200);
        openTable.setOnAction(event -> openTable());

        // Set create table button
        createTable = new Button("Create");
        createTable.getStyleClass().add("other_button");
        createTable.setPrefSize(90, 33);
        createTable.setLayoutX(325);
        createTable.setLayoutY(280);
        createTable.setOnAction(event -> createTable());

        // Set exit button
        exitButton = new Button("Close");
        exitButton.getStyleClass().add("exit_button");
        exitButton.setPrefSize(90, 33);
        exitButton.setLayoutX(325);
        exitButton.setLayoutY(360);
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        // Set root panel
        rootPanel = new Pane();
        rootPanel.getChildren().addAll(databaseNameText, databaseNameField, importButton, credentialsText, saveDatabase, credentialsPanel, tablesText, refreshButton, tablesListView, openTable, createTable, exitButton);

        // Set scene
        scene = new Scene(rootPanel, 420, 430);
        scene.getStylesheets().add("/css/main.css");

        // Set stage
        setTitle("New database");
        setScene(scene);
        setResizable(false);
        setX(500);
        setY(100);
    }

    /**
     * Methods
     */
    private void refreshWindow() {
        if(dataBase == null) return;

        try {
            dataBase.setTables(storage.extractDatabase(storage.getDatabaseFileByName(dataBase.getName())).getTables());
        } catch (Exception e) {
            new MessageWindow(e.toString(), "No database to refresh. Database did not create yet.").show();
        }
        tablesListView.getItems().removeAll(tablesListView.getItems());
        for (Table table : dataBase.getTables()) {
            Text text = new Text(table.getName());
            text.getStyleClass().add("list_item");
            tablesListView.getItems().add(text);
        }
        tablesListView.refresh();

        databaseNameField.setText(dataBase.getName());

        loginField.setText(dataBase.getCredentials().getLogin());
        passwordField.setText(dataBase.getCredentials().getPassword());
    }

    private void importDatabase() {
        // Set path field
        String pathFieldPlaceholder = "Enter full path to file here...";
        TextField pathField = new TextField(pathFieldPlaceholder);
        pathField.setPrefSize(300, 20);
        pathField.setLayoutX(5);
        pathField.setLayoutY(5);
        pathField.setFocusTraversable(false);
        pathField.setOnMouseClicked(event -> {
            if (pathField.getText().equals(pathFieldPlaceholder))
                pathField.setText("");
        });

        // Set extracted database textarea
        TextArea showDatabase = new TextArea();
        showDatabase.setPrefSize(300, 200);
        showDatabase.setLayoutX(5);
        showDatabase.setLayoutY(80);
        showDatabase.setEditable(false);

        // Set extract button
        Button extractButton = new Button("Extract");
        extractButton.getStyleClass().add("other_button");
        extractButton.setPrefSize(90, 33);
        extractButton.setLayoutX(210);
        extractButton.setLayoutY(35);
        extractButton.setOnAction(event -> {
            try {
                extractedDatabase = storage.extractDatabase(new File(pathField.getText()));
                showDatabase.setText(extractedDatabase.toString());
            } catch (Exception e) {
                new MessageWindow(e.toString(), e.toString() + "\n" + e.getMessage()).show();
            }
        });

        // Set import panel
        importPanel = new Pane();
        importPanel.getStyleClass().add("top_panel");
        importPanel.setPrefSize(310, 330);
        importPanel.setLayoutX(440);
        importPanel.setLayoutY(15);

        // Set close import button
        Button cancelImport = new Button("Cancel");
        cancelImport.getStyleClass().add("exit_button");
        cancelImport.setPrefSize(90, 33);
        cancelImport.setLayoutX(200);
        cancelImport.setLayoutY(285);
        cancelImport.setOnAction(event -> {
            rootPanel.getChildren().remove(importPanel);
            scene.getWindow().setWidth(440);
        });

        // Set import extracted database button
        Button importExtractedDatabase = new Button("Import");
        importExtractedDatabase.getStyleClass().add("ok");
        importExtractedDatabase.setPrefSize(90, 33);
        importExtractedDatabase.setLayoutX(30);
        importExtractedDatabase.setLayoutY(285);
        importExtractedDatabase.setOnAction(event -> {
            try {
                dataBase = storage.extractDatabase(new File(pathField.getText()));
            } catch (Exception e) {
                new MessageWindow(e.toString(), e.toString() + "\n" + e.getMessage()).show();
            }

            rootPanel.getChildren().remove(importPanel);
            scene.getWindow().setWidth(440);

            refreshWindow();
        });

        // Change scene
        importPanel.getChildren().addAll(pathField, extractButton, showDatabase, cancelImport, importExtractedDatabase);
        rootPanel.getChildren().add(importPanel);
        scene.getWindow().setWidth(770);
    }

    private void saveDatabase() {
        dataBase = new DataBase();

        if(extractedDatabase != null) {
            try {
                dataBase.setTables(extractedDatabase.getTables());
            } catch (DataBaseException e) {
                new MessageWindow(e.toString(), e.getMessage() + "\n" + e.toString()).show();
            }
        }

        dataBase.setName(databaseNameField.getText());
        dataBase.getCredentials().setLogin(loginField.getText());
        dataBase.getCredentials().setPassword(passwordField.getText());

        try {
            storage.createDatabase(dataBase);
        } catch (Exception e) {
            new MessageWindow(e.toString(), e.getMessage() + "\n" + e.toString()).show();
            try {
                if(!storage.getDatabaseFileByName(dataBase.getName()).delete()) throw new StorageException(e.toString() + "\n" + e.getMessage());
            } catch (StorageException | RemoteException e1) {
                new MessageWindow(e.toString(), e.getMessage() + "\n" + e.toString()).show();
            }
            return;
        }

        MessageWindow successWindow = new MessageWindow("Success", "Database '" + dataBase.getName() + "' was was successfully created.");
        successWindow.getMessageArea().setStyle("-fx-text-fill: green;");
        successWindow.setWidth(350);
        successWindow.setHeight(100);
        successWindow.show();
        //successWindow.setOnCloseRequest(event -> this.close());
    }

    private void openTable() {
        Text table = tablesListView.getSelectionModel().getSelectedItem();

        try {
            new TableWindow(dataBase.getTableByName(table.getText()), storage.getDatabaseFileByName(dataBase.getName()), storage).show();
        } catch (Exception e) {
            new MessageWindow(e.toString(), "No valid table selected.").show();
        }

        tablesListView.getSelectionModel().clearSelection();
    }

    private void createTable() {
        Table table = new Table("NewTable");

        try {
            if(dataBase == null || storage.getDatabaseFileByName(dataBase.getName()) == null) {
                new MessageWindow("Can not create table", "Save new current database before creation new tables.").show();
                return;
            }

            new TableWindow(table, storage.getDatabaseFileByName(dataBase.getName()), storage).show();
        } catch (Exception e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }
    }
}
