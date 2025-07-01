package com.majkel.emotinews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.majkel.emotinews.exception.ParsingNewsApiException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EmotionsAnalyzer {

    private final static HttpClient httpClient=HttpClient.newHttpClient();

    public List<TextEmotion> parseText(List<String> news){
        List<TextEmotion> emotionsList=null;
        Gson gson=new Gson();
        try{
            HttpRequest httpPost=HttpRequest.newBuilder()
                    .uri(new URI("https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-sentiment"))
                    .POST(HttpRequest.BodyPublishers.ofString("\"inputs\": "+gson.toJson(news)))
                    .header("Authorization",ConfigLoader.getValue("api.huggingface.emotions.analizer"))
                    .build();
            HttpResponse<String> stringHttpResponse=httpClient.send(httpPost, HttpResponse.BodyHandlers.ofString());

            if(stringHttpResponse.statusCode()!=200)
                throw new ParsingNewsApiException("Received status code "+ stringHttpResponse.statusCode());

            Type type=new TypeToken<List<TextEmotion>>(){}.getType();
            emotionsList=gson.fromJson(stringHttpResponse.body(),type);

        }catch(URISyntaxException e){
            throw new RuntimeException("URISyntaxException ",e);
        }catch (IOException | InterruptedException e){
            throw new ParsingNewsApiException("Error while calling HuggingFaceAPI (news parser)",e);
        }

        return emotionsList!=null?emotionsList:List.of();
    }
}
