package com.majkel.emotinews.ui.fxapp;

import com.majkel.emotinews.ui.controller.MainViewController;
import com.majkel.emotinews.ui.controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FXMainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/RootView.fxml"));
        Scene scene = new Scene(loader.load());

        RootController rootController=loader.getController();
        rootController.setHostServicies(getHostServices());

        primaryStage.setTitle("EmotiNews");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
