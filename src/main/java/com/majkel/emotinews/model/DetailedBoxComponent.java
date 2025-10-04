package com.majkel.emotinews.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class DetailedBoxComponent extends VBox {

    @FXML private Label title;
    @FXML private Text description;
    @FXML private Hyperlink link;

    public DetailedBoxComponent() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/DetailedBox.fxml"));
        loader.setController(this);

        try {
            VBox root = loader.load();
            this.getChildren().setAll(root.getChildren());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Label getTitle() {
        return title;
    }

    public Text getDescription() {
        return description;
    }

    public Hyperlink getLink() {
        return link;
    }
}