package com.majkel.emotinews.model;

import com.google.gson.annotations.Expose;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

public class NewsArticle{
    private String author;
    private String title;
    private String description;
    private String url;
    private String publishedAt;
    private String content;

    private boolean fav=false;

    private BooleanProperty favourite=new SimpleBooleanProperty(fav);

    public NewsArticle(){}

    public NewsArticle(String title, String description){
        this.title=title;
        this.description=description;
    }

    public static NewsArticle createFallBackNews(String errorMsg){
        return new NewsArticle(
                "Error: "+errorMsg,
                "You are seeing this message because the app could not connect to the News API. Please check your internet connection and try again."
        );
    }

    public static NewsArticle createAnalyzingNewsFallBackNews(String errorMsg){
        return new NewsArticle(
                "Error: "+errorMsg,
                "The app could not process the news sentiment using the HuggingFace model. Sometimes it could be overloaded, don't worry and please try again later."
        );
    }


    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isFavourite(){
        return favourite.get();
    }

    public void changeFavourite(){
        favourite.setValue(!favourite.get());
        fav=!fav;
    }

    public BooleanProperty favouriteProperty() {
        return favourite;
    }

    @Override
    public String toString(){
        StringBuilder str=new StringBuilder();
        str.append(author).append(" ").append(title).append(" ").append(description);
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsArticle that)) return false;
        return title.equals(that.getTitle()) && description.equals(that.getDescription()) && url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, url);
    }



}
