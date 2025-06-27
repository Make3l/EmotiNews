package com.majkel.emotinews;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.List;

public class NewsFetcher {
    private static final HttpClient httpClient=HttpClient.newHttpClient();

    public List<NewsArticle> getNewsList(){
        Gson gson=new Gson();
        List<NewsArticle> articles = null;
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://newsapi.org/v2/top-headlines?q=Bitcoin"))
                    .header("X-Api-Key", ConfigLoader.getValue("api.news.key"))
                    .build();
            HttpResponse<String> getResponse=httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if(getResponse.statusCode()!=200){
                System.out.println("ERROR: Response code is "+getResponse.statusCode());
                return List.of();
            }
            NewsHolder response=gson.fromJson(getResponse.body(),NewsHolder.class);
            articles=response.getArticles();
            System.out.println("Kod: "+response.getStatus());

        }catch(URISyntaxException e){
            System.out.println("ERROR: URISyntaxException "+e.getMessage());
        }catch (InterruptedException | IOException ie){
            System.out.println("Error: "+ie.getMessage());
        }
        return articles!=null? articles:List.of();
    }
}
