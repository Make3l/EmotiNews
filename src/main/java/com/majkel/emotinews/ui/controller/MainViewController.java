package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class MainViewController {
    @FXML
    private ListView<String> listViewObj;

    private List<NewsWithEmotions> allNews;

    @FXML
    private TextField topicField;

    @FXML
    private void initialize(){
        allNews = NewsPipeline.loadNews();
        display(allNews);
    }


    private void display(List<NewsWithEmotions>news){
        ObservableList<String>items= FXCollections.observableArrayList();
        for(NewsWithEmotions n: news){
            items.add(n.getArticle().getTitle());
        }
        listViewObj.setItems(items);
    }

    @FXML
    public void handleAll(){
        display(allNews);
    }

    @FXML
    public void handlePositive(){
        display(allNews.stream().filter(n->n.getEmotion().equals("LABEL_2")).toList());
    }
    @FXML
    public void handleNegative(){
        display(allNews.stream().filter(n->n.getEmotion().equals("LABEL_0")).toList());
    }
    @FXML
    public void handleNeutral(){
        display(allNews.stream().filter(n->n.getEmotion().equals("LABEL_1")).toList());
    }
    @FXML
    public void searchTopic(){
        if(topicField.getText().isEmpty())
            return;
        allNews=NewsPipeline.loadNews(topicField.getText());
        display(allNews);
        topicField.clear();
    }
}
