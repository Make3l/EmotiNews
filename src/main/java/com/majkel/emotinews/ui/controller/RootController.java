package com.majkel.emotinews.ui.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class RootController {

    @FXML
    private TabPane rootTabPane;

    private HostServices hostServices;

    private MainViewController mainViewController;
    private ChartController chartViewController;


    public void setHostServicies(HostServices hostServices){
        if(hostServices!=null )
            this.hostServices=hostServices;

    }

    @FXML
    public void initialize() {
        try {

            // Loading MainView
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/MainView.fxml"));
            Parent mainRoot = mainLoader.load();
            mainViewController = mainLoader.getController();
            if(mainViewController!=null)
                mainViewController.setHostServices(hostServices);

            // Loading ChartView
            FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/ChartView.fxml"));
            Parent chartRoot = chartLoader.load();
            chartViewController = chartLoader.getController();

            // Callback loading
            mainViewController.setCallbackConsumer(chartViewController::updateChart);

            rootTabPane.getTabs().addAll(
                    new Tab("News", mainRoot),
                    new Tab("Chart", chartRoot)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}