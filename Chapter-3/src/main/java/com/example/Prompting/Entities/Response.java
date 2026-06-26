package com.example.Prompting.Entities;


public class Response {
    String tittle;
    String content;
    String createdAt;

    public Response() {
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Response(String tittle, String content, String createdAt) {
        this.tittle = tittle;
        this.content = content;
        this.createdAt = createdAt;
    }

}
