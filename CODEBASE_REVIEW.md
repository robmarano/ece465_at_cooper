# Codebase Review: ECE 465 - Cloud Computing

## 1. Purpose & Overview
This repository serves as the codebase for the **Spring 2026 ECE 465 Independent Study on Cloud Computing**. 

*   **Goal**: To teach foundational distributed systems concepts, including multi-threaded programming, network communication (sockets), coordination, and potentially modern cloud technologies like Docker/Kubernetes.
*   **Context**: The project is structured as a series of Java implementations demonstrating these concepts, ranging from basic locking mechanisms to a distributed client-server application.

## 2. Design & Structure
The project follows a standard **Apache Maven** directory structure:

*   **Build System**: Maven (`pom.xml`).
*   **Source Code**: `src/main/java/edu/cooper/ece465`
    *   **Root**: Basic threading examples (Producer/Consumer, `CubbyHole`, `Drop`).
    *   `sockets`: A "Distributed Imaging" application implementing a custom protocol over TCP sockets (`DistributedImagingServer`, `Client`, `Protocol`, `Thread`).
    *   `concurrent`: Advanced concurrency examples (`BlockingQueue`, `ThreadPool`, `WaitNotify`).
    *   `gson`: Examples exploring JSON serialization.
*   **Documentation**:
    *   `docs/`: Contains course notes (`ece465-notes.md`) and architecture blueprints.
    *   `README.md`: Course syllabus and logistics.

## 3. Implementation Details
The current implementation reflects an older Java educational style:

*   **Java Version**: The project is configured for **Java 1.7** (Source/Target in `pom.xml`). This is significantly outdated (End of Life was 2015).
*   **Dependencies**:
    *   `junit` **3.8.1** (Legacy version, pre-annotations).
    *   `log4j` **1.2.16** (End of Life, contains security vulnerabilities).
    *   `commons-imaging` (Snapshot version).
*   **Concurrency Model**:
    *   The `DistributedImagingServer` uses a **Thread-per-Request** model (`new DistributedImagingThread(socket).start()`). While simple, this does not scale to high concurrency.
    *   Direct usage of the `Thread` class (e.g., `Runner extends Thread`) is prevalent, whereas modern Java prefers implementing `Runnable` and using `ExecutorService`.

## 4. Recommended Enhancements
To align this course with modern Cloud Computing standards, the following improvements are strongly recommended:

### A. Modernization (Critical)
1.  **Upgrade JDK**: Bump to **Java 21 (LTS)**. This unlocks modern features like Virtual Threads (Project Loom), which are highly relevant for a Cloud Computing course as they solve the scalability issues of the "Thread-per-Request" model.
2.  **library Upgrades**:
    *   **JUnit**: Migrate to **JUnit 5** (Jupiter) for modern testing features.
    *   **Logging**: Switch to **SLF4J** with **Logback** or **Log4j2**, removing the vulnerable Log4j 1.x dependency.
    *   **Maven Plugins**: Update `maven-compiler-plugin`, `maven-surefire-plugin`, etc., to recent versions.

### B. Architecture & Code Quality
1.  **Executor Services**: Refactor manual `new Thread().start()` calls to use `java.util.concurrent.ExecutorService` or `CompletableFutures`.
2.  **Virtual Threads**: If upgrading to Java 21, refactor the Server to use Virtual Threads to demonstrate high-throughput I/O handling on a single machine.
3.  **Project Structure**:
    *   Move "test" classes (like `ProducerConsumerTest.java`) from `src/main/java` to `src/test/java` and convert them to proper JUnit test cases.
    *   Standardize package naming (currently mixes `distrComputingJourney` class with package names).

### C. Repository Hygiene
1.  **Cleanup**: The `SUPPORT.md` file appears to be a generic GitHub Pages theme template and should be removed or updated to be relevant.
2.  **Building**: Add a `Dockerfile` to demonstrate containerization, a key topic listed in the syllabus.

## 5. Potential "Cloud" Features
To bridge the gap between "Distributed Systems" and "Cloud Computing":
1.  **Containerization**: Containerize the `DistributedImagingServer` using Docker.
2.  **Orchestration**: Create Kubernetes manifests to deploy the server and multiple clients.
3.  **Observability**: Integrate OpenTelemetry or Prometheus metrics to monitor the thread pool usage or request latency.
