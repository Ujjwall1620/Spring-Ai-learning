# Spring Boot Multi-LLM Integration

## Overview

This project shows how to integrate multiple large language models (LLMs) in a single Spring Boot application. Each provider is configured as its own `ChatClient` bean, enabling the application to use different model endpoints such as Google Gemini and Ollama side-by-side.

## What this project demonstrates

- Defining a dedicated `ChatClient` bean for each LLM provider
- Naming beans clearly for injection and runtime selection
- Injecting provider-specific clients using `@Qualifier`
- Supporting flexible routing strategies through factory or router components

## Architecture

- `ChatClientConfig` contains provider-specific bean definitions
- Controllers or services receive the correct client via `@Qualifier`
- Runtime selection can be handled in the controller, in a routing service, or using a factory map

## Core concepts

- Each LLM provider gets its own Spring bean
- Beans should use distinct names such as `geminiChatClient` and `ollamaChatClient`
- The application should avoid storing request-specific state inside shared clients
- Remote calls to LLM providers should include timeout and retry handling

## Example configuration

Create a configuration class that registers one `ChatClient` per provider:

```java
@Bean
public ChatClient geminiChatClient() {
    // configure GoogleGenAiChatModel and return ChatClient
}

@Bean
public ChatClient ollamaChatClient() {
    // configure OllamaChatModel and return ChatClient
}
```

## Example controller usage

Use `@Qualifier` to inject the correct provider client:

```java
@Autowired
@Qualifier("geminiChatClient")
private ChatClient geminiClient;

@Autowired
@Qualifier("ollamaChatClient")
private ChatClient ollamaClient;

@GetMapping("/chat/gemini-chat")
public String geminiChat(@RequestParam String q) {
    return geminiClient.sendMessage(q).getContent();
}

@GetMapping("/chat/ollama-chat")
public String ollamaChat(@RequestParam String q) {
    return ollamaClient.sendMessage(q).getContent();
}
```

## Adding a new LLM provider

1. Add the provider dependency and any required configuration properties.
2. Register a new `ChatClient` bean in `ChatClientConfig`.
3. Inject the new bean with `@Qualifier`, or add it to a runtime router/factory.

## Runtime selection strategies

- Controller-level routing: choose the provider based on a request parameter.
- Router or factory bean: map provider names to `ChatClient` beans and resolve dynamically.
- Load balancing: implement request distribution if multiple backends exist for the same provider.

## Configuration and security

- Store API keys in `application.properties`, `application.yml`, or environment variables.
- Never hard-code secrets in source code.
- For production, consider Spring Cloud Config, Vault, or another secure secret store.

## Testing guidance

- Unit tests: mock `ChatClient` beans using a test configuration.
- Integration tests: use sandbox endpoints or HTTP recording/mocking tools.
- Verify that each provider route resolves the intended bean and that fallback/error handling works.

## Notes

- Spring beans are singletons by default. Avoid storing per-request data inside shared clients.
- Add robust error handling and remote-call safeguards around LLM invocations.
- Keep provider-specific implementation details isolated from application logic.

## Next steps

For a more complete README, include actual code snippets from `ChatClientConfig.java`, `chatController.java`, and sample configuration entries from `application.properties`.