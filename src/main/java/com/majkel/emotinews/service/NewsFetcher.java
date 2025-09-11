package com.majkel.emotinews.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
import java.util.ArrayList;
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

            if (getResponse.statusCode() == 401 || getResponse.statusCode() == 403) {
                throw new NewsApiException("Invalid or missing API key");
            } else if(getResponse.statusCode()!=200)
                throw new NewsApiException("NewsAPI request failed with status code "+getResponse.statusCode());

            NewsHolder response=gson.fromJson(getResponse.body(),NewsHolder.class);
            articles=response.getArticles();

        } catch(URISyntaxException e){
            throw new NewsApiException("Invalid API URL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NewsApiException("Thread was interrupted while calling NewsAPI", e);
        } catch(JsonSyntaxException e){
            throw new NewsApiException("Invalid JSON received from NewsApi",e);
        } catch (IOException e) {
            throw new NewsApiException("I/O error while calling NewsAPI", e);
        }

        if(articles!=null && !articles.isEmpty())
            articles= CollectionUtils.filterValidNews(articles);
        return articles!=null?articles: new ArrayList<>();
    }
}
