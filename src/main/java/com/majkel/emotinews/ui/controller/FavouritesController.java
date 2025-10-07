package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.model.DetailedBoxComponent;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.storage.JSONStorage;
import com.majkel.emotinews.utils.InputSanitizer;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FavouritesController {

    private final String storageFilePath= ConfigLoader.getValue("news.storage.path");
    private List<NewsWithEmotions>favAllList;
    private ObservableList<NewsWithEmotions>observableList;

    private HostServices hostServices;

    private NewsWithEmotions lastSelectedNews;

    private Consumer<List<NewsWithEmotions>> callbackFavList;

    @FXML
    private ListView<NewsWithEmotions> favList;

    private DetailedBoxComponent detailedBox;

    @FXML
    private Button searchButton;

    @FXML
    private TextField phraseField;

    @FXML
    private SplitPane rootSplit;

    @FXML
    public void initialize(){
        detailedBox=new DetailedBoxComponent();
        detailedBox.managedProperty().bind(detailedBox.visibleProperty());
        detailedBox.getDescription().wrappingWidthProperty().bind(detailedBox.widthProperty().subtract(20));

        favAllList=JSONStorage.safeLoad(new File(storageFilePath));

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
                            detailedBox.getDescription().setText("");
                            hideDetailedBox();
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
                    detailedBox.getTitle().setText("");
                    detailedBox.getDescription().setText("");
                    hideDetailedBox();
                    lastSelectedNews=null;
                }else{
                    showDetailedBox();
                    detailedBox.getTitle().setText(selected.getArticle().getTitle());
                    detailedBox.getDescription().setText(selected.getArticle().getDescription());
                    if(selected.getArticle().getUrl()!=null && !selected.getArticle().getUrl().isBlank())
                        detailedBox.getLink().setOnAction(ns->{hostServices.showDocument(selected.getArticle().getUrl());});
                    else
                        detailedBox.getLink().setVisible(false);

                    lastSelectedNews=selected;
                }
            }

        });

    }

    private void showDetailedBox(){
        if(detailedBox.isVisible())
            return;

        if (rootSplit != null && !rootSplit.getItems().contains(detailedBox))
            rootSplit.getItems().add(detailedBox);

        detailedBox.setVisible(true);
    }

    private void hideDetailedBox(){
        if(!detailedBox.isVisible())
            return;

        if (rootSplit != null)
            rootSplit.getItems().remove(detailedBox);


        detailedBox.setVisible(false);
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

    public void setCallbackFavList(Consumer<List<NewsWithEmotions>>callbackFavList){
        this.callbackFavList=callbackFavList;
    }

    @FXML
    private void searchFavourites(){
        String searchedPhase= InputSanitizer.filterTopic(phraseField.getText().toLowerCase());
        if(searchedPhase.isEmpty()){
            display(favAllList);
            return;
        }
        display(favAllList.stream().filter(e-> e.getArticle().getTitle().toLowerCase().contains(searchedPhase)).collect(Collectors.toList()));
    }
}
