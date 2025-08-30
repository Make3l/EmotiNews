package com.majkel.emotinews.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.majkel.emotinews.adapter.BooleanPropertyAdapter;
import com.majkel.emotinews.model.NewsHolder;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.utils.CollectionUtils;
import javafx.beans.property.BooleanProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NewsFetcher {
    private static final HttpClient httpClient=HttpClient.newHttpClient();

    public List<NewsArticle> getNewsList(String query){
        Gson gson= new GsonBuilder().registerTypeAdapter(BooleanProperty.class,new  BooleanPropertyAdapter()).create();
        List<NewsArticle> articles = null;
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://newsapi.org/v2/"+query+"language=en"))
                    .header("X-Api-Key", ConfigLoader.getValue("api.news.key"))
                    .build();
            HttpResponse<String> getResponse=httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if(getResponse.statusCode()!=200)
                throw new NewsApiException("Received status code "+getResponse.statusCode());

            NewsHolder response=gson.fromJson(getResponse.body(),NewsHolder.class);
            articles=response.getArticles();
            System.out.println("Kod: "+response.getStatus());

        }catch(URISyntaxException e){
            throw new RuntimeException("URISyntaxException ",e);
        }catch (InterruptedException | IOException e){
            throw new NewsApiException("Error while calling NewsAPI",e);
        }
        if(articles!=null)
            articles= CollectionUtils.filterValidNews(articles);
        return articles!=null? articles:List.of();
    }
}
