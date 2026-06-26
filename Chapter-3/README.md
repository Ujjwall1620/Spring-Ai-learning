# 🚀 Spring AI - Chapter 3: Prompt Engineering & Structured AI Responses

A practical learning project that demonstrates **Prompt Engineering** and **structured AI response mapping** using Spring AI with Google Gemini.

This chapter teaches how to interact with Large Language Models (LLMs) through Spring Framework, focusing on crafting effective prompts and converting AI responses into well-defined Java objects.

---

## 📚 Project Description

**Chapter-3** implements core Prompt Engineering concepts using Spring Boot and Spring AI. The project demonstrates:

- Creating and customizing prompts for LLM interaction
- Using system prompts to guide AI behavior and improve response quality
- Extracting and utilizing response metadata (token usage, model information, finish reason)
- Mapping AI responses into structured Java entities instead of raw text
- Building clean, production-grade REST APIs for AI-powered applications

**Why Prompt Engineering?**

Effective prompts are the foundation of quality AI interactions. Proper prompt structure and system prompts significantly improve response quality and relevance.

**Why Structured Responses?**

Returning structured Java objects instead of raw strings provides:
- Type safety and compile-time checking
- Easy serialization to JSON for REST APIs
- Better code maintainability and integration
- Clear contract definition for API consumers

---

## 🎯 Learning Objectives

This chapter covers:

- ✅ **Prompt API** - Creating Prompt objects in Spring AI
- ✅ **Prompt Customization** - Building effective user prompts
- ✅ **System Prompts** - Using system-level instructions to improve AI response quality
- ✅ **ChatClient** - The fluent API for interacting with language models
- ✅ **ChatResponse & Metadata** - Extracting raw responses and metadata
- ✅ **Entity Mapping** - Converting AI responses into Java objects
- ✅ **Structured Output** - Benefits of returning DTOs vs raw text
- ✅ **REST Architecture** - Building clean layered Spring Boot applications

---

## ✨ Features

- 🔹 **Prompt-Based AI Interaction** - Create and execute prompts programmatically
- 🔹 **System Prompt Support** - Use system-level instructions to guide AI behavior
- 🔹 **Structured AI Responses** - Map AI responses to Java entities
- 🔹 **Response Metadata Extraction** - Access token usage, model info, finish reasons
- 🔹 **Three REST API Endpoints** - Different approaches to AI interaction
- 🔹 **Clean Layered Architecture** - Controller → Service → ChatClient pattern
- 🔹 **Google Gemini Integration** - Full integration with Gemini API
- 🔹 **Dependency Injection** - Proper use of Spring's DI container
- 🔹 **Lombok Integration** - Reduced boilerplate code

---

## 🛠️ Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming language |
| **Spring Boot** | 4.0.6 | Web application framework |
| **Spring AI** | 2.0.0 | AI/ML integration |
| **Google Gemini** | gemini-2.5-flash | Large Language Model |
| **Maven** | Latest | Build management |
| **Lombok** | Latest | Reduce boilerplate |
| **REST API** | HTTP/JSON | Communication |

---

## 📁 Project Structure

```
Chapter-3/
│
├── pom.xml                              # Maven configuration
├── README.md                            # Documentation
│
├── src/main/
│   ├── java/com/example/Prompting/
│   │   ├── Chapter3Application.java     # Spring Boot entry point
│   │   │
│   │   ├── Controller/
│   │   │   └── chatController.java      # REST API endpoints
│   │   │
│   │   ├── Service/
│   │   │   └── ChatService.java         # Business logic & AI interaction
│   │   │
│   │   ├── Configuration/
│   │   │   └── ChatClientConfig.java    # Spring AI ChatClient beans
│   │   │
│   │   └── Entities/
│   │       ├── Response.java            # DTO for basic responses
│   │       └── AiResponse.java          # DTO for response + metadata
│   │
│   └── resources/
│       ├── application.properties       # Configuration
│       ├── static/                      # Static files
│       └── templates/                   # HTML templates
│
└── target/                              # Compiled artifacts
```

---

## 🔌 API Endpoints

Three REST endpoints demonstrating different AI interaction approaches:

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| GET | `/chat/gemini-chat` | Basic AI interaction | `q` (question) | `Response` object |
| GET | `/chat/gemini-metadata` | Extract response metadata | `q` (question) | `ChatResponseMetadata` |
| GET | `/chat/gemini-RM` | Response + system prompt + metadata | `q` (question) | `AiResponse` object |

### Example Requests

**Basic Chat:**
```
GET http://localhost:8081/chat/gemini-chat?q=What%20is%20Java%3F
```

**Metadata Only:**
```
GET http://localhost:8081/chat/gemini-metadata?q=Explain%20Spring%20Boot
```

**Response with System Prompt:**
```
GET http://localhost:8081/chat/gemini-RM?q=What%20are%20Java%20generics%3F
```

---

## 🔄 How the Application Works

Request flow through the application layers:

```
HTTP Client
    ↓
chatController (receives question)
    ↓
ChatService (creates Prompt)
    ↓
ChatClient.prompt()
    ↓
System Prompt (optional)
    ↓
.call() (executes request)
    ↓
Google Gemini API
    ↓
ChatResponse (raw response + metadata)
    ↓
Entity Mapping (.entity(Class))
    ↓
Response / AiResponse Object
    ↓
JSON Serialization
    ↓
HTTP Response (200 OK)
```

### Three Approaches Demonstrated

**Approach 1: Basic Response Mapping** (`/gemini-chat`)
```
Question → Prompt → ChatClient → Gemini → Response Entity
```

**Approach 2: Metadata Only** (`/gemini-metadata`)
```
Question → Prompt → ChatClient → Gemini → Metadata Extraction
```

**Approach 3: Response + System Prompt + Metadata** (`/gemini-RM`)
```
Question → Prompt → System Prompt → ChatClient → Gemini → AiResponse Entity
```

---

## 🎯 Prompt Engineering

This project demonstrates two levels of prompting:

### 1. User Prompt (Basic)

The user's question becomes the prompt:

```java
Prompt prompt = new Prompt(question);
```

**Use Case:** General queries and simple Q&A

### 2. System Prompt + User Prompt (Advanced)

System instructions guide the AI's behavior and format:

```java
ChatResponse response = geminiChatClient.prompt(prompt)
    .system("""
        You are an expert Java mentor.
        
        Explain in simple English.
        Use headings.
        Use bullet points.
        Give one example.
        Keep the answer concise.
        """)
    .call()
    .chatResponse();
```

**Benefits of System Prompts:**

✅ Defines the AI's role and expertise  
✅ Provides response format instructions  
✅ Ensures consistent tone and style  
✅ Improves response relevance  
✅ Reduces hallucinations through clear guidelines  

---

## 🔷 Structured AI Responses

AI responses are mapped into Java objects instead of raw text:

### Response Entity

```java
public class Response {
    String tittle;      // Response title
    String content;     // Main response text
    String createdAt;   // Timestamp
}
```

**Mapping:**
```java
Response response = geminiChatClient.prompt(prompt)
    .call()
    .entity(Response.class);
```

### AiResponse Entity

```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiResponse {
    private String response;                    // AI-generated text
    private ChatResponseMetadata metadata;      // Response metadata
}
```

### Benefits of Structured Responses

| Aspect | Raw String | Structured Entity |
|--------|-----------|-------------------|
| **Type Safety** | ❌ No | ✅ Yes |
| **IDE Support** | ❌ No | ✅ Yes (autocomplete) |
| **Compile Checks** | ❌ No | ✅ Yes |
| **JSON Serialization** | ❌ Manual | ✅ Automatic |
| **API Contract** | ❌ Unclear | ✅ Clear |

---

## 📊 Response Metadata

Spring AI provides detailed metadata about each AI response:

```java
ChatResponseMetadata metadata = response.getMetadata();
```

### Available Metadata

- **Token Usage** - Input/output token counts
- **Model Information** - Which model generated the response
- **Finish Reason** - How generation finished (STOP, MAX_TOKENS, etc.)
- **Request/Response IDs** - For debugging and tracing
- **Rate Limit Info** - API rate limit details

### Example Metadata

```json
{
  "tokenUsage": {
    "inputTokens": 42,
    "outputTokens": 156,
    "totalTokens": 198
  },
  "model": "gemini-2.5-flash",
  "finishReason": "STOP",
  "rateLimit": {
    "requestsPerMinute": 60,
    "requestsPerDay": 1500
  }
}
```

---

## ⚙️ Configuration

Configure Google Gemini API with these properties:

### Required Properties

```properties
# API Key (use environment variable)
spring.ai.google.genai.api-key=${Gemini_API_Key}

# Model Selection
spring.ai.google.genai.chat.options.model=gemini-2.5-flash

# Server Port
server.port=8081

# Application Name
spring.application.name=chapter-3
```

### Setting Environment Variables

**Windows (PowerShell):**
```powershell
$env:Gemini_API_Key = "your-api-key-here"
```

**Windows (Command Prompt):**
```cmd
set Gemini_API_Key=your-api-key-here
```

**Linux/Mac:**
```bash
export Gemini_API_Key=your-api-key-here
```

### Security Best Practices

⚠️ **NEVER commit API keys to version control!**

Instead:
- ✅ Use environment variables
- ✅ Store in secure vaults
- ✅ Use external config files (.gitignored)
- ✅ Use Spring Cloud Config Server

---

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Google Cloud Account with Gemini API
- Gemini API Key

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/Spring-Ai.git
cd Spring-Ai/Chapter-3
```

### Step 2: Get Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Click "Create API Key"
3. Copy the generated API key

### Step 3: Set Environment Variable

**Windows (PowerShell):**
```powershell
$env:Gemini_API_Key = "your-actual-api-key"
```

**Linux/Mac:**
```bash
export Gemini_API_Key=your-actual-api-key
```

### Step 4: Build Project

```bash
mvn clean install
```

**Expected Output:**
```
BUILD SUCCESS
Total time: X.XXs
```

### Step 5: Run Application

```bash
mvn spring-boot:run
```

**Expected Output:**
```
2026-06-26 18:02:01.649 INFO  Chapter3Application : Started Chapter3Application
INFO Tomcat started on port(s): 8081
```

### Step 6: Test APIs

```bash
# Basic Chat
curl "http://localhost:8081/chat/gemini-chat?q=What%20is%20Java"

# Metadata
curl "http://localhost:8081/chat/gemini-metadata?q=Explain%20Spring%20Boot"

# Response with Metadata
curl "http://localhost:8081/chat/gemini-RM?q=What%20are%20generics"
```

---

## 📝 Example Request

### Using curl

```bash
curl -X GET "http://localhost:8081/chat/gemini-RM?q=Explain%20what%20the%20Spring%20AI%20ChatClient%20is"
```

### Using Postman

1. Create a new **GET** request
2. URL: `http://localhost:8081/chat/gemini-RM`
3. Query Params:
   - Key: `q`
   - Value: `Explain what the Spring AI ChatClient is`
4. Click **Send**

---

## 📤 Example Response

### Basic Chat Response (`/gemini-chat`)

```json
{
  "tittle": "What is Java?",
  "content": "Java is a general-purpose, class-based, object-oriented programming language designed to have as few implementation dependencies as possible.",
  "createdAt": "2026-06-26T18:02:33.000Z"
}
```

### Response with Metadata (`/gemini-RM`)

```json
{
  "response": "# Spring AI ChatClient\n\n## Overview\nThe ChatClient is a fluent API for interacting with AI models.\n\n## Key Features\n- Type-safe prompt building\n- Automatic response mapping\n- Metadata extraction\n\n## Example\n```java\nChatResponse response = chatClient.prompt(prompt).call().chatResponse();\n```",
  "metadata": {
    "tokenUsage": {
      "inputTokens": 47,
      "outputTokens": 289,
      "totalTokens": 336
    },
    "model": "gemini-2.5-flash",
    "finishReason": "STOP"
  }
}
```

### Metadata Only Response (`/gemini-metadata`)

```json
{
  "tokenUsage": {
    "inputTokens": 42,
    "outputTokens": 156,
    "totalTokens": 198
  },
  "model": "gemini-2.5-flash",
  "finishReason": "STOP",
  "rateLimit": {
    "requestsPerMinute": 60,
    "requestsPerDay": 1500
  }
}
```

---

## 🎓 Key Spring AI Concepts Demonstrated

### 1. ChatClient
The fluent API for interacting with language models.
```java
ChatClient chatClient = ChatClient.builder(chatModel).build();
```

### 2. Prompt
Represents the input to the model.
```java
Prompt prompt = new Prompt(userQuestion);
```

### 3. System Prompt
High-level instructions defining AI behavior.
```java
.system("You are a Java expert. Explain concisely.")
```

### 4. ChatResponse
The raw response from the model with output and metadata.
```java
ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
```

### 5. ChatResponseMetadata
Detailed information about the response including token usage.
```java
ChatResponseMetadata metadata = response.getMetadata();
```

### 6. Entity Mapping
Spring AI's automatic mapping of AI responses to Java objects.
```java
Response response = chatClient.prompt(prompt).call().entity(Response.class);
```

### 7. Structured Output
Converting unstructured text into well-defined Java objects.
```java
AiResponse aiResponse = new AiResponse(text, metadata);
```

---

## 🙏 Acknowledgments

- **Spring AI Team** - For the excellent Spring AI framework
- **Google Cloud** - For the Gemini API
- **Spring Community** - For continuous support

---

**Happy Learning! 🎓**

This chapter focuses on foundational Prompt Engineering concepts. Master these before moving to advanced topics.
