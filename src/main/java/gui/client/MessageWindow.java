package gui.client;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class MessageWindow extends Stage {
    /**
     * Attribute
     */
    private TextArea messageArea;

    /**
     * Constructor
     */
    MessageWindow(String title, String message) {
        if(title == null || title.equals("")) title = "Message";

        messageArea = new TextArea(message);
        messageArea.setPrefSize(600, 300);
        messageArea.setEditable(false);
        messageArea.setStyle("-fx-text-fill: red");

        // Set root panel
        Pane rootPanel = new Pane();
        rootPanel.getChildren().add(messageArea);

        // Set stage
        setTitle(title);
        setScene(new Scene(rootPanel, messageArea.getPrefWidth(), messageArea.getPrefHeight()));
        setResizable(false);
    }

    /**
     * Getter
     */
    TextArea getMessageArea() {
        return messageArea;
    }
}
