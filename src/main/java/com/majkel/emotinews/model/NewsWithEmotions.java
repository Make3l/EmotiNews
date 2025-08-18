package com.majkel.emotinews.model;

import java.util.Objects;

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

    public String changedEmotionName(){
        switch (this.emotion){
            case "LABEL_0" -> {
                return "Negative";
            }
            case "LABEL_1"->{
                return "Neutral";
            }
            case "LABEL_2"->{
                return "Positive";
            }
        }
        return "ERROR";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsWithEmotions that)) return false;
        return article.equals(that.getArticle()) && emotion.equals(that.getEmotion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(article,emotion);
    }

}