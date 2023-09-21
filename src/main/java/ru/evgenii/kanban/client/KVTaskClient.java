package ru.evgenii.kanban.client;

import ru.evgenii.kanban.exeptions.RequestFailedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    String URL;
    String API_TOKEN;
    HttpClient httpClient;

    public KVTaskClient(String url) {
        this.URL = url;
        httpClient = HttpClient.newHttpClient();
        this.API_TOKEN = register(URL);
    }

    private String register(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/register"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RequestFailedException("Запрос регистрации не удлася" + response.statusCode());
            }

        } catch (IOException | InterruptedException exception) {
            throw new RequestFailedException("Не удалось сделать запрос");
        }
    }

    public void put(String key, String json) {
        if(!json.equals("[]")) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/save/" + key + "?API_TOKEN=" + API_TOKEN))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RequestFailedException("Запрос сохранения не выполнился");
                }
            } catch (IOException | InterruptedException e) {
                throw new RequestFailedException("Запрос сохранения не выполнился");
            }
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/load/" + key + "?API_TOKEN=" + API_TOKEN))
                .GET()
                .build();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200) {
                throw new RequestFailedException("Запрос загрузки не выполнился");
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RequestFailedException("Запрос загрузки не выполнился");
        }
    }
}
