package com.alura.literalura.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class ConsumoAPI {

    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error en la consulta. Código HTTP: " + response.statusCode());
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("No se pudo obtener la información de la API: " + e.getMessage());
        }
    }

    public String codificarValor(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}