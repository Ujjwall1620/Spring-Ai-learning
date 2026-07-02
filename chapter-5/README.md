# Spring AI RAG Implementation - Complete Learning Guide for Chapter-5

This is a comprehensive guide to understanding and implementing Retrieval-Augmented Generation (RAG) using Spring Boot 4.1.0 and Spring AI 2.0.0.

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Technologies Used](#2-technologies-used)
3. [Project Architecture](#3-project-architecture)
4. [Dependencies Analysis](#4-dependencies-analysis)
5. [Application Configuration](#5-application-configuration)
6. [Project Structure](#6-project-structure--package-organization)
7. [How RAG Works](#7-how-rag-works)
8. [Service Layer Methods](#8-service-layer-methods)
9. [Spring AI Concepts](#9-spring-ai-concepts-used)
10. [Vector Database (MariaDB)](#10-vector-database-mariadb)
11. [Ollama Integration](#11-ollama-integration)
12. [Similarity Search](#12-similarity-search)
13. [VectorStore](#13-vectorstore)
14. [Code Walkthrough](#14-code-walkthrough)
15. [API Endpoints](#15-api-endpoints)
16. [How to Run](#16-how-to-run)
17. [Learning Summary](#17-learning-summary)
18. [Future Improvements](#18-future-improvements)

---

## 1. Project Overview

### What is this Project?

This project is a **Retrieval-Augmented Generation (RAG)** system built with Spring Boot and Spring AI. It's a question-answering application that retrieves relevant documents from a vector database and uses them to generate accurate, context-aware answers using a locally-running Large Language Model (Ollama).

**Real-world example:** Imagine a customer support chatbot that answers ONLY based on your company's knowledge base—never hallucinating or using general internet knowledge.

### The Problem RAG Solves

| Problem | Without RAG | With RAG |
|---------|-----------|----------|
| **Hallucination** | "I think the answer might be..." (wrong!) | "According to your document..." (correct!) |
| **Outdated Knowledge** | LLM trained on 2023 data | Load 2024 documents dynamically |
| **Generic Responses** | Generic answer from training data | Domain-specific answer from your docs |
| **No Source** | "Where did this come from?" | "Found in document X, paragraph Y" |
| **Privacy** | Data sent to cloud APIs | Everything runs locally |

### Why RAG Instead of Normal LLM?

**Without RAG:**
```
User: "What is encapsulation?"
    ↓
LLM: "Encapsulation is... hmm... probably..." (hallucinating?)
```

**With RAG:**
```
User: "What is encapsulation?"
    ↓
Search: Find relevant documents about encapsulation
    ↓
Found: "Encapsulation protects object data by keeping fields private..."
    ↓
LLM: "Based on the provided document: Encapsulation protects..." (accurate!)
```

**Key Benefit:** RAG constrains the LLM to answer ONLY from provided documents, eliminating hallucination.

### Learning Objectives

✅ Spring AI fundamentals and ChatClient API  
✅ Vector embeddings and semantic search  
✅ Vector databases (MariaDB)  
✅ Complete RAG pipeline  
✅ Ollama local LLM integration  
✅ System prompts and prompt engineering  
✅ Chat memory for multi-turn conversations  
✅ SearchRequest and retrieval tuning  

---

## 2. Technologies Used

| Technology | Version | Role | Why |
|-----------|---------|------|-----|
| **Java** | 21 | Language | Modern features (records, pattern matching) |
| **Spring Boot** | 4.1.0 | Framework | Auto-config, embedded server, production-ready |
| **Spring AI** | 2.0.0 | LLM Integration | Unified API, RAG support |
| **Ollama** | Latest | LLM Runtime | Local, private, fast |
| **MariaDB** | Latest | Vector DB | SQL + vector search, cost-effective |
| **Maven** | Latest | Build Tool | Dependency management |
| **Docker** | Latest | Containers | Service isolation |
| **nomic-embed-text** | Latest | Embeddings | 768D vectors, semantic search |
| **qwen2.5:1.5b** | Latest | Chat Model | Lightweight, fast, quality answers |

---

## 3. Project Architecture

### Data Flow Diagram

```
┌─────────────────────────┐
│   User Question         │
└────────────┬────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ↓                 ↓
┌─────────────┐  ┌──────────────┐
│ RETRIEVAL   │  │ DATA LOADING │
│ (getAnswer) │  │ (save docs)  │
└─────────────┘  └──────────────┘
    │                 │
    ↓                 ↓
1. SearchRequest    1. Load strings
2. Embed question   2. Create Documents
3. Find top 5       3. Generate embeddings
4. Extract text     4. Store in MariaDB
5. Build context    5. Create indexes
6. Add system msg
7. Call LLM
    │                 │
    └────────┬────────┘
             │
             ↓
    ┌──────────────────┐
    │ ChatClient       │
    │ + Advisors       │
    │ (Memory + Log)   │
    └────────┬─────────┘
             │
             ↓
    ┌──────────────────┐
    │ Ollama LLM       │
    │ qwen2.5:1.5b     │
    └────────┬─────────┘
             │
             ↓
    ┌──────────────────┐
    │ Answer to User   │
    └──────────────────┘
```

---

## 4. Dependencies Analysis

### Overview

| Dependency | Required | Purpose |
|-----------|----------|---------|
| **spring-ai-starter-model-ollama** | ✅ | Ollama integration |
| **spring-ai-starter-vector-store-mariadb** | ✅ | Vector database |
| **spring-ai-rag** | ✅ | RAG abstractions |
| **spring-boot-starter-webmvc** | ✅ | REST API |
| **spring-boot-starter-jdbc** | ✅ | Database connectivity |
| **mariadb-java-client** | ✅ | MariaDB driver |
| **lombok** | ⚠️ | Code generation (optional) |
| **spring-ai-vector-store-advisor** | ⚠️ | Advisor pattern (unused) |
| **spring-ai-tika-document-reader** | ⚠️ | PDF loading (unused) |

### Critical Dependencies

**spring-ai-starter-model-ollama:**
- Provides OllamaChatModel class
- Handles HTTP communication to Ollama
- Auto-configured from properties
- If removed: OllamaChatModel not found → app fails to start

**spring-ai-starter-vector-store-mariadb:**
- Implements VectorStore interface
- Auto-creates vector store tables
- Handles similarity search
- If removed: VectorStore bean missing → startup fails

**spring-ai-rag:**
- Provides Document, SearchRequest classes
- RAG pattern abstractions
- If removed: Compilation errors

**spring-boot-starter-webmvc:**
- REST controller support
- If removed: Endpoints not registered

**mariadb-java-client:**
- JDBC driver for MariaDB
- If removed: "No suitable driver" SQLException

---

## 5. Application Configuration

### Properties

```properties
# Application Identity
spring.application.name=RAG
server.port=8082

# Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen2.5:1.5b
spring.ai.ollama.embedding.options.model=nomic-embed-text

# Vector Store Configuration
spring.ai.vectorstore.mariadb.initialize-schema=true

# Database Configuration
spring.datasource.url=jdbc:mariadb://localhost:3308/Spring_Ai
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```

### Configuration Details

| Property | Value | Purpose |
|----------|-------|---------|
| `spring.application.name` | RAG | Application identifier |
| `server.port` | 8082 | HTTP port (non-standard to avoid conflicts) |
| `spring.ai.ollama.base-url` | http://localhost:11434 | Ollama API endpoint |
| `spring.ai.ollama.chat.options.model` | qwen2.5:1.5b | Chat model name |
| `spring.ai.ollama.embedding.options.model` | nomic-embed-text | Embedding model (768D vectors) |
| `spring.ai.vectorstore.mariadb.initialize-schema` | true | Auto-create tables |
| `spring.datasource.url` | jdbc:mariadb://localhost:3308/Spring_Ai | Database URL (note: port 3308) |
| `spring.datasource.username` | root | Database user |
| `spring.datasource.password` | root | Database password |
| `spring.datasource.driver-class-name` | org.mariadb.jdbc.Driver | JDBC driver |

---

## 6. Project Structure & Package Organization

### Package Layout

```
com.example.RAG/
├── RagApplication.java                    [Bootstrap]
├── Config/
│   └── AiConfig.java                      [Spring Beans]
├── Controller/
│   └── chatController.java                [REST API]
├── Service/
│   ├── chatService.java                   [RAG Implementation]
│   └── ServicImpl.java                     [Interface]
├── Helper/
│   └── dataStore.java                     [Sample Data - 50 topics]
└── resources/
    ├── application.properties
    └── Prompt/
        └── system_message.st              [LLM System Prompt]
```

### Responsibilities

**RagApplication.java:**
- Spring Boot entry point
- Starts Tomcat on port 8082
- Scans components, initializes Spring context

**AiConfig.java:**
- Creates ChatMemory (max 20 messages)
- Creates ChatClient with advisors (MessageChatMemoryAdvisor, SimpleLoggerAdvisor)
- Configures Ollama integration

**chatController.java:**
- `GET /chat?q=question&userId=user123` - Get answer
- `GET /chat/save` - Load sample documents

**chatService.java:**
- `getAnswer(question, userId)` - RAG pipeline
- `saveData(topics)` - Load documents to vector store

**dataStore.java:**
- 50 Java/Spring educational topics
- Used for sample data loading

**system_message.st:**
- LLM system prompt constraining behavior
- Forces LLM to use only provided documents

---

## 7. How RAG Works

### Phase 1: Document Loading

**Step 1:** Call `GET /chat/save`  
**Step 2:** Convert 50 topics to Document objects  
**Step 3:** For each document:
- Extract text
- Generate embedding (768D vector) via Ollama
- Store in MariaDB tables

### Phase 2: Similarity Search

**Step 1:** User asks question: "What is encapsulation?"  
**Step 2:** Build SearchRequest (topK=5, threshold=0.6)  
**Step 3:** Search process:
- Convert question to 768D vector
- Search MariaDB for top 5 similar vectors
- Calculate similarity = 1 / (1 + distance)
- Filter by threshold (≥ 0.6)

### Phase 3: Prompt Building

**Step 1:** Extract text from 5 retrieved documents  
**Step 2:** Combine into context string  
**Step 3:** Load system prompt from file  
**Step 4:** Build final prompt:
```
System: You are a RAG assistant. Use ONLY the information...
Document: [combined context from 5 documents]

User: What is encapsulation?
[Previous messages from conversation history]
```

### Phase 4: LLM Processing

**Step 1:** Send prompt to Ollama via ChatClient  
**Step 2:** Advisors process:
- MessageChatMemoryAdvisor adds conversation history
- SimpleLoggerAdvisor logs for debugging
**Step 3:** qwen2.5:1.5b model:
- Reads system prompt → understand constraints
- Reads documents → extract relevant info
- Generates answer based ONLY on documents

### Phase 5: Return Answer

**Step 1:** Response stored in ChatMemory (for next turn)  
**Step 2:** Return to client  

---

## 8. Service Layer Methods

### `getAnswer(String question, String userId)`

```java
@Override
public String getAnswer(String question, String userId) {
    // 1. Build search request
    SearchRequest searchRequest = SearchRequest.builder()
        .query(question)
        .topK(5)
        .similarityThreshold(0.6)
        .build();
    
    // 2. Search vector store
    List<Document> documents = vectorStore.similaritySearch(searchRequest);
    
    // 3. Handle no results
    if (documents.isEmpty()) {
        return "No relevant information found.";
    }
    
    // 4. Extract context
    List<String> list = documents.stream()
        .map(Document::getText)
        .toList();
    String context = String.join(",", list);
    
    // 5. Call LLM with advisors, system prompt, and context
    String answer = chatClient.prompt(question)
        .advisors(advisorSpec -> advisorSpec.param(
            ChatMemory.CONVERSATION_ID, userId))
        .system(s -> s.text(systemMessage)
                      .param("document", context))
        .call()
        .content();
    
    return answer;
}
```

**Purpose:** Core RAG pipeline  
**Input:** question, userId  
**Output:** Answer string  

### `saveData(List<String> list)`

```java
@Override
public String saveData(List<String> list) {
    // 1. Convert strings to Documents
    List<Document> documents = list.stream()
        .map(Document::new)
        .toList();
    
    // 2. Add to vector store (auto-generates embeddings)
    vectorStore.add(documents);
    
    return "Data saved successfully!";
}
```

**Purpose:** Load documents and generate embeddings  
**Input:** List of topic strings  
**Output:** Status message  

---

## 9. Spring AI Concepts Used

### SearchRequest

```java
SearchRequest.builder()
    .query(question)              // Text to embed
    .topK(5)                      // Max results
    .similarityThreshold(0.6)     // Min similarity
    .build()
```

**What:** Configuration for similarity search  
**Why:** Separates search config from execution

### ChatClient

```java
chatClient.prompt(question)
    .system(s -> s.text(...).param("document", context))
    .advisors(advisorSpec -> advisorSpec.param(...))
    .call()
    .content()
```

**What:** Fluent API for LLM interaction  
**Why:** Clean, chainable, composable  

### Document

```java
new Document("Text content...", metadata)
```

**What:** Text + metadata container  
**Why:** Abstracts document representation  

### VectorStore

```java
vectorStore.add(documents);
vectorStore.similaritySearch(searchRequest);
```

**What:** Vector database abstraction  
**Why:** Switch implementations with config only  

### ChatMemory

Stores conversation history per userId with max 20 messages  
**Why:** Multi-turn conversations  

### Advisors

- **MessageChatMemoryAdvisor:** Adds conversation history
- **SimpleLoggerAdvisor:** Logs requests/responses

**Why:** Composable cross-cutting concerns  

### System Prompts

```
You are a RAG assistant.
Use ONLY the information in the document.
Do not use your own knowledge.

Document:
{document}
```

**Why:** Constrain LLM behavior, prevent hallucination  

---

## 10. Vector Database (MariaDB)

### What is a Vector Database?

Traditional DBs store text/numbers. Vector DBs store high-dimensional vectors representing semantic meaning.

### Why MariaDB?

| Reason | Benefit |
|--------|---------|
| Familiar SQL | Use standard queries |
| Cost-effective | No expensive specialized DBs |
| Spring AI support | Seamless integration |
| Hybrid queries | Vector + text search |

### How Vectors are Stored

```sql
CREATE TABLE vector_store (
    id VARCHAR(36) PRIMARY KEY,
    content LONGTEXT,           -- Document text
    created_at TIMESTAMP
);

CREATE TABLE vector_store_embeddings (
    id VARCHAR(36) PRIMARY KEY,
    vector_store_id VARCHAR(36),
    embedding VECTOR(768),      -- 768D embedding
    FOREIGN KEY (vector_store_id) REFERENCES vector_store(id)
);
```

### Similarity Search SQL

```sql
SELECT * FROM vector_store vs
JOIN vector_store_embeddings ve ON vs.id = ve.vector_store_id
WHERE 1/(1+(embedding <-> [0.051, -0.142, ...])) >= 0.6
ORDER BY embedding <-> [0.051, -0.142, ...]
LIMIT 5;
```

---

## 11. Ollama Integration

### Why Ollama?

| Aspect | Ollama | Cloud LLMs |
|--------|--------|-----------|
| Privacy | Local | Cloud |
| Cost | Free | Per token |
| Speed | ~1s | 1-5s |
| Offline | Yes | No |

### Models Used

**Chat Model:** `qwen2.5:1.5b` (1.5B parameters, lightweight)  
**Embedding Model:** `nomic-embed-text` (768D vectors)  

### How Spring AI Connects

```
spring.ai.ollama.base-url=http://localhost:11434
    ↓
OllamaChatModel bean created
    ↓
Ready to make requests
```

### Chat Request

```json
POST http://localhost:11434/api/chat
{
    "model": "qwen2.5:1.5b",
    "messages": [
        {"role": "system", "content": "You are a RAG..."},
        {"role": "user", "content": "What is encapsulation?"}
    ]
}
```

### Embedding Request

```json
POST http://localhost:11434/api/embeddings
{
    "model": "nomic-embed-text",
    "prompt": "Encapsulation protects object data..."
}
```

---

## 12. Similarity Search

### SearchRequest Parameters

```java
SearchRequest.builder()
    .query(question)              // "What is encapsulation?"
    .topK(5)                      // Return 5 docs
    .similarityThreshold(0.6)     // Min similarity 60%
    .build()
```

### Similarity Calculation

```
Distance = 0.2
Similarity = 1 / (1 + 0.2) = 0.833 (83.3%) ✅

Distance = 0.7
Similarity = 1 / (1 + 0.7) = 0.588 (58.8%) ❌ (below threshold)
```

### Retrieval Process

```
1. Embed question → 768D vector
2. Search MariaDB for top 5 similar vectors
3. Calculate similarity scores
4. Filter by threshold (≥ 0.6)
5. Return List<Document> sorted by similarity
```

---

## 13. VectorStore

### What is VectorStore?

Spring AI abstraction for vector database operations

### Main Methods

```java
vectorStore.add(documents);                    // Store + embed
vectorStore.similaritySearch(searchRequest);   // Search
```

### Flow

**add():**
```
For each document:
    Extract text
    Generate embedding (768D)
    Store in MariaDB
    Create indexes
```

**similaritySearch():**
```
Embed query
Search MariaDB (vector distance)
Calculate similarity scores
Filter by threshold
Return sorted Document list
```

---

## 14. Code Walkthrough

### Execution Path

```
1. RagApplication.main() starts
   ↓
2. Spring creates context, initializes beans
   ↓
3. AiConfig creates ChatClient, ChatMemory, etc.
   ↓
4. Application ready on port 8082
   ↓
5. User calls GET /chat/save
   ↓
6. Controller → chatService.saveData()
   ↓
7. For 50 topics:
   - Create Document
   - Generate embedding
   - Store in MariaDB
   ↓
8. User calls GET /chat?q=...&userId=...
   ↓
9. Controller → chatService.getAnswer()
   ↓
10. Build SearchRequest and search vector store
    ↓
11. Extract 5 documents, combine context
    ↓
12. Build prompt with system message
    ↓
13. ChatClient sends to Ollama
    ↓
14. Advisors add memory and logging
    ↓
15. LLM processes and generates answer
    ↓
16. Store in ChatMemory, return to client
```

---

## 15. API Endpoints

### Endpoint 1: Get Answer

```
GET /chat?q=What%20is%20encapsulation?&userId=user123
```

| Field | Value |
|-------|-------|
| HTTP Method | GET |
| Query Param | `q` = question |
| Header | `userId` = conversation ID |
| Response | Plain text string |

**Example:**
```bash
curl "http://localhost:8082/chat?q=What%20is%20Spring%20Boot?&userId=user1"
# Response: "Spring Boot simplifies Java development by providing..."
```

### Endpoint 2: Save Data

```
GET /chat/save
```

| Field | Value |
|-------|-------|
| HTTP Method | GET |
| Parameters | None |
| Response | "Data saved successfully!" |

**Example:**
```bash
curl http://localhost:8082/chat/save
```

---

## 16. How to Run

### Prerequisites
- Java 21
- Maven
- Docker
- 4GB+ RAM

### Step 1: Start MariaDB

```bash
docker run -d \
  --name mariadb-rag \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=Spring_Ai \
  -p 3308:3306 \
  mariadb:latest
```

### Step 2: Start Ollama

```bash
docker run -d \
  --name ollama-rag \
  -p 11434:11434 \
  ollama/ollama:latest
```

### Step 3: Pull Models

```bash
ollama pull qwen2.5:1.5b
ollama pull nomic-embed-text
```

### Step 4: Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

### Step 5: Test

```bash
# Load documents
curl http://localhost:8082/chat/save

# Ask question
curl "http://localhost:8082/chat?q=What%20is%20encapsulation?&userId=user1"
```

---

## 17. Learning Summary

### Concepts Mastered

✅ Spring AI ChatClient API  
✅ Vector embeddings and semantic search  
✅ Vector databases and similarity search  
✅ RAG pipeline implementation  
✅ Ollama local LLM integration  
✅ System prompts and prompt engineering  
✅ Chat memory for conversations  
✅ SearchRequest configuration  
✅ Advisors pattern (memory, logging)  
✅ MariaDB vector operations  

### Key Takeaways

1. **RAG Prevents Hallucination** - LLMs constrained by documents
2. **Spring AI Abstracts Complexity** - Switch providers with config
3. **Local LLMs Are Practical** - Ollama enables private deployment
4. **Vectors Enable Semantics** - Embeddings capture meaning
5. **Advisors Compose Concerns** - Clean separation of concerns
6. **System Prompts Control Behavior** - Constrain LLM outputs

---

## 18. Future Improvements

### 1. Persistent Chat Memory

**Current:** In-memory (lost on restart)  
**Improvement:** Database-backed storage
```java
.chatMemoryRepository(new JpaChatMemoryRepository())
```

### 2. Metadata Filtering

**Current:** Vector search only  
**Improvement:** Add metadata filters
```java
.filterExpression("category == 'Spring'")
```

### 3. Hybrid Search

**Current:** Vector-only  
**Improvement:** Combine vector + keyword search

### 4. PDF Document Loader

**Current:** Hardcoded topics  
**Improvement:** Load from PDFs
```java
DocumentReader reader = new TikaDocumentReader(resource);
```

### 5. Smart Chunking

**Current:** Full documents  
**Improvement:** Split into chunks
```java
TextSplitter splitter = new TokenTextSplitter(500, 0);
```

### 6. Streaming Responses

**Current:** Wait for full response  
**Improvement:** Stream tokens as generated

### 7. Multiple Embedding Models

**Current:** Single model  
**Improvement:** Ensemble multiple models

### 8. Production Deployment

**Current:** Development setup  
**Improvement:** Docker Compose, monitoring, scaling

---

## Summary

This project demonstrates a production-ready RAG system combining:
- Spring Boot framework
- Spring AI abstractions
- Local Ollama LLMs
- MariaDB vector database
- Similarity search
- System prompts
- Chat memory

All components work together to create a system that provides accurate, grounded answers based on your documents, not hallucinations.

**Happy Learning! 🚀**
