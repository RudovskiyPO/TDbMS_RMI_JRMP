package gui.server;

import engine.service.Storage;
import engine.service.StorageImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerMainWindow extends Application {
    /**
     * Attributes
     */
    private Pane rootPanel;
    private ListView<String> logsListView;
    private Button startServer;
    private Button stopServer;

    private ArrayList<String> logsList;

    private StorageImpl storage;
    private Registry registry;
    /**
     * Constructor
     */
    public ServerMainWindow() {
        logsList = new ArrayList<>();

        // Set gui.server start button
        startServer = new Button("Start");
        startServer.getStyleClass().add("ok");
        startServer.setPrefSize(90, 33);
        startServer.setLayoutX(10);
        startServer.setLayoutY(10);
        startServer.setOnAction(event -> {
            try {
                storage = new StorageImpl();
                registry.bind(Storage.BINDING_NAME, storage);
            } catch (RemoteException | AlreadyBoundException e) {
                addLog(e.toString());
                return;
            }
            addLog("Server started");
        });

        // Set gui.server start button
        stopServer = new Button("Stop");
        stopServer.getStyleClass().add("exit_button");
        stopServer.setPrefSize(90, 33);
        stopServer.setLayoutX(startServer.getLayoutX() + 150);
        stopServer.setLayoutY(startServer.getLayoutY());
        stopServer.setOnAction(event -> {
            try {
                registry.unbind(Storage.BINDING_NAME);
                UnicastRemoteObject.unexportObject(storage, true);
            } catch (RemoteException | NotBoundException e) {
                addLog(e.toString());
                return;
            }
            addLog("Server stopped");
        });

        // Set logs list view
        logsListView = new ListView<>();
        logsListView.setPrefSize(250, 300);
        logsListView.setLayoutX(startServer.getLayoutX() - 5);
        logsListView.setLayoutY(startServer.getLayoutY() + startServer.getPrefHeight() + 15);

        // Set root panel
        rootPanel = new Pane();
        rootPanel.setPrefSize(logsListView.getPrefWidth(), logsListView.getPrefHeight());

        // Register service
        try {
            storage = new StorageImpl();
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            registry.bind(Storage.BINDING_NAME, storage);

            addLog("Start application...");
            addLog("Server started");
        } catch (RemoteException | AlreadyBoundException e) {
            addLog(e.toString());
        }
    }

    /**
     * Methods
     */
    private void addLog(String log) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");
        Date date = new Date();
        logsList.add("[" + dateFormat.format(date) + "] " + log);
        refreshLogsListView();
    }

    private void refreshLogsListView() {
        logsListView.getItems().removeAll(logsListView.getItems());
        for (String log : logsList)
            logsListView.getItems().add(log);
        logsListView.refresh();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Add elements to root panel
        rootPanel.getChildren().addAll(logsListView, startServer, stopServer);

        // Set scene
        Scene scene = new Scene(rootPanel, logsListView.getPrefWidth(), logsListView.getPrefHeight() + 55);
        scene.getStylesheets().add("/css/main.css");

        // Set primary stage params
        primaryStage.setTitle("Database Management System: Server");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setX(100);
        primaryStage.setY(200);
        primaryStage.setOnCloseRequest(event -> {
            addLog("Shutdown application...");
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
