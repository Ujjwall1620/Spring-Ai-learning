package com.example.Prompting.Service;


import com.example.Prompting.Entities.AiResponse;
import com.example.Prompting.Entities.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient geminiChatClient;
    public ChatService(
            @Qualifier("geminiChatClient") ChatClient geminiChatClient
    ) {
        this.geminiChatClient = geminiChatClient;
    }

    public Response chatWithGemini(String question) {
        Prompt prompt = new Prompt(question);
        Response response = geminiChatClient.prompt(prompt)
                .call()
                .entity(Response.class);
        return response;
    }

    public ChatResponseMetadata metaData(String question) {
        Prompt prompt = new Prompt(question);
      return geminiChatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getMetadata();

    }

    public AiResponse AiResponse(String question) {
        Prompt prompt = new Prompt(question);

        ChatResponse response = geminiChatClient.prompt(prompt)
                .system("""
            You are an expert Java mentor.

            Explain in simple English.
            Use headings.
            Use bullet points.
            Give one example.
            Keep the answer concise.
            """)
                .call().chatResponse();
        String res = response.getResult().getOutput().getText();
        ChatResponseMetadata metadata = response.getMetadata();
        AiResponse aiResponse = new AiResponse(res, metadata);
        System.out.println("========== RESPONSE ==========");
        System.out.println(res);
        System.out.println("==============================");
        return aiResponse;
    }

}
