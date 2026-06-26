package com.example.second.Configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean(name = "geminiChatClient")
    public ChatClient geminiChatClient(
            GoogleGenAiChatModel chatModel
    ) {
        return ChatClient.builder(chatModel)
                .build();
    }

    @Bean(name = "ollamaChatClient")
    public ChatClient ollamaChatClient( OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }
}


