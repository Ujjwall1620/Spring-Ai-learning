package com.example.LLMmemory.Service;

public interface chatSerivceImpl {
    public String chat(String userId, String message);

    public String DiffChat(String userId,String message);
}
