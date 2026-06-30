package com.example.LLMmemory.Controller;

import com.example.LLMmemory.Service.chatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    public final chatService chatService;
    @GetMapping
    public ResponseEntity<?> chat(@RequestParam("q") String message, HttpSession session) {
        // Use the built-in HttpSession ID as the unique conversation identifier
        String conversationId = session.getId();
        return ResponseEntity.ok(chatService.chat(conversationId, message));
    }


    @GetMapping("/Diff")
    public ResponseEntity<?> Diffchat(
            @RequestParam("q") String message,
            // @RequestHeader is use for getting header from the request
            @RequestHeader("userID") String userID) {
        return ResponseEntity.ok(chatService.chat(userID, message));
    }

}
