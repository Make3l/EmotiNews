package com.majkel.emotinews.ui.controller;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class RootController {

    @FXML
    private TabPane rootTabPane;

    private MainViewController mainViewController;
    private ChartController chartViewController;
    private FavouritesController favouritesController;


    public void setHostServices(HostServices hostServices){
        if(mainViewController!=null && hostServices!=null )
            mainViewController.setHostServices(hostServices);
    }

    @FXML
    public void initialize() {
        try {

            // Loading MainView
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/MainView.fxml"));
            Parent mainRoot = mainLoader.load();
            mainViewController = mainLoader.getController();

            // Loading ChartView
            FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/ChartView.fxml"));
            Parent chartRoot = chartLoader.load();
            chartViewController = chartLoader.getController();


            // Loading FavouritesView
            FXMLLoader favLoader=new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/FavouritesView.fxml"));
            Parent favRoot=favLoader.load();
            favouritesController=favLoader.getController();



            // Callback loading
            mainViewController.setCallbackConsumer(chartViewController::updateChart);

            mainViewController.setCallbackFavNews(e->{
                if(e.getAddDelete())
                    favouritesController.addFavourite(e.getNews());
                else
                    favouritesController.removeFavourite(e.getNews());
            });



            rootTabPane.getTabs().addAll(
                    new Tab("News", mainRoot),
                    new Tab("Chart", chartRoot),
                    new Tab("Favourites",favRoot)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}