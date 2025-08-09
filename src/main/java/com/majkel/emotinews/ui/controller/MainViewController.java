package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import javafx.animation.PauseTransition;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

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
    private Text description;

    @FXML
    private VBox detailedBox;

    @FXML
    private Button searchButton;


    private Consumer<List<NewsWithEmotions>> chartArticles;

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
                    description.setText("");
                    detailedBox.setVisible(false);
                    detailedBox.setManaged(false);
                    lastSelectedArticle=null;
                }
                else{
                    detailedBox.setVisible(true);
                    detailedBox.setManaged(true);
                    title.setText(selected.getTitle());
                    description.setText(selected.getDescription());
                    link.setOnAction(event->hostServices.showDocument(selected.getUrl()));
                    lastSelectedArticle=selected;
                }
            }
        });
        //chartArticles.accept(allNews);
    }


    private void display(List<NewsWithEmotions>news){
        ObservableList<NewsArticle>items= FXCollections.observableArrayList();
        for(NewsWithEmotions n: news){
            items.add(n.getArticle());
        }
        listViewObj.setItems(items);
    }

    public void setChartArticles(Consumer<List<NewsWithEmotions>> articles){
        this.chartArticles=articles;
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

        searchButton.setDisable(true);
        searchButton.getStyleClass().removeAll("search-button","cooldown-button");
        searchButton.getStyleClass().add("cooldown-button");
        PauseTransition pauseTransition=new PauseTransition(Duration.seconds(4));
        pauseTransition.setOnFinished(e->{
            searchButton.setDisable(false);
            searchButton.getStyleClass().removeAll("search-button","cooldown-button");
            searchButton.getStyleClass().add("search-button");
        });
        pauseTransition.play();

        allNews=NewsPipeline.loadNews(topicField.getText());
        chartArticles.accept(allNews);
        display(allNews);
        topicField.clear();
    }

    public void setHostServices(HostServices hostServices){
        this.hostServices=hostServices;
    }

}
