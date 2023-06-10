package ulb.infof307.g04.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ulb.infof307.g04.exceptions.api.*;

public class AbstractAPIService {
    private final HttpClient client;
    protected String baseUri;

    public AbstractAPIService(String baseUri) {
        this(baseUri, HttpClient.newHttpClient());
    }

    public AbstractAPIService(String baseUri, HttpClient client) {
        this.baseUri = baseUri;
        this.client = client;
    }

    Builder getRequestBuilder(String endpoint) {
        return HttpRequest.newBuilder()
            .uri(URI.create(this.baseUri + endpoint))
            .header("Content-Type", "application/json");
    }

    HttpRequest.Builder getRequestBuilder(String endpoint, String authToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json");

        if (authToken != null) {
            builder = builder.header("Authorization", authToken);
        }

        return builder;
    }

    HttpResponse<String> sendRequest(HttpRequest request) throws ApiException {
        try {
            HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
            handleExceptions(response);
            return response;
        } catch (IOException | InterruptedException e) {
            throw new NetworkException();
        }
    }

    HttpResponse<String> sendCreateObjectRequest(String endpoint, String token, Object body) throws ApiException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String bodyJson = mapper.writeValueAsString(body);

            HttpRequest request = this.getRequestBuilder(endpoint, token)
                    .POST(BodyPublishers.ofString(bodyJson))
                    .header("Content-Type", "application/json") // specify the content type of the request body
                    .build();

            return this.sendRequest(request);
        } catch (IOException e) {
            throw new NetworkException();
        }
    }



    HttpResponse<String> sendFetchOneObjectRequest(String endpoint, String token, int id) throws ApiException {
        HttpRequest request = this.getRequestBuilder(String.format("%s%d/", endpoint, id), token)
            .GET()
            .build();

        return this.sendRequest(request);
    }

    HttpResponse<String> sendFetchAllObjectRequest(String endpoint, String token) throws ApiException {
        HttpRequest request = this.getRequestBuilder(endpoint, token)
            .GET()
            .build();

        return this.sendRequest(request);
    }

    HttpResponse<String> sendPatchObjectRequest(String endpoint, String token, int id, Object body) throws ApiException {
        ObjectMapper mapper = new ObjectMapper();
        String bodyJson = null;
        try {
            bodyJson = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ApiException("An error occurred while processing the request.");
        }

        HttpRequest request = this.getRequestBuilder(String.format("%s%d/", endpoint, id), token)
            .method("PATCH", BodyPublishers.ofString(bodyJson))
            .build();
        return this.sendRequest(request);
    }

    HttpResponse<String> sendDeleteObjectRequest(String endpoint, String token, int id) throws ApiException {
        HttpRequest request = this.getRequestBuilder(String.format("%s%d/", endpoint, id), token)
            .DELETE()
            .build();

        return this.sendRequest(request);
    }


    protected void handleExceptions(HttpResponse<String> response) throws ApiException {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 400) {
            return;
        }
        String errorMessage = response.body();

        switch (response.statusCode()) {
            case 400 -> throw new BadRequestException(errorMessage);
            case 401 -> throw new UnauthorizedException(errorMessage);
            case 403 -> throw new ForbiddenException(errorMessage);
            case 404 -> throw new NotFoundException(errorMessage);
            case 415 -> throw new UnsupportedMediaTypeException(errorMessage);
            case 500 -> throw new ServerException(errorMessage);
            default -> {
                if (response.statusCode() >= 400) {
                    throw new ApiException("An error occurred while processing the request.");
                }
            }
        }
    }



}