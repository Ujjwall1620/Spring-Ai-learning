package com.example.Prompting.Controller;

import com.example.Prompting.Entities.Response;
import com.example.Prompting.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class chatController {

    private final ChatService chatService;

    public chatController(ChatService chatService){
        this.chatService = chatService;
    }


    @GetMapping("/gemini-chat")
    public ResponseEntity<?> chat(@RequestParam(value = "q") String question) {
          Response res=  chatService.chatWithGemini(question);
        return  ResponseEntity.ok(res);
    }

    @GetMapping("/gemini-metadata")
    public ResponseEntity<?> MetaData(@RequestParam(value = "q") String question) {

        return  ResponseEntity.ok(chatService.metaData(question));
    }

    @GetMapping("/gemini-RM")
    public ResponseEntity<?> getResponseWithMetadata(@RequestParam(value = "q") String question) {
        return  ResponseEntity.ok(chatService.AiResponse(question));
    }}



