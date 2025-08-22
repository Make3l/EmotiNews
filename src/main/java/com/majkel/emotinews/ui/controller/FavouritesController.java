package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.storage.JSONStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FavouritesController {

    private final String storageFilePath="src/main/resources/news.json";
    private List<NewsWithEmotions>favAllList;
    private ObservableList<NewsWithEmotions>observableList;

    @FXML
    private ListView<NewsWithEmotions> favList;

    @FXML
    public void initialize(){
        try{
            favAllList= JSONStorage.load(new File(storageFilePath));
        } catch (IOException e) {
            System.out.println("ERROR: IOEXCEPTION in file (file path): "+storageFilePath);
        }
        observableList=FXCollections.observableArrayList(favAllList);
        favList.setItems(observableList);

        favList.setCellFactory(lv->new ListCell<NewsWithEmotions>(){
            @Override
            protected void updateItem(NewsWithEmotions item, boolean empty){
                super.updateItem(item,empty);
                if(empty | item==null){
                    setText(null);
                    setGraphic(null);
                }else{
                    setText(item.getArticle().getTitle());
                }
            }
        });

    }

    public void addFavourite(NewsWithEmotions news){
        favAllList.add(news);
        observableList.add(news);
        System.out.println("Dodano!!!");
    }
    public void removeFavourite(NewsWithEmotions news){
        favAllList.remove(news);
        observableList.remove(news);
        System.out.println("Zabrano!!!");
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


}
