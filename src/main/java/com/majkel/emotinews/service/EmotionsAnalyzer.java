package com.majkel.emotinews.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EmotionsAnalyzer {

    private final static HttpClient httpClient=HttpClient.newHttpClient();

    public List<TextEmotion> parseArticles(List<String> news){
        System.out.println(news);
        System.out.println("\n");
        System.out.println(news.size());
        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(news==null || news.isEmpty())
            return new ArrayList<>();
        List<TextEmotion> emotionsList=null;
        Gson gson=new Gson();
        try{
            HttpRequest httpPost=HttpRequest.newBuilder()
                    .uri(new URI("https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-sentiment"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\n"+"\"inputs\": "+gson.toJson(news)+"\n}"))
                    .header("Authorization", ConfigLoader.getValue("api.huggingface.emotions.analizer"))
                    .header("Content-Type", "application/json")
                    //.timeout(Duration.ofSeconds(45))
                    .build();
            HttpResponse<String> stringHttpResponse=httpClient.send(httpPost, HttpResponse.BodyHandlers.ofString());

            if(stringHttpResponse.statusCode()!=200)
                throw new ParsingNewsApiException("Received status code "+ stringHttpResponse.statusCode());

            if (stringHttpResponse.statusCode() == 429) {
                throw new ParsingNewsApiException("Rate limit exceeded when calling HuggingFace API");
            }

            Type type = new TypeToken<List<List<TextEmotion>>>(){}.getType();
            List<List<TextEmotion>> parsed = gson.fromJson(stringHttpResponse.body(), type);
            emotionsList = parsed.get(0);

        }catch(URISyntaxException e){
            throw new RuntimeException("URISyntaxException ",e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParsingNewsApiException("Thread interrupted while calling HuggingFace API", e);
        } catch (IOException e) {
            throw new ParsingNewsApiException("I/O error while calling HuggingFace API", e);
        }

        return emotionsList!=null?emotionsList:new ArrayList<>();
    }
}
