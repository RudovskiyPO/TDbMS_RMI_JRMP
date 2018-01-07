package gui.client;

import engine.core.Credentials;
import engine.core.DataBase;
import engine.core.Table;
import engine.exceptions.*;
import engine.service.Storage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

class DatabaseWindow extends Stage {
    /**
     * Attributes
     */
    private Scene scene;
    private Pane rootPanel;

    private Pane credentialsPanel;
    private Text credentialsText;
    private TextField loginField;
    private TextField passwordField;

    private Text tablesText;
    private ListView<Text> tablesListView;

    private Button saveCredentials;
    private Button refreshButton;
    private Button removeDatabase;
    private Button openTable;
    private Button createTable;
    private Button exitButton;

    private final Storage storage;
    private DataBase dataBase;

    /**
     * Constructor
     */
    DatabaseWindow(DataBase dataBase, Storage storage) throws IOException, SAXException, StorageException, CellException, TableException, ParserConfigurationException, ColumnException, NoteException, DataBaseException {
        // Set storage
        this.storage = storage;

        // Set database
        this.dataBase = dataBase;

        // Set credentials text
        credentialsText = new Text("Credentials");
        credentialsText.getStyleClass().add("header_of_list");
        credentialsText.setLayoutX(10);
        credentialsText.setLayoutY(28);

        // Set login field
        loginField = new TextField(dataBase.getCredentials().getLogin());
        loginField.setPrefSize(300, 20);
        loginField.setLayoutX(5);
        loginField.setLayoutY(5);
        loginField.setFocusTraversable(false);

        // Set password field
        passwordField = new TextField(dataBase.getCredentials().getPassword());
        passwordField.setPrefSize(300, 20);
        passwordField.setLayoutX(5);
        passwordField.setLayoutY(35);
        passwordField.setFocusTraversable(false);

        // Set save credentials button
        saveCredentials = new Button("Save");
        saveCredentials.getStyleClass().add("ok");
        saveCredentials.setLayoutX(310);
        saveCredentials.setLayoutY(13);
        saveCredentials.setPrefSize(90, 33);
        saveCredentials.setFocusTraversable(false);
        saveCredentials.setOnAction(event -> saveCredentialsToDB());

        // Set credentials panel
        credentialsPanel = new Pane();
        credentialsPanel.getStyleClass().add("credentials_panel");
        credentialsPanel.setPrefSize(405, 65);
        credentialsPanel.setLayoutX(10);
        credentialsPanel.setLayoutY(30);
        credentialsPanel.getChildren().addAll(loginField, passwordField, saveCredentials);

        // Set tables text
        tablesText = new Text("Tables:");
        tablesText.getStyleClass().add("header_of_list");
        tablesText.setLayoutX(10);
        tablesText.setLayoutY(120);

        // Set refresh button
        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("refresh");
        refreshButton.setPrefSize(60, 25);
        refreshButton.setLayoutX(254);
        refreshButton.setLayoutY(97);
        refreshButton.setFocusTraversable(false);
        refreshButton.setOnAction(event -> refreshWindow());

        // Set tables list view
        tablesListView = new ListView<>();
        tablesListView.setLayoutX(10);
        tablesListView.setLayoutY(122);
        tablesListView.setPrefSize(304, 250);
        for (Table table : dataBase.getTables()) {
            Text text = new Text(table.getName());
            text.getStyleClass().add("list_item");
            tablesListView.getItems().add(text);
        }

        // Set remove database
        removeDatabase = new Button("Remove");
        removeDatabase.getStyleClass().add("exit_button");
        removeDatabase.setPrefSize(90, 33);
        removeDatabase.setLayoutX(320);
        removeDatabase.setLayoutY(130);
        removeDatabase.setOnAction(event -> removeDatabase());

        // Set open table button
        openTable = new Button("Open");
        openTable.getStyleClass().add("other_button");
        openTable.setPrefSize(90, 33);
        openTable.setLayoutX(320);
        openTable.setLayoutY(195);
        openTable.setOnAction(event -> openTable());

        // Set create table button
        createTable = new Button("Create");
        createTable.getStyleClass().add("other_button");
        createTable.setPrefSize(90, 33);
        createTable.setLayoutX(320);
        createTable.setLayoutY(260);
        createTable.setOnAction(event -> createTable());

        // Set exit button
        exitButton = new Button("Close");
        exitButton.getStyleClass().add("exit_button");
        exitButton.setPrefSize(90, 33);
        exitButton.setLayoutX(320);
        exitButton.setLayoutY(325);
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        // Set root panel
        rootPanel = new Pane();
        rootPanel.getChildren().addAll(credentialsText, credentialsPanel, tablesText, refreshButton, tablesListView, removeDatabase, openTable, createTable, exitButton);

        // Set scene
        scene = new Scene(rootPanel, 420, 380);
        scene.getStylesheets().add("/css/main.css");

        // Set stage
        setTitle(dataBase.getName().toUpperCase());
        setScene(scene);
        setResizable(false);
        setX(500);
        setY(100);
    }

    /**
     * Methods
     */
    private void saveCredentialsToDB() {
        Credentials credentials = new Credentials(loginField.getText(), passwordField.getText());
        dataBase.setCredentials(credentials);

        try {
            storage.changeCredentials(dataBase);
        } catch (Exception e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
            return;
        }

        loginField.setText(credentials.getLogin());
        passwordField.setText(credentials.getPassword());

        MessageWindow successWindow = new MessageWindow("Success", "Credentials was successfully saved to database '" + dataBase.getName() + "'.");
        successWindow.getMessageArea().setStyle("-fx-text-fill: green;");
        successWindow.setWidth(400);
        successWindow.setHeight(100);
        successWindow.show();
    }

    private void refreshWindow() {
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

        loginField.setText(dataBase.getCredentials().getLogin());
        passwordField.setText(dataBase.getCredentials().getPassword());
    }

    private void removeDatabase() {
        try {
            storage.deleteDatabase(dataBase.getName());
        } catch (Exception e) {
            new MessageWindow(e.toString(), e.getMessage() + "\n" + e.toString()).show();
        }

        MessageWindow successWindow = new MessageWindow("Success", "Database '" + dataBase.getName() + "' was was successfully deleted.");
        successWindow.getMessageArea().setStyle("-fx-text-fill: green;");
        successWindow.setWidth(350);
        successWindow.setHeight(100);
        successWindow.show();
        successWindow.setOnCloseRequest(event -> this.close());
    }

    private void openTable() {
        Text tableName = tablesListView.getSelectionModel().getSelectedItem();

        try {
            Table table = dataBase.getTableByName(tableName.getText());
            File databaseFile = storage.getDatabaseFileByName(dataBase.getName());
            new TableWindow(table, databaseFile, storage).show();
        } catch (Exception e) {
            new MessageWindow(e.toString(), "No valid table selected.").show();
        }

        tablesListView.getSelectionModel().clearSelection();
    }

    private void createTable() {
        Table table = new Table("NewTable");

        try {
            File databaseFile = storage.getDatabaseFileByName(dataBase.getName());
            new TableWindow(table, databaseFile, storage).show();
        } catch (Exception e) {
            new MessageWindow(e.toString(), e.getMessage()).show();
        }
    }
}
