package com.majkel.emotinews;

import com.google.gson.Gson;
import com.majkel.emotinews.exception.NewsApiException;

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
        Gson gson=new Gson();
        List<NewsArticle> articles = null;
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://newsapi.org/v2/"+query))
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
        return articles!=null? articles:List.of();
    }
}
