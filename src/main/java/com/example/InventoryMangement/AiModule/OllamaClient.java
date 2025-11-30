package com.example.InventoryMangement.AiModule;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class OllamaClient {

    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public OllamaClient() {
        this.client = WebClient.builder()
                .baseUrl("http://127.0.0.1:11434/v1")
                .build();
    }

    public String ask(String prompt) {
        record Message(String role, String content) {}
        record Request(String model, List<Message> messages) {}

        Request req = new Request(
                "tinyllama:latest",
                List.of(new Message("user", prompt))
        );

        String json = client.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractMessage(json);
    }

    private String extractMessage(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            return "Error parsing response.";
        }
    }
}
