# Spring AI Advisors & Chat Memory: A Conversational AI Application with Persistent Database Storage

## Project Overview

This project demonstrates a production-ready implementation of **Spring AI Advisors** and **Conversation Memory** management in a Spring Boot application. It showcases how to build intelligent chatbots that maintain conversation context, automatically manage chat history, and persist memory data to a relational database.

### Key Features Demonstrated:

- **Spring AI ChatClient** - High-level API for interacting with AI models
- **Advisors** - Interceptors that enhance AI requests and responses with cross-cutting concerns
- **Conversation Memory** - Automatic management of chat history and context
- **Persistent Chat Memory** - Database-backed memory that survives application restarts
- **JDBC-based ChatMemoryRepository** - Spring JDBC integration for storing conversations in MySQL
- **Google Gemini Integration** - Using cutting-edge Gemini AI models for intelligent responses
- **MySQL Persistence** - Production-grade relational database storage for chat history
- **Conversation Isolation** - Multiple independent conversations with automatic ID management

---

## Learning Objectives

Through this Chapter 4 project, you will learn:

1. **What Advisors are in Spring AI** - Decorators that wrap AI operations
2. **Why Advisors are important** - They enable cross-cutting concerns without cluttering business logic
3. **How Advisors intercept requests and responses** - The middleware pattern for AI operations
4. **How chat memory works** - Maintaining conversation context across multiple requests
5. **How conversation history is automatically managed** - MessageWindowChatMemory and its configuration
6. **How to persist memory in MySQL** - Using JdbcChatMemoryRepository for durable storage
7. **Difference between in-memory and database-backed memory** - Trade-offs between speed and durability
8. **Automatic schema initialization** - Spring AI's self-healing database setup with `initialize-schema=ALWAYS`
9. **Conversation ID patterns** - Tracking multiple independent conversations per user

---

## Understanding Advisors in Spring AI

### What is an Advisor?

An **Advisor** is a component in Spring AI that acts as an interceptor for AI operations. It wraps requests going to the Language Model (LM) and responses coming back from it. Think of it as middleware for AI calls.

```
User Request → Advisor Chain → LLM → Advisor Chain → Response to User
```

### Why Advisors Matter

Advisors solve several important problems:

1. **Separation of Concerns** - Keep memory management, logging, validation, and other concerns separate from business logic
2. **Reusability** - Write an advisor once, apply it everywhere
3. **Flexibility** - Add or remove advisors without changing application logic
4. **Composability** - Chain multiple advisors together for complex behavior
5. **Testing** - Mock advisors to test core logic in isolation

### Built-in Advisors in Spring AI

Spring AI provides several ready-to-use advisors:

- **MessageChatMemoryAdvisor** - Automatically loads previous messages before calling the LM and saves new messages after
- **SimpleLoggerAdvisor** - Logs requests and responses for debugging
- **Prompt-related Advisors** - Transform or validate prompts before sending
- **Response-related Advisors** - Post-process or validate model responses

### MessageChatMemoryAdvisor: How It Works

The `MessageChatMemoryAdvisor` is the core advisor for this project. Here's what it does:

**Before LM Call (Request Phase):**
1. Receives a conversation ID from the request
2. Queries the `ChatMemory` to fetch all previous messages in that conversation
3. Prepends these messages to the current user message
4. Sends the enriched prompt to the LM

**After LM Call (Response Phase):**
1. Receives the LM's response
2. Extracts the new assistant message
3. Stores both the user message and assistant response in `ChatMemory`
4. Returns the response to the application

### Benefits Over Manual Memory Management

Without advisors, you would write code like:

```java
// ❌ WITHOUT ADVISOR: Manual memory management
String conversationId = "user-123";
List<Message> history = database.getMessages(conversationId);
String prompt = buildPrompt(history, userMessage);
String response = llm.call(prompt);
database.save(new Message(conversationId, "user", userMessage));
database.save(new Message(conversationId, "assistant", response));
```

With advisors, it becomes declarative:

```java
// ✅ WITH ADVISOR: Declarative, automatic memory management
geminiChatClient.prompt()
    .user(message)
    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
    .call()
    .content();
```

---

## Chat Memory: The Foundation of Context

### What is ChatMemory?

`ChatMemory` is an abstraction that handles storing and retrieving conversation history. It allows the AI system to understand context across multiple messages and maintain coherent conversations.

### Why Chat Memory is Essential

Without memory:
- Each request is isolated; the LM treats every message as a new conversation
- Users must repeat context in every message
- The AI cannot learn from conversation flow
- Real dialogue becomes impossible

### Conversation IDs

Every conversation is identified by a unique **Conversation ID**. This allows:

- Separating conversations between different users
- Tracking multiple concurrent conversations with the same user
- Isolating chat history for privacy and organization

In this project, conversation IDs follow the pattern: `"conversation-" + userId`

### Memory Windows with MessageWindowChatMemory

`MessageWindowChatMemory` is a windowed memory implementation that keeps only the most recent N messages:

```
Total messages in database: 100
Window size (maxMessages): 10
Messages sent to LM: Last 10 messages
Benefit: Reduces token costs and keeps prompts focused on recent context
```

**Configuration in this project:**

```yaml
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
```

The `ChatMemory` bean is configured with:

```java
@Bean
public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(jdbcChatMemoryRepository)
        .maxMessages(10)  // Keep only last 10 messages in each conversation
        .build();
}
```

### How Previous Messages Are Injected

When the LM receives a prompt:

```
User: "What was I talking about earlier?"

Before:
- Assistant loads last 10 messages from database
- Prepends them to the current message
- Sends: "Previous context [messages 1-10] + User: What was I talking about earlier?"
```

---

## ChatMemoryRepository: Persistence Layer

### What is ChatMemoryRepository?

`ChatMemoryRepository` is the abstraction responsible for storing and retrieving conversation history. It defines the contract for how messages are persisted and accessed.

### Available Implementations

This project provides two implementations, each with different trade-offs:

#### InMemoryChatMemoryRepository

**How It Works:**
- Uses `ConcurrentHashMap` internally to store messages in application memory
- No database calls; data is kept in RAM
- Fast access; minimal latency

**Characteristics:**
- ✅ Extremely fast (no I/O)
- ✅ Simple setup; no database required
- ✅ Perfect for development and testing
- ❌ Data is lost when the application stops
- ❌ Not suitable for production
- ❌ Limited to single application instance

**Use Case:** Local development, testing, proof-of-concepts

#### JdbcChatMemoryRepository

**How It Works:**
- Uses Spring JDBC to persist messages in a relational database (MySQL)
- Every message is written to and read from the database
- Memory survives application restarts
- Multiple instances can share the same conversation history

**Characteristics:**
- ✅ Durable; data persists across restarts
- ✅ Suitable for production applications
- ✅ Supports multiple application instances
- ✅ Database becomes the single source of truth
- ❌ Slightly higher latency due to I/O operations
- ❌ Requires database setup and maintenance

**Use Case:** Production deployments, multi-instance setups, critical applications

### Comparison: InMemory vs JDBC

| Feature | InMemoryChatMemoryRepository | JdbcChatMemoryRepository |
|---------|------------------------------|--------------------------|
| **Storage** | Application RAM (ConcurrentHashMap) | MySQL Database |
| **Data Durability** | Lost on restart | Persists indefinitely |
| **Performance** | Very fast (milliseconds) | Slightly slower (network I/O) |
| **Scalability** | Single instance only | Multiple instances supported |
| **Production Ready** | ❌ No | ✅ Yes |
| **Setup Complexity** | Minimal | Requires MySQL + configuration |
| **Multi-user Support** | Limited | Full support with isolation |
| **Database Required** | No | Yes |
| **Cost** | None | Hosting/infrastructure |

---

## JDBC Chat Memory Configuration: In-Depth Guide

### The Critical Configuration Property

The most important configuration for this project is:

```properties
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
```

### What This Property Does

This property instructs Spring AI to **automatically create the required database tables for storing chat messages** on application startup.

When set to `ALWAYS`, Spring AI:

1. Checks if the chat memory tables exist in the database
2. If they don't exist, automatically creates them with the correct schema
3. If they already exist, skips creation (no errors, idempotent)
4. Tables include: conversation ID, message role (user/assistant), message content, timestamp, etc.

### Why This Property is Important

#### Without This Property (or set to NEVER):

```
❌ Application starts
❌ First request comes in
❌ Error: "Table 'Spring-Ai.spring_ai_chat_message' doesn't exist"
❌ Chat functionality breaks
❌ Manual database setup required
```

#### With `initialize-schema=ALWAYS`:

```
✅ Application starts
✅ Spring AI automatically creates tables if needed
✅ Database is ready to use immediately
✅ No manual SQL scripts needed
✅ Developer can focus on business logic
```

### Automatic Schema Creation

Spring AI creates tables with this structure (approximately):

```sql
CREATE TABLE spring_ai_chat_message (
    conversation_id VARCHAR(255) NOT NULL,
    message_id VARCHAR(255) PRIMARY KEY,
    message_type VARCHAR(20),  -- 'user', 'assistant', etc.
    content LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Property Values Explained

| Value | Behavior | Use Case |
|-------|----------|----------|
| **ALWAYS** | Create schema on every startup | Development, testing, safe default |
| **NEVER** | Never create schema (explicit opt-out) | Production with pre-existing schema |
| **IF_MISSING** | Create only if schema doesn't exist | Some Spring AI versions |

### What Happens If Property is Removed

```properties
# ❌ If you remove or comment out the property:
# spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
```

**Result:**
- Spring AI defaults to `NEVER` or similar behavior
- Tables won't be created automatically
- Application will fail with database errors
- Users must manually run SQL DDL scripts

### In This Project's Application Properties

```properties
# File: src/main/resources/application.properties

# ============================
# Database Configuration
# ============================
spring.datasource.url=jdbc:mysql://localhost:3307/Spring-Ai?createDatabaseIfNotExist=true...
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ============================
# Chat Memory Configuration
# ============================
# This automatically creates chat memory tables on startup
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
```

### Development vs Production

**Development:**
```properties
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
# ✅ Safe; schemas recreated on each restart
# ✅ Convenient for testing
```

**Production:**
```properties
spring.ai.chat.memory.repository.jdbc.initialize-schema=NEVER
# ✅ Prevents accidental schema drops
# ✅ Explicit schema management via migrations
# ⚠️ Requires pre-created tables
```

---

## Project Architecture

The application follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    User / REST Client                        │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  ChatController                              │
│  Receives HTTP requests (/chat, /chat/Diff)                 │
│  Extracts conversation ID and message                        │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  ChatService (Interface)                     │
│  Defines contract for chat operations                        │
│  Methods: chat(), DiffChat()                                 │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│              ChatServiceImpl (Implementation)                  │
│  Business logic for chat processing                          │
│  Builds prompts, calls ChatClient                            │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                ChatClient (geminiChatClient)                 │
│  High-level API for LM interaction                           │
│  Registered with MessageChatMemoryAdvisor                    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│            MessageChatMemoryAdvisor                          │
│  Intercepts requests and responses                           │
│  Manages conversation context                               │
│  Coordinates with ChatMemory                                │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│               ChatMemory (MessageWindow)                     │
│  Maintains message window (last 10 messages)                │
│  Interfaces with ChatMemoryRepository                        │
│  Enforces memory limits                                      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│        JdbcChatMemoryRepository                              │
│  Persists messages to database                               │
│  Retrieves message history by conversation ID                │
│  Uses Spring JDBC for database access                        │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────┐
│                   MySQL Database                          │
│  Tables: spring_ai_chat_message, conversation history     │
└────────────────────────┬──────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────┐
│              Google Gemini API                            │
│  Processes prompts and generates responses                │
│  Model: gemini-2.5-flash                                  │
└───────────────────────────────────────────────────────────┘
```

### Layer Descriptions

1. **REST Controller Layer** - HTTP endpoint handlers
2. **Service Layer** - Business logic and orchestration
3. **ChatClient Layer** - Spring AI's high-level abstraction for LMs
4. **Advisor Layer** - Cross-cutting concerns (memory, logging)
5. **Memory Layer** - In-memory message windowing
6. **Repository Layer** - Data persistence (JDBC)
7. **Database Layer** - MySQL storage
8. **LM API Layer** - External Google Gemini service

---

## Complete Request/Response Flow

This diagram shows exactly what happens when a user sends a message:

```
┌─ USER REQUEST ─────────────────────────────────────────┐
│ GET /chat?q="Tell me a joke"&sessionId=xyz             │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ ChatController.chat()                                  │
│ • Receives message: "Tell me a joke"                  │
│ • Extracts conversationId from HttpSession             │
│ • Calls chatService.chat(conversationId, message)     │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ ChatService.chat()                                     │
│ • Builds: geminiChatClient.prompt()                   │
│ • Sets: .user("Tell me a joke")                       │
│ • Adds advisor param: ConversationID = "conv-xyz"     │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ MessageChatMemoryAdvisor (BEFORE REQUEST)              │
│ • Receives conversationId = "conv-xyz"                │
│ • Queries ChatMemory: "Get all messages for conv-xyz" │
│ • Database retrieves: [message1, message2, ...]      │
│ • Passes last 10 messages to window                    │
│ • Prepends history to current message                 │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ Enriched Prompt Sent to Gemini                         │
│ [Context: Previous messages (1-10)]                   │
│ [New User Message: "Tell me a joke"]                  │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ Google Gemini API                                      │
│ • Processes enriched prompt                           │
│ • Generates: "Why did the AI cross the road?..."     │
│ • Returns response                                     │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ MessageChatMemoryAdvisor (AFTER RESPONSE)              │
│ • Receives response from Gemini                       │
│ • Creates Message: (role="assistant", content="...")  │
│ • Stores in ChatMemory                                │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ JdbcChatMemoryRepository                               │
│ • Persists user message to database                   │
│ • Persists assistant message to database              │
│ • Inserts: (conversationId, role, content, timestamp) │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ MySQL Database                                         │
│ UPDATE spring_ai_chat_message SET ... WHERE ...        │
│ INSERT INTO spring_ai_chat_message (...)              │
└─────────────────────┬─────────────────────────────────┘
                      │
┌─────────────────────▼─────────────────────────────────┐
│ Response Returned to User                              │
│ HTTP 200 OK                                            │
│ Body: "Why did the AI cross the road? To chat with    │
│        the other side!"                                │
└────────────────────────────────────────────────────────┘

NEXT REQUEST (Same User):
• Advisor automatically loads messages 1-10 from database
• Conversation context is maintained
• User doesn't need to repeat context
```

### Key Flow Points

1. **Conversation ID Extraction** - Every request gets a unique conversation context
2. **Automatic Context Loading** - Previous messages loaded without explicit queries
3. **Prompt Enrichment** - Historical messages prepended to new request
4. **LM Processing** - Gemini sees full context and generates informed response
5. **Dual Message Storage** - Both user query and assistant response stored
6. **Persistence** - All messages survive application restarts

---

## Technologies Used

### Core Framework
- **Java 21** - Latest Java LTS version with modern features
- **Spring Boot 4.1.0** - Enterprise-grade application framework
- **Spring Framework 6.x** - Underlying dependency injection and core features

### AI & LLM
- **Spring AI 2.0.0** - Abstraction layer for LLM operations
- **Google Gemini 2.5 Flash** - State-of-the-art large language model
- **ChatClient** - High-level Spring AI API for model interaction

### Database & Persistence
- **MySQL 8.0.33** - Relational database for chat history storage
- **MySQL JDBC Driver** - Database connectivity
- **Spring JDBC** - Database access layer for query execution
- **Connection Pooling** - Built-in through Spring Boot

### Development & Build
- **Maven** - Build automation and dependency management
- **Apache Maven Compiler Plugin** - Java compilation
- **Spring Boot Maven Plugin** - Application packaging and execution

### Utilities & Logging
- **Lombok** - Boilerplate reduction (@Slf4j, @RequiredArgsConstructor)
- **SLF4J** - Logging facade for application insights

### Testing
- **Spring Boot Test** - Testing framework with WebMvc support

---

## Project Structure & Components

```
src/main/java/com/example/LLMmemory/
├── Controller/
│   └── ChatController.java          HTTP endpoints for chat operations
├── Service/
│   ├── chatSerivceImpl.java          Service interface contract
│   └── chatService.java             Service implementation
├── Config/
│   └── AiConfig.java                Spring AI configuration and beans
└── LlMmemoryApplication.java        Spring Boot entry point

src/main/resources/
├── application.properties            Configuration for database, API keys, etc.
```

### Key Classes

#### `ChatController` (REST Endpoints)
```java
@RestController
@RequestMapping("/chat")
public class ChatController {
    // GET /chat?q=message
    // Extracts HttpSession ID as conversation ID
    // Routes to chatService
    
    // GET /chat/Diff?q=message&userID=custom-id
    // Accepts custom user ID from request header
    // Enables multi-user conversations
}
```

**Endpoints:**

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/chat` | GET | Chat with session-based conversation ID |
| `/chat/Diff` | GET | Chat with custom user ID (header-based) |

#### `chatService` (Business Logic)
```java
@Service
public class chatService implements chatSerivceImpl {
    private final ChatClient geminiChatClient;
    
    // Uses ChatClient with MessageChatMemoryAdvisor
    // Manages conversation IDs and prompt building
    // Supports both standard and custom conversation modes
}
```

**Methods:**

1. `chat(userId, message)` - Standard chat with automatic conversation management
2. `DiffChat(userId, message)` - Alternative chat with SimpleLoggerAdvisor for debugging

#### `AiConfig` (Spring Configuration)
```java
@Configuration
@Slf4j
public class AiConfig {
    
    // 1. ChatMemory Bean
    // Creates MessageWindowChatMemory with JdbcChatMemoryRepository
    // Configured with maxMessages=10 (keep last 10 messages)
    
    // 2. ChatClient Bean (geminiChatClient)
    // Builds ChatClient with Gemini model
    // Registers MessageChatMemoryAdvisor as default
    // Sets temperature=0.7 for balanced creativity
    // Limits output to 2000 tokens
}
```

#### `application.properties` (Configuration)
```properties
# Gemini API Configuration
spring.ai.google.genai.api-key=${Gemini_API_KEY}
spring.ai.google.genai.chat.model=gemini-2.5-flash
spring.ai.google.genai.chat.temperature=0.5

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3307/Spring-Ai?...
spring.datasource.username=root
spring.datasource.password=root

# Chat Memory Configuration
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
```

---

## Key Learnings & Takeaways

### 1. Advisors Pattern
Advisors implement the **Middleware/Interceptor** pattern for AI operations, enabling:
- Separation of cross-cutting concerns from business logic
- Composable, reusable components
- Clean, testable code

### 2. Chat Memory Management
Automatic memory management through Spring AI eliminates boilerplate:
- No manual loading/saving of conversation history
- Declarative configuration via annotations
- Production-ready persistence

### 3. MessageWindowChatMemory
Windowed memory keeps conversations focused:
- Limits context size for cost efficiency
- Prevents prompt bloat
- Maintains conversation coherence

### 4. JdbcChatMemoryRepository
Database-backed persistence provides:
- Durability across restarts
- Multi-instance support
- Production reliability

### 5. Automatic Schema Initialization
`initialize-schema=ALWAYS` demonstrates:
- Framework-assisted infrastructure setup
- Reduced DevOps burden
- Rapid development cycles

### 6. Conversation Isolation
Conversation IDs provide:
- Multi-user support
- Independent contexts per conversation
- Privacy and data isolation

### 7. Advisor Composition
Chaining advisors (MessageChatMemoryAdvisor + SimpleLoggerAdvisor) shows:
- Multiple concerns in single request
- Layered transparency for debugging
- Modular cross-cutting logic

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Google Gemini API Key

### Setup

1. **Clone Repository**
```bash
git clone <repository-url>
cd chapter-4
```

2. **Configure Environment**
```bash
export Gemini_API_KEY=your-actual-api-key
```

3. **Update Database Configuration** (if needed)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://your-host:3306/your-db
spring.datasource.username=your-username
spring.datasource.password=your-password
```

4. **Build Project**
```bash
mvn clean install
```

5. **Run Application**
```bash
mvn spring-boot:run
```

Application starts on: `http://localhost:8081`

### Test the Application

**Basic Chat Request:**
```bash
curl "http://localhost:8081/chat?q=Hello%20Gemini"
```

**Custom User ID Chat:**
```bash
curl "http://localhost:8081/chat/Diff?q=Tell%20me%20a%20joke" \
  -H "userID: user-123"
```

### Database Schema
Spring AI automatically creates the required tables when `initialize-schema=ALWAYS` is set. No manual SQL scripts needed!

---

## Spring AI Memory Architecture

```
┌─────────────────────────────────────────────┐
│          Application Code                   │
│  geminiChatClient.prompt()...               │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│    MessageChatMemoryAdvisor                 │
│  (Spring AI Built-in Advisor)               │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│    ChatMemory Interface                     │
│  • addMessage()                             │
│  • getMessages(conversationId)              │
│  • clear()                                  │
└────────────────┬────────────────────────────┘
                 │
    ┌────────────┴────────────┐
    │                         │
┌───▼──────────────────┐  ┌──▼─────────────────┐
│InMemoryChatMemory    │  │JdbcChatMemory      │
│Repository            │  │Repository          │
│(ConcurrentHashMap)   │  │(Spring JDBC)       │
└──────────────────────┘  └──────────┬─────────┘
                                     │
                          ┌──────────▼───────────┐
                          │  MySQL Database      │
                          │ spring_ai_chat_msg   │
                          └──────────────────────┘
```

---

## Production Considerations

### Configuration Changes

**Development:**
```properties
spring.ai.chat.memory.repository.jdbc.initialize-schema=ALWAYS
logging.level.com.example.LLMmemory=DEBUG
spring.ai.google.genai.chat.temperature=0.5
```

**Production:**
```properties
spring.ai.chat.memory.repository.jdbc.initialize-schema=NEVER
logging.level.com.example.LLMmemory=INFO
spring.ai.google.genai.chat.temperature=0.3  # More deterministic
spring.ai.google.genai.chat.options.max-output-tokens=2000
```

### Database Optimization
- Add indexes on `conversation_id` column
- Implement message retention policies (archive old messages)
- Configure connection pooling parameters
- Use read replicas for high-traffic scenarios

### API Key Management
- Use environment variables or vault systems (never commit keys)
- Implement API key rotation
- Monitor usage and costs

### Monitoring & Observability
- Log all API calls and responses
- Monitor database query performance
- Track conversation metrics
- Set up alerts for errors and anomalies

---

## Conclusion

This Chapter 4 project demonstrates how to build production-ready conversational AI applications using Spring AI. By leveraging Advisors, Chat Memory, and persistent storage, you can create intelligent systems that maintain context, scale reliably, and provide seamless user experiences.

The key insight is that Spring AI abstracts away much of the complexity around memory management and advisor coordination, allowing developers to focus on business logic rather than infrastructure concerns.

---

## Resources & References

- [Spring AI Official Documentation](https://spring.io/projects/spring-ai)
- [Google Gemini API Documentation](https://ai.google.dev/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Spring JDBC Documentation](https://spring.io/projects/spring-framework)

---

**Author:** Ujjwal  
**Learning Project:** Chapter 4 - Spring AI Advisors & Chat Memory  
**Version:** 1.0  
**Last Updated:** 2026-06-30
