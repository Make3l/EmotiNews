package com.majkel.emotinews.ui.fxapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMainApp extends Application{
    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        Parent root=loader.load();
        Scene scene=new Scene(root);

        stage.setTitle("EmotiNews");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
