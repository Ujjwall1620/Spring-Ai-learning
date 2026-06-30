package com.example.LLMmemory.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service

public class chatService implements chatSerivceImpl {

    private final ChatClient geminiChatClient;

    public chatService(@Qualifier("geminiChatClient") ChatClient geminiChatClient) {
        this.geminiChatClient = geminiChatClient;
    }

    @Override
    public String chat(String userId, String message) {
        String conversationId = "conversation-" + userId;
        return geminiChatClient.prompt()
                .user(message)
                .advisors(advisorSpec-> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }


    // creating a communication based on the user id which is coming from the header
    @Override
    public String DiffChat(String userId, String message) {
        String conversationId = "conversation-" + userId;
        return geminiChatClient.prompt()
                .user(message)
                .advisors(advisorSpec-> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }



    }

