package com.majkel.emotinews;

import java.util.Iterator;
import java.util.List;

public class NewsHolder implements Iterable<NewsArticle>{
    private List<NewsArticle> articles;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
    }
    @Override
    public Iterator<NewsArticle> iterator(){
        return articles.iterator();
    }

}
