package com.example.RAG.Service;

import java.util.List;

public interface ServicImpl {
    public String getAnswer(String question, String userId);
    public String saveData(List<String> list);
}
