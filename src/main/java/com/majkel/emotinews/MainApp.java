package com.majkel.emotinews;

import java.util.List;

public class MainApp {
    public static void main(String []args){
        NewsFetcher newsFetcher=new NewsFetcher();
        List<NewsArticle>articles = newsFetcher.getNewsList();
        for(NewsArticle article: articles){
            System.out.println(article+" ");
        }

    }
}
