package com.majkel.emotinews.service;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface HttpClientPort {
    HttpResponse<String> send(HttpRequest httpRequest) throws IOException,InterruptedException;
}
