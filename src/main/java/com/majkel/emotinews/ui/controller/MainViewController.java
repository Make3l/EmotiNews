package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.Callback;
import com.majkel.emotinews.model.CallbackFav;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import javafx.animation.PauseTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

public class MainViewController {
    @FXML
    private ListView<NewsWithEmotions> listViewObj;

    private List<NewsWithEmotions> allNews;

    private HostServices hostServices;

    private NewsWithEmotions lastSelectedNews;

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

    private String currentTopic="technology";//current topic, default=technology

    private Consumer<Callback> callbackConsumer;

    private Consumer<CallbackFav> callbackFavNews;

    @FXML
    private void initialize(){
        allNews = NewsPipeline.loadNews();
        display(allNews);

        listViewObj.setCellFactory(param-> new ListCell<>(){
            private final Button favouriteButton=new Button("☆");
            private final Label titleLabel=new Label();
            private final HBox content=new HBox(favouriteButton,titleLabel);

            {
                titleLabel.getStyleClass().add("news-title");
                favouriteButton.getStyleClass().add("fav-button");
                favouriteButton.setVisible(false);
                content.getStyleClass().add("news-item");
                favouriteButton.setOnMouseClicked(e->{
                   NewsWithEmotions selected=getItem();
                   if(selected!=null){
                       selected.getArticle().changeFavourite();
                       callbackFavNews.accept(new CallbackFav(selected, selected.getArticle().isFavourite()));
                   }
                });

            }

            @Override
                protected void updateItem(NewsWithEmotions item,boolean empty){
                    super.updateItem(item, empty);
                    if(empty || item==null)
                    {
                        favouriteButton.textProperty().unbind();
                        setText(null);
                        setGraphic(null);
                    }
                    else
                    {
                        favouriteButton.textProperty().unbind();
                        favouriteButton.textProperty().bind(Bindings.when(item.getArticle().favouriteProperty())
                                .then("★").otherwise("☆"));
                        //setButtonIcon(item.getArticle().isFavourite());
                        titleLabel.setText(item.getArticle().getTitle());
                        setGraphic(content);
                        setOnMouseEntered(e->favouriteButton.setVisible(true));
                        setOnMouseExited(e->favouriteButton.setVisible(false));
                    }

            }
        });

        listViewObj.setOnMouseClicked(e->{
            NewsWithEmotions selected=listViewObj.getSelectionModel().getSelectedItem();
            listViewObj.getSelectionModel().clearSelection();
            if(selected!=null)
            {
                if(selected== lastSelectedNews){
                    title.setText("");
                    description.setText("");
                    detailedBox.setVisible(false);
                    detailedBox.setManaged(false);
                    lastSelectedNews =null;
                }
                else{
                    detailedBox.setVisible(true);
                    detailedBox.setManaged(true);
                    title.setText(selected.getArticle().getTitle());
                    description.setText(selected.getArticle().getDescription());
                    link.setOnAction(event->hostServices.showDocument(selected.getArticle().getUrl()));
                    lastSelectedNews =selected;
                }
            }
        });

        Platform.runLater(()->{
            callbackConsumer.accept(new Callback(currentTopic,allNews));
        });
    }


    private void display(List<NewsWithEmotions>news){
        ObservableList<NewsWithEmotions>items= FXCollections.observableArrayList();
        items.addAll(news);
        listViewObj.setItems(items);
    }

    public void setCallbackConsumer(Consumer<Callback> callback){
        this.callbackConsumer=callback;
    }

    public void setCallbackFavNews(Consumer<CallbackFav> callbackFavNews){
        this.callbackFavNews=callbackFavNews;
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
        currentTopic=topicField.getText();
        callbackConsumer.accept(new Callback(currentTopic,allNews));
        display(allNews);
        topicField.clear();
    }

    public void setHostServices(HostServices hostServices){
        this.hostServices=hostServices;
    }

}
