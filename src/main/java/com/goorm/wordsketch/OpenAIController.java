package com.goorm.wordsketch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goorm.wordsketch.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class OpenAIController {

    private final OpenAIService openAIService;

    @Autowired
    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/image")
    public Mono<String> askGPT(@RequestParam String prompt) throws JsonProcessingException {
        System.out.println(prompt);
        return openAIService.getImage(prompt);
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("good");
    }
}
