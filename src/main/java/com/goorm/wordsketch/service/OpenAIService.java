package com.goorm.wordsketch.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIService {

    private final WebClient webClient;

    public OpenAIService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization",
                "Bearer MY-API-KEY")
            .build();
    }

    public Mono<String> getImage(String prompt) {
        return this.webClient.post()
            .uri("/images/generations")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                "{\n" +
                    "  \"model\": \"dall-e-3\",\n" +
                    "  \"prompt\": \"" + prompt + "\",\n" +
                    "  \"n\": 1,\n" +
                    "  \"size\": \"1024x1024\"\n" +
                    "}"
            )
            .retrieve()
            .bodyToMono(String.class);
    }
}