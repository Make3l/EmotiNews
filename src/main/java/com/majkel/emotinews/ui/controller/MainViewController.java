package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class MainViewController {
    @FXML
    private ListView<NewsArticle> listViewObj;

    private List<NewsWithEmotions> allNews;

    private HostServices hostServices;

    private NewsArticle lastSelectedArticle;

    @FXML
    private TextField topicField;

    @FXML
    private Hyperlink link;

    @FXML
    private Label title;

    @FXML
    private TextArea descryption;

    @FXML
    private VBox detailedBox;


    @FXML
    private void initialize(){
        allNews = NewsPipeline.loadNews();
        display(allNews);

        listViewObj.setCellFactory(param-> new ListCell<>(){
                @Override
                protected void updateItem(NewsArticle item,boolean empty){
                    super.updateItem(item, empty);
                    if(empty || item==null)
                        setText(null);
                    else
                        setText(item.getTitle());
            }
        });

        listViewObj.setOnMouseClicked(e->{
            NewsArticle selected=listViewObj.getSelectionModel().getSelectedItem();
            if(selected!=null)
            {
                if(selected==lastSelectedArticle){
                    title.setText("");
                    descryption.setText("");
                    detailedBox.setVisible(false);
                    detailedBox.setManaged(false);
                    lastSelectedArticle=null;
                }
                else{
                    detailedBox.setVisible(true);
                    detailedBox.setManaged(true);
                    title.setText(selected.getTitle());
                    descryption.setText(selected.getDescription());
                    link.setOnAction(event->hostServices.showDocument(selected.getUrl()));
                    lastSelectedArticle=selected;
                }
            }
        });
    }


    private void display(List<NewsWithEmotions>news){
        ObservableList<NewsArticle>items= FXCollections.observableArrayList();
        for(NewsWithEmotions n: news){
            items.add(n.getArticle());
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

    public void setHostServices(HostServices hostServices){
        this.hostServices=hostServices;
    }

}
