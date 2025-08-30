package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsWithEmotions;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RootController {

    @FXML
    private TabPane rootTabPane;

    private MainViewController mainViewController;
    private ChartController chartViewController;
    private FavouritesController favouritesController;


    public void setHostServices(HostServices hostServices){
        if(mainViewController!=null && favouritesController!=null && hostServices!=null )
        {
            mainViewController.setHostServices(hostServices);
            favouritesController.setHostServices(hostServices);
        }
    }

    public List<NewsWithEmotions> getFavouritesList(){
        if(favouritesController!=null)
            return favouritesController.getFavouritesList();
        return new ArrayList<>();
    }

    public String getStorageFilePath(){
        if(favouritesController!=null)
            return favouritesController.getStorageFilePath();
        return "";
    }


    @FXML
    public void initialize() {
        try {

            // Loading FavouritesView
            FXMLLoader favLoader=new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/FavouritesView.fxml"));
            Parent favRoot=favLoader.load();
            favouritesController=favLoader.getController();

            // Loading MainView
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/MainView.fxml"));
            Parent mainRoot = mainLoader.load();
            mainViewController = mainLoader.getController();

            // Loading ChartView
            FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/com/majkel/emotinews/ui/view/ChartView.fxml"));
            Parent chartRoot = chartLoader.load();
            chartViewController = chartLoader.getController();



            // Callback loading
            mainViewController.setCallbackConsumer(chartViewController::updateChart);

            mainViewController.setCallbackFavNews(e->{
                if(e.getAddDelete())
                    favouritesController.addFavourite(e.getNews());
                else
                    favouritesController.removeFavourite(e.getNews());
            });

            favouritesController.setCallbackFavList(mainViewController::setFavourites);



            Tab tabMain=new Tab("News", mainRoot);
            Tab tabChart=new Tab("Chart", chartRoot);
            Tab tabFavourites=new Tab("Favourites",favRoot);

            tabMain.setClosable(false);
            tabChart.setClosable(false);
            tabFavourites.setClosable(false);

            rootTabPane.getTabs().addAll(tabMain,tabChart,tabFavourites);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}