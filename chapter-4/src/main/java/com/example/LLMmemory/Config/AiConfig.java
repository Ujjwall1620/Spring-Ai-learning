package com.example.LLMmemory.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.ai.tool.ToolCallback.logger;


@Slf4j
@Configuration
public class AiConfig {

    // if you wants to change the default configuration of the jdbcChatMemoryRepository then:
    // you can create a bean for the jdbcChatMemoryRepository
    @Bean
    public ChatMemory chatMemory (JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }


    @Bean(name = "geminiChatClient")
    public ChatClient geminiChatClient(GoogleGenAiChatModel chatModel, ChatMemory chatMemory) {
        log.info("Implementation of chatMemory: "+chatMemory.getClass().getName());
      MessageChatMemoryAdvisor memoryAdvisor=  MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel).defaultAdvisors(memoryAdvisor).defaultOptions(GoogleGenAiChatOptions.builder().maxOutputTokens(2000).temperature(0.7)).build();
    }

}
