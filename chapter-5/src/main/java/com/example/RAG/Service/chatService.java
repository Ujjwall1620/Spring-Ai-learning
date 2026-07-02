package com.example.RAG.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class chatService implements ServicImpl{

    @Value("classpath:/Prompt/system_message.st")
    private Resource systemMessage;

    public final ChatClient chatClient;

    public final VectorStore vectorStore;

    public chatService(
            @Qualifier("ollamaChatClient") ChatClient chatClient,
            VectorStore vectorStore
    ) {
        this.chatClient=chatClient;
        this.vectorStore=vectorStore;
    }


    // Here I write the manual system prompt to the LLM
    @Override
    public String getAnswer(String question,String userId) {

        SearchRequest searchRequest= SearchRequest.builder().query(question)
                .topK(5).similarityThreshold(0.6).build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        if (documents.isEmpty()) {
            log.info("No relevant documents found in vector store for the question: " + question);
            return "No relevant information found.";
        }
        List<String> list = documents.stream().map(Document::getText).toList();
        String context = String.join(",",list);
        log.info("Context retrieved from vector store: " + context);



        String answer = chatClient.prompt(question)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .system(s->s.text(systemMessage).param("document", context))
                .call()
                .content();
        log.info("Answer generated: " + answer);
        return answer;
    }


    //this is still having a problem, by just system prompt, the LLM is answering the question if it's not in the vector store,
    // but I want it to answer only if it's in the vector store, otherwise it should say "I don't know"


//    //writing a method which can automatically retrieve the data from the vector store and save it to the database
//    // Without giving any system prompt to the LLM!,By using the QuestionAnswerAdvisor!
//
//    public String getAnswerWithoutSystemPrompt(String question,String userId) {
//        String content = chatClient.prompt(question)
//                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
//                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
//                        .searchRequest(
//                                SearchRequest
//                                        .builder()
//                                        .topK(6)
//                                        .similarityThreshold(0.6)
//                                        .build())
//                        .build())
//                .call()
//                .content();
//        log.info("Answer generated without system prompt: " + content);
//        return content;
//    }




    @Override
    public String saveData(List<String> list) {
        List<Document> list1 = list.stream().map(Document::new).toList();
        System.out.println("Saving data to vector store: " + list1);
        vectorStore.add(list1);
        return "Data saved successfully!";
    }
}






