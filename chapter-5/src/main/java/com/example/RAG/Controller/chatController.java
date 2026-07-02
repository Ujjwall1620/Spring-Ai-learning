package com.example.RAG.Controller;

import com.example.RAG.Helper.dataStore;
import com.example.RAG.Service.chatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class chatController {

   public final chatService chatService;

    @GetMapping
    public ResponseEntity<String> getChatResponse(
            @RequestParam("q") String question,
            @RequestHeader("userId") String userId) {
        return ResponseEntity.ok(chatService.getAnswer(question,userId));
    }

    @GetMapping("/save")
    public ResponseEntity<String> saveData() {
        String s = chatService.saveData(dataStore.getJavaSpringTopics());
        System.out.println("DATA SAVED!");
        return ResponseEntity.ok(s);
    }
}
