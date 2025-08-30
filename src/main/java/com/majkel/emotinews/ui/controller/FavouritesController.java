package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.storage.JSONStorage;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FavouritesController {

    private final String storageFilePath="src/main/resources/news.json";
    private List<NewsWithEmotions>favAllList;
    private ObservableList<NewsWithEmotions>observableList;

    private HostServices hostServices;

    private NewsWithEmotions lastSelectedNews;

    private Consumer<List<NewsWithEmotions>> callbackFavList;

    @FXML
    private ListView<NewsWithEmotions> favList;

    @FXML
    private VBox detailedBox;

    @FXML
    private Label newsTitle;

    @FXML
    private Text newsDescription;

    @FXML
    private Hyperlink newsLink;

    @FXML
    public void initialize(){
        try{
            favAllList= JSONStorage.load(new File(storageFilePath));
        } catch (IOException e) {
            System.out.println("ERROR: IOEXCEPTION in file (file path): "+storageFilePath);
        }

        Platform.runLater(()->{callbackFavList.accept(favAllList);});

        observableList=FXCollections.observableArrayList(favAllList);
        favList.setItems(observableList);

        favList.setCellFactory(lv->new ListCell<NewsWithEmotions>(){
            private Button deleteFavButton=new Button("★");
            private Label newsTitle=new Label();
            private HBox hBox=new HBox(deleteFavButton,newsTitle);
            {
                newsTitle.getStyleClass().add("news-title");
                deleteFavButton.getStyleClass().add("fav-button");
                deleteFavButton.setVisible(false);
                hBox.getStyleClass().add("news-item");
                deleteFavButton.setOnMouseEntered(e->{deleteFavButton.setText("☆");});
                deleteFavButton.setOnMouseExited(e->{deleteFavButton.setText("★");});
                deleteFavButton.setOnMouseClicked(e->{
                    NewsWithEmotions selected=getItem();
                    if(selected!=null)
                    {
                        removeFavourite(selected);
                        Platform.runLater(()->selected.getArticle().changeFavourite());
                        if(selected.equals(lastSelectedNews)) {
                            newsTitle.setText("");
                            newsDescription.setText("");
                            detailedBox.setManaged(false);
                            detailedBox.setVisible(false);
                            lastSelectedNews = null;
                        }
                    }
                });
            }


            @Override
            protected void updateItem(NewsWithEmotions item, boolean empty){
                super.updateItem(item,empty);
                if(empty | item==null){
                    setText(null);
                    setGraphic(null);
                }else{
                    newsTitle.setText(item.getArticle().getTitle());
                    setGraphic(hBox);
                    setOnMouseEntered(e->deleteFavButton.setVisible(true));
                    setOnMouseExited(e->deleteFavButton.setVisible(false));
                }
            }

        });

        favList.setOnMouseClicked(e->{
            NewsWithEmotions selected=favList.getSelectionModel().getSelectedItem();
            favList.getSelectionModel().clearSelection();
            if(selected!=null)
            {
                if(selected.equals(lastSelectedNews)){
                    newsTitle.setText("");
                    newsDescription.setText("");
                    detailedBox.setManaged(false);
                    detailedBox.setVisible(false);
                    lastSelectedNews=null;
                }else{
                    newsTitle.setText(selected.getArticle().getTitle());
                    newsDescription.setText(selected.getArticle().getDescription());
                    newsLink.setOnAction(ns->{hostServices.showDocument(selected.getArticle().getUrl());});
                    detailedBox.setManaged(true);
                    detailedBox.setVisible(true);
                    lastSelectedNews=selected;
                }
            }

        });

    }

    public void addFavourite(NewsWithEmotions news){
        favAllList.add(news);
        observableList.add(news);
    }
    public void removeFavourite(NewsWithEmotions news){
        favAllList.remove(news);
        observableList.remove(news);
    }

    private void display(List<NewsWithEmotions>newsToDisplay){
        observableList.setAll(newsToDisplay);
    }

    @FXML
    private void handleAll(){
        display(favAllList);
    }
    @FXML
    private void handlePositive(){
        display(favAllList.stream().filter(e->e.getEmotion().equals("LABEL_2")).toList());
    }
    @FXML
    private void handleNegative(){
        display(favAllList.stream().filter(e->e.getEmotion().equals("LABEL_0")).toList());
    }
    @FXML
    private void handleNeutral() {
        display(favAllList.stream().filter(e->e.getEmotion().equals("LABEL_1")).toList());
    }

    public void setHostServices(HostServices hostServices){
        this.hostServices=hostServices;
    }

    public List<NewsWithEmotions> getFavouritesList(){
        if(favAllList!=null)
            return favAllList;
        return new ArrayList<>();
    }

    public String getStorageFilePath() {
        return storageFilePath;
    }

    public void setCallbackFavList(Consumer<List<NewsWithEmotions>>callbackFavList){
        this.callbackFavList=callbackFavList;
    }
}
