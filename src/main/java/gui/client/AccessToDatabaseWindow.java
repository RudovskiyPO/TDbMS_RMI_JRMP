package gui.client;

import engine.core.Credentials;
import engine.core.DataBase;
import engine.exceptions.*;
import engine.service.Storage;
import engine.storageWorker.EntranceToStorage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

class AccessToDatabaseWindow extends Stage {
    /**
     * Attributes
     */
    private Scene scene;
    private Pane rootPanel;
    private Button okButton;
    private TextField loginField;
    private TextField passwordField;

    private DataBase dataBase;

    /**
     * Constructor
     */
    AccessToDatabaseWindow(String databaseName, Storage storage) throws IOException, SAXException, StorageException, CellException, TableException, ParserConfigurationException, ColumnException, NoteException, DataBaseException {
        // Set database
        dataBase = getDataBaseByName(databaseName, storage);

        // Set login field
        String loginFieldPlaceholder = "Enter database login here...";
        loginField = new TextField(loginFieldPlaceholder);
        loginField.setPrefSize(300, 20);
        loginField.setLayoutX(10);
        loginField.setLayoutY(20);
        loginField.setOnMouseClicked(event -> {
            if(loginField.getText().equals(loginFieldPlaceholder))
                loginField.setText("");
        });

        // Set password field
        String passwordFieldPlaceholder = "Enter database password here...";
        passwordField = new TextField(passwordFieldPlaceholder);
        passwordField.setPrefSize(300, 20);
        passwordField.setLayoutX(10);
        passwordField.setLayoutY(60);
        passwordField.setOnMouseClicked(event -> {
            if(passwordField.getText().equals(passwordFieldPlaceholder))
                passwordField.setText("");
        });

        // Set ok button
        okButton = new Button("OK");
        okButton.getStyleClass().add("ok");
        okButton.setPrefSize(90, 33);
        okButton.setLayoutX(200);
        okButton.setLayoutY(100);
        okButton.setOnAction(event -> {
            if(checkDatabaseCredentials()) {
                openDatabaseWindow(storage);

                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();
            }
            else new MessageWindow("Access denied", "Wrong database credentials data.").show();
        });

        // Set root panel
        rootPanel = new Pane();
        rootPanel.getChildren().addAll(loginField, passwordField, okButton);

        // Set scene
        scene = new Scene(rootPanel, 320, 150);
        scene.getStylesheets().add("/css/main.css");

        // Set stage
        setTitle("Access to " + databaseName);
        setScene(scene);
        setResizable(false);
        setX(500);
        setY(100);
    }

    /**
     * Methods
     */
    private DataBase getDataBaseByName(String databaseName, Storage storage) throws StorageException, TableException, NoteException, SAXException, CellException, DataBaseException, ParserConfigurationException, ColumnException, IOException {
        return EntranceToStorage.extractDataBaseFromFile(storage.getDatabaseFileByName(databaseName));
    }

    private boolean checkDatabaseCredentials() {
        Credentials credentials = dataBase.getCredentials();

        return loginField.getText().equals(credentials.getLogin()) && passwordField.getText().equals(credentials.getPassword());
    }

    private void openDatabaseWindow(Storage storage) {
        try {
            new DatabaseWindow(dataBase, storage).show();
        } catch (Exception e) {
            new MessageWindow(e.getMessage(), e.toString()).show();
        }
    }
}
