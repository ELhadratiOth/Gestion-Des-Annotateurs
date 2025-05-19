package com.gestiondesannotateurs.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

@Component
public class ModelLogStreamClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void start() {
        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Builder builder = client.newWebSocketBuilder();
        builder.buildAsync(URI.create("ws://localhost:8000/model/logs/stream"), new LogListener());
    }

    private class LogListener implements WebSocket.Listener {
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("[LOG STREAM]: " + data);
            restTemplate.postForEntity("http://localhost:8080/api/model/logs/receive", data.toString(), Void.class);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }
    }
}
