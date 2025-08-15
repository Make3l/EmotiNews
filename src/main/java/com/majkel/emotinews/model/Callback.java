package com.majkel.emotinews.model;

import java.util.List;

public class Callback {
    private String topic;
    private List<NewsWithEmotions> news;

    public Callback(){}

    public Callback(String topic, List<NewsWithEmotions> news){
        this.topic=topic;
        this.news=news;
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<NewsWithEmotions> getNews() {
        return news;
    }

    public void setNews(List<NewsWithEmotions> news) {
        this.news = news;
    }
}
