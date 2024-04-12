package com.goorm.wordsketch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.wordsketch.batch.openai.OpenAIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIService {

    @Value("${openai-key}")
    private String openAIApiKey;
    private final WebClient webClient;
    private final S3Service s3Service;

    @Autowired
    public OpenAIService(S3Service s3Service) {
        this.s3Service = s3Service;
        this.webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build())
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization",
                "Bearer " + openAIApiKey)
            .build();
    }

    public String getImage(String prompt) throws JsonProcessingException {
        Mono<String> result = this.webClient.post()
            .uri("/images/generations")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                "{\n" +
                    "  \"model\": \"dall-e-3\",\n" +
                    "  \"prompt\": \"" + prompt + " 속담 8비트 형태로 그려줘" + "\",\n" +
                    "  \"n\": 1,\n" +
                    "  \"size\": \"1024x1024\",\n" +
                    "  \"response_format\": \"b64_json\"\n" +
                    "}"
            )
            .retrieve()
            .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                response -> response.bodyToMono(String.class).map(Exception::new))
            .bodyToMono(String.class);

        String str = result.block();
        ObjectMapper mapper = new ObjectMapper();
        OpenAIResponse openAIResponse = mapper.readValue(str, OpenAIResponse.class);

        return s3Service.uploadImageToS3(openAIResponse.getData().get(0).getB64_json());
    }
}