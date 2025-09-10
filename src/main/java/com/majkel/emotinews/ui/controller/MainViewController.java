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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
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

    private List<NewsWithEmotions> favourites=null;

    private Task<List<NewsWithEmotions>> newsPipelineTask;


    @FXML
    private void initialize(){
        allNews=new ArrayList<>();
        allNews.add(new NewsWithEmotions("LABEL_1", NewsArticle.createDefaultNews()));
        Platform.runLater(()->display(allNews));

        Task<List<NewsWithEmotions>>task=new Task<>() {
            @Override
            protected List<NewsWithEmotions> call() throws Exception{
                return NewsPipeline.loadNews();
            }
        };
        task.setOnSucceeded(e->{
            allNews=task.getValue();
            Platform.runLater(()->callbackConsumer.accept(new Callback(currentTopic,allNews)));
            syncFavouritesWithAllNews();
            display(allNews);
        });
        task.setOnFailed(e->{
            Throwable ex = task.getException();
            System.err.println("Failed at newsPipelineTask: " + ex.getMessage());
            ex.printStackTrace();
        });
        new Thread(task).start();




        listViewObj.setCellFactory(param-> new ListCell<>(){
            private final Button favouriteButton=new Button();
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
                if(selected.equals(lastSelectedNews)){
                    title.setText("");
                    description.setText("");
                    detailedBox.setVisible(false);
                    detailedBox.setManaged(false);
                    lastSelectedNews = null;
                }
                else{
                    detailedBox.setVisible(true);
                    detailedBox.setManaged(true);
                    title.setText(selected.getArticle().getTitle());
                    description.setText(selected.getArticle().getDescription());
                    String currentURL=selected.getArticle().getUrl();
                    if(currentURL!=null && !currentURL.isEmpty())
                        link.setOnAction(event->hostServices.showDocument(currentURL));
                    else
                        link.setVisible(false);

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

        if(newsPipelineTask!=null && newsPipelineTask.isRunning()){
            System.out.println("The search has already begun");
            return;
        }

        searchButton.setDisable(true);
        searchButton.getStyleClass().removeAll("search-button","cooldown-button");
        searchButton.getStyleClass().add("cooldown-button");
        PauseTransition pauseTransition=new PauseTransition(Duration.seconds(15));
        pauseTransition.setOnFinished(e->{
            searchButton.setDisable(false);
            searchButton.getStyleClass().removeAll("search-button","cooldown-button");
            searchButton.getStyleClass().add("search-button");
        });
        pauseTransition.play();

        newsPipelineTask=new Task<>() {
            @Override
            protected List<NewsWithEmotions> call() throws Exception{
                return NewsPipeline.loadNews(topicField.getText());
            }
        };
        newsPipelineTask.setOnSucceeded(e->{
            allNews=newsPipelineTask.getValue();
            currentTopic=topicField.getText();
            callbackConsumer.accept(new Callback(currentTopic,allNews));
            syncFavouritesWithAllNews();
            display(allNews);
            topicField.clear();
            newsPipelineTask=null;
        });
        newsPipelineTask.setOnFailed(e->{
            Throwable ex = newsPipelineTask.getException();
            System.err.println("Failed at newsPipelineTask: " + ex.getMessage());
            ex.printStackTrace();
            newsPipelineTask=null;
        });
        new Thread(newsPipelineTask).start();
    }

    public void setHostServices(HostServices hostServices){
        this.hostServices=hostServices;
    }

    private void syncFavouritesWithAllNews(){//it swaps objects in order to connect swapped news with this in favourites
        if(favourites==null)
            return;
        Map<NewsWithEmotions,Integer> map=new HashMap<>();
        for(int i=0;i<allNews.size();i++)
            map.put(allNews.get(i),i);

        for(NewsWithEmotions news: favourites){
            Integer index=map.get(news);
            if(index!=null)
                allNews.set(index,news);
        }
    }

    public void setFavourites(List<NewsWithEmotions>favourites){
        this.favourites=favourites;
    }

}
