package com.majkel.emotinews.model;

public class NewsWithEmotions {
    private final String emotion;
    private final NewsArticle article;
    public NewsWithEmotions( String emotion, NewsArticle article){
        this.emotion=emotion;
        this.article=article;
    }
    public String getEmotion(){
        return emotion;
    }
    public NewsArticle getArticle(){
        return article;
    }
}