package gui.client;

import engine.service.Storage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ClientMainWindow extends Application {

    /**
     * Attributes
     */
    private Pane rootPanel;
    private Text textDatabases;
    private Button refreshButton;
    private ListView<Text> databasesListView;
    private Button openDatabase;
    private Button createDatabase;
    private Button exitButton;

    private Registry registry;
    private Storage storage;

    /**
     * Constructor
     */
    public ClientMainWindow() {
        try {
            registry = LocateRegistry.getRegistry("localhost");
            storage = (Storage) registry.lookup(Storage.BINDING_NAME);
        } catch (RemoteException | NotBoundException e) {
            new MessageWindow(e.getMessage(), e.toString()).show();
        }

        // Initialize window elements
        initializeRootPanel();
        initializeTextDatabases();
        initializeRefreshButton();
        initializeDatabasesListView();
        initializeOpenDatabase();
        initializeCreateDatabase();
        initializeExitButton();
    }

    /**
     * Initialize window elements
     */
    private void initializeRootPanel() {
        rootPanel = new Pane();
        rootPanel.setPrefSize(352, 360);
        rootPanel.getStyleClass().add("main_window");
    }

    private void initializeTextDatabases() {
        textDatabases = new Text("Databases:");
        textDatabases.getStyleClass().add("header_of_list");
        textDatabases.setLayoutX(20);
        textDatabases.setLayoutY(20);
    }

    private void initializeRefreshButton() {
        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("refresh");
        refreshButton.setPrefSize(60, 25);
        refreshButton.setLayoutX(280);
        refreshButton.setLayoutY(2);
        refreshButton.setFocusTraversable(false);

        refreshButton.setOnAction(event -> {
            ArrayList<String> databases;
            try {
                storage = (Storage) registry.lookup(Storage.BINDING_NAME);
                databases = storage.getDatabasesList();
            } catch (RemoteException | NotBoundException e) {
                new MessageWindow(e.getMessage(), e.toString()).show();
                return;
            }

            databasesListView.getItems().removeAll(databasesListView.getItems());
            for (String database : databases) {
                Text text = new Text(database);
                text.getStyleClass().add("list_item");

                databasesListView.getItems().add(text);
            }
            databasesListView.refresh();
        });
    }

    private void initializeDatabasesListView() {
        databasesListView = new ListView<>();
        databasesListView.setLayoutX(20);
        databasesListView.setLayoutY(25);
        databasesListView.setPrefSize(320, 280);

        ArrayList<String> databases;
        try {
            databases = storage.getDatabasesList();
        } catch (RemoteException e) {
            new MessageWindow(e.getMessage(), e.toString()).show();
            return;
        }
        for (String database : databases) {
            Text text = new Text(database);
            text.getStyleClass().add("list_item");

            databasesListView.getItems().add(text);
        }
    }

    private void initializeOpenDatabase() {
        openDatabase = new Button("Open");
        openDatabase.getStyleClass().add("other_button");
        openDatabase.setPrefSize(90, 33);
        openDatabase.setLayoutX(30);
        openDatabase.setLayoutY(320);
        openDatabase.setFocusTraversable(false);

        openDatabase.setOnAction(event -> {
            Text database = databasesListView.getSelectionModel().getSelectedItem();

            try {
                new AccessToDatabaseWindow(database.getText(), storage).show();
            } catch (Exception e) {
                new MessageWindow(e.toString(), "No valid database selected").show();
            }

            databasesListView.getSelectionModel().clearSelection();
        });
    }

    private void initializeCreateDatabase() {
        createDatabase = new Button("Create");
        createDatabase.getStyleClass().add("other_button");
        createDatabase.setPrefSize(90, 33);
        createDatabase.setLayoutX(135);
        createDatabase.setLayoutY(320);
        createDatabase.setFocusTraversable(false);

        createDatabase.setOnAction(event -> new CreateDatabaseWindow(storage).show());
    }

    private void initializeExitButton() {
        exitButton = new Button("Exit");
        exitButton.getStyleClass().add("exit_button");
        exitButton.setPrefSize(90, 33);
        exitButton.setLayoutX(240);
        exitButton.setLayoutY(320);
        exitButton.setFocusTraversable(false);

        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Add elements to root panel
        rootPanel.getChildren().addAll(textDatabases, refreshButton, databasesListView, openDatabase, createDatabase, exitButton);

        // Set scene
        Scene scene = new Scene(rootPanel, primaryStage.getWidth(), primaryStage.getHeight());
        scene.getStylesheets().add("/css/main.css");

        // Set primary stage params
        primaryStage.setTitle("Database Management System: Client");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Entrance point
     */
    public static void main(String[] args) {
        launch(args);
    }
}
