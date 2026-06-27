package com.example.Prompting.Service;


import com.example.Prompting.Entities.AiResponse;
import com.example.Prompting.Entities.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        Prompt prompt = new Prompt(question, ChatOptions.builder().maxTokens(1000).temperature(0.5).build());


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

// Dynamic query method to handle any type of query by using user
// this is help to enhance and match the desired output of the user.
//
    public String dynamicQuery(String query){
        String DynamicQuery="You are a java expert. you have to write programs. Now answer this question: {query}";
        return geminiChatClient.prompt()
                .user(u-> u.text(DynamicQuery).param("query", query))
                .call().content();
    }

    // Dynamic prompting by using PromptTemplate to handle any type of query by using user

    public String PromptTemplate(){
        PromptTemplate promptTemplate= PromptTemplate.builder()
                .template("what is {name}, and tell me the example of {example}")
                .build();
      String  render= promptTemplate.render(Map.of("name", "Java", "example", "abstact class"));
      Prompt prompt= new Prompt( render, ChatOptions.builder().maxTokens(2000).temperature(0.5).build());
        System.out.println("========== PromptTemplate ==========");
        System.out.println(geminiChatClient.prompt(prompt).call().content());
        System.out.println("==============================");
            return geminiChatClient.prompt(prompt)
                .call().content();
    }


}
