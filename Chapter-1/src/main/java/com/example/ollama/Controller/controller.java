package com.example.ollama.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    private final ChatClient chatClient;

    public controller(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public ResponseEntity<?> chat(
            @RequestParam(value = "q") String prompt) {
            String result = this.chatClient.prompt(prompt).call().content();
        return ResponseEntity.ok("Response from chat with prompt: " + result);
    }
}
