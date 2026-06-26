package com.example.second.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class chatController {
    private final ChatClient geminiChatClient;
    private final ChatClient ollamaChatClient;

    public chatController(
            @Qualifier("geminiChatClient") ChatClient geminiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {

        this.geminiChatClient = geminiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }


    @GetMapping("/gemini-chat")
    public ResponseEntity<?> chat(@RequestParam(value = "q") String question) {
      String response = geminiChatClient.prompt(question).call().content();
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/ollama-chat")
    public ResponseEntity<?> ollamaChat(@RequestParam(value = "q") String question) {
        String response = ollamaChatClient.prompt(question).call().content();
        return ResponseEntity.ok(response);
    }
}
