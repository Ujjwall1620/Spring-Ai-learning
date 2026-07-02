package com.example.RAG.Helper;

import java.util.List;

public class dataStore {
    public static List<String> getJavaSpringTopics() {
        return List.of(
                "Java is an object-oriented programming language that is platform-independent. It follows the principle of Write Once, Run Anywhere by compiling source code into bytecode executed by the JVM.",
                "The JVM (Java Virtual Machine) executes Java bytecode and provides platform independence. It also manages memory, garbage collection, and runtime optimizations.",
                "The JDK contains development tools such as the Java compiler and debugger, while the JRE contains only the libraries and JVM required to run Java applications.",
                "A class is a blueprint for creating objects, while an object is an instance of a class containing state and behavior.",
                "Encapsulation protects object data by keeping fields private and exposing them through getter and setter methods.",
                "Inheritance allows one class to acquire the properties and methods of another class, promoting code reuse.",
                "Polymorphism enables the same method call to behave differently depending on the object's runtime type.",
                "Abstraction hides implementation details and exposes only the essential features of an object.",
                "ArrayList stores elements in a dynamic array and provides fast random access but slower insertions and deletions in the middle.",
                "LinkedList stores elements as nodes connected through references, making insertions and deletions efficient but random access slower.",
                "HashMap stores key-value pairs and provides average O(1) lookup time using hashing.",
                "HashSet stores unique elements and internally uses a HashMap for storage.",
                "TreeMap stores key-value pairs in sorted order using a Red-Black Tree.",
                "PriorityQueue retrieves elements based on priority rather than insertion order.",
                "A thread is a lightweight process that allows multiple tasks to execute concurrently within a Java application.",
                "Synchronization prevents multiple threads from accessing shared resources simultaneously, avoiding race conditions.",
                "ExecutorService manages thread pools and simplifies concurrent task execution.",
                "The Stream API provides functional-style operations for processing collections using filter, map, reduce, and collect operations.",
                "The filter() method selects elements matching a condition from a stream.",
                "The map() method transforms each element of a stream into another object.",
                "The collect() method gathers stream elements into collections or custom results.",
                "Spring Boot simplifies Java application development by providing auto-configuration, embedded servers, and production-ready features.",
                "Dependency Injection is a core Spring feature that manages object creation and wiring through the IoC container.",
                "The @SpringBootApplication annotation combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.",
                "@RestController combines @Controller and @ResponseBody to create REST APIs.",
                "@Service marks a class as a business service managed by the Spring container.",
                "@Repository indicates that a class interacts with the database and enables exception translation.",
                "@Component is a generic stereotype annotation for Spring-managed beans.",
                "Spring Data JPA simplifies database operations by providing repository interfaces with built-in CRUD functionality.",
                "JpaRepository provides methods such as save(), findById(), findAll(), and deleteById().",
                "Hibernate is the default JPA implementation used by Spring Boot for ORM.",
                "Spring Security provides authentication, authorization, password encoding, and protection against common security vulnerabilities.",
                "JWT is a stateless authentication mechanism where user information is securely stored in a signed token.",
                "Microservices architecture divides an application into independently deployable services that communicate using REST APIs or messaging systems.",
                "Spring Cloud Gateway acts as an API Gateway that routes requests to different microservices.",
                "Service Discovery allows microservices to locate each other dynamically using a registry such as Eureka.",
                "Spring AI provides abstractions for integrating Large Language Models into Spring Boot applications.",
                "A ChatClient is used to send prompts to an AI model and receive generated responses.",
                "Embeddings convert text into numerical vectors that preserve semantic meaning for similarity search.",
                "A Vector Store stores embedding vectors and retrieves similar documents using similarity search.",
                "Retrieval-Augmented Generation combines vector search with LLMs to generate context-aware answers.",
                "A RAG pipeline consists of document ingestion, text splitting, embedding generation, vector storage, retrieval, and answer generation."
        );
    }
}
