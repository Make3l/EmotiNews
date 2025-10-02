package com.majkel.emotinews.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.majkel.emotinews.adapter.BooleanPropertyAdapter;
import com.majkel.emotinews.model.NewsHolder;
import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.utils.CollectionUtils;
import javafx.beans.property.BooleanProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewsFetcher {

    private final HttpClientPort httpClient;
    private final String apiKey;

    public NewsFetcher(HttpClientPort httpClient, String apiKey){
        this.httpClient=httpClient;
        this.apiKey=apiKey;
    }

    private static String encode(String value){
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+","%20");
    }

    private static String buildQuery(Map<String,String> params){
        return params.entrySet().stream()
                .filter(e->e.getKey()!=null && e.getValue()!=null && !e.getValue().isBlank())
                .map(e->e.getKey()+"="+encode(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String buildUrl(String path, Map<String,String>params){
        if(path==null || path.isBlank())
            throw new IllegalArgumentException("Path is mandatory in url e.g. top-headlines");
        if(params==null || params.isEmpty())
            throw new IllegalArgumentException("Params are mandatory in url e.g. q=\"Trump\"");

        String base="https://newsapi.org/v2/";
        String query=buildQuery(params);
        return base+path+"?"+query;
    }


    public List<NewsArticle> getNewsList(String path, Map<String,String>params) throws HttpTimeoutException {
        if (apiKey == null || apiKey.isBlank())
            throw new NewsApiException("API key is not configured");

        Gson gson = new GsonBuilder().registerTypeAdapter(BooleanProperty.class, new BooleanPropertyAdapter()).create();
        List<NewsArticle> articles = null;

        String uri = buildUrl(path, params);
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("X-Api-Key", apiKey)
                    .timeout(Duration.ofSeconds(35))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest);

            if (getResponse.statusCode() == 401 || getResponse.statusCode() == 403) {
                throw new NewsApiException("Invalid or missing API key");
            } else if (getResponse.statusCode() != 200)
                throw new NewsApiException("NewsAPI request failed with status code " + getResponse.statusCode());

            NewsHolder response = gson.fromJson(getResponse.body(), NewsHolder.class);
            if (response != null)
                articles = response.getArticles();

        }catch (HttpTimeoutException e){
            throw e;
        }catch(URISyntaxException e){
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
