package com.majkel.emotinews.adapter;

import com.majkel.emotinews.service.HttpClientPort;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientWrapper implements HttpClientPort {
    private final HttpClient httpClient;

    public HttpClientWrapper(HttpClient httpClient){
        this.httpClient=httpClient;
    }

    @Override
    public HttpResponse<String> send(HttpRequest httpRequest) throws IOException,InterruptedException {
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
