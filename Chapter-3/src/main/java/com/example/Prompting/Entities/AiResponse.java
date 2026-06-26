package com.example.Prompting.Entities;


import lombok.*;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiResponse {
        private String response;
        private ChatResponseMetadata metadata;

    }

