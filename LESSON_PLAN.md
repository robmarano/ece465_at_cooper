# ECE 465: Distributed Systems Engineering - Lesson Plan

**Textbook Reference**: *Distributed Systems* by Maarten van Steen and Andrew S. Tanenbaum (v4.0.3x)

This lesson plan integrates the theoretical foundations of distributed systems with the practical engineering exercises assimilated from the course archives.

---

## Part I: Foundations (Chapters 1-3)

### Session 01: The Distributed Transition
**Objective**: Understand the shift from centralized uniprocessor systems to distributed architectures. The "Hello World" of distribution.
*   **Theory**:
    *   Definition of a Distributed System (Tanenbaum Ch. 1.1).
    *   Goals: Resource Sharing, Openness, Concurrency.
    *   **Patterson & Hennessy Connection**: Why single-core scaling stopped (Power Wall) and why we scaled out.
*   **Exercise**: `edu.cooper.ece465.session01`
    *   Basic process-level concurrency vs. thread-level concurrency.

### Session 02: Architectures & Processes
**Objective**: Exploring how software components are organized and executed.
*   **Theory**:
    *   Architectural Styles: Layered, Object-based, Resource-centered (REST), Event-based (Tanenbaum Ch. 2).
    *   Threads vs. Virtualization (Tanenbaum Ch. 3).
*   **Exercise**: `edu.cooper.ece465.session02`
    *   Multithreaded Server implementation.

### Session 03: Communication & RPC
**Objective**: Moving beyond raw sockets to structured communication.
*   **Theory**:
    *   Remote Procedure Calls (RPC) vs. Message Oriented Middleware (MOM).
    *   The "Channel" Abstraction.
*   **Exercise**: `edu.cooper.ece465.session03`
    *   Implementing a proto-RPC mechanism or sophisticated chat.

### Session 04: Naming & Coordination
**Objective**: How distributed entities find each other and agree on values.
*   **Theory**:
    *   Flat vs. Structured Naming (Tanenbaum Ch. 4).
    *   Clock Synchronization (Lamport timestamps) & Mutual Exclusion (Tanenbaum Ch. 5).
*   **Exercise**: `edu.cooper.ece465.session04`
    *   Implementation of a logical clock or distributed lock.

### Session 04b: Naming & Service Discovery (The "DNS-Lite" Module)
**Objective**: Demystifying how distributed entities find each other.
*   **Rationale**:
    *   *Hardware Grounding*: Resolution incurs network latency.
    *   *Tradeoff*: Centralized Registry = Simple but Single Point of Failure.
*   **Implementation Plan**:
    1.  **Service Registry**: A centralized `NamingServer` (HashMap over Socket) to register `(Name -> IP:Port)`.
    2.  **Client-Side Exploration**: `DnsInspector` to query real-world DNS records.
    3.  **Integration**: `DistributedImagingServer` registers itself; `DistributedImagingClient` looks it up.
*   **Exercise**: `edu.cooper.ece465.session04.naming`
    *   Build the Registry, Register a mock service, Resolve it.

### Session 05: Data Formats & Heterogeneity
**Objective**: Handling data across different architectures (Endianness, languages).
*   **Theory**:
    *   Marshaling/Unmarshaling.
    *   JSON vs. Protocol Buffers vs. XML.
*   **Exercise**: `edu.cooper.ece465.session05`
    *   **The "Mini-Gson" Challenge**: Implement a `MiniJsonSerializer` (`edu.cooper.ece465.session05.reflection`) using Java Reflection to understand how serializers inspect objects at runtime.
    *   Compare output with Google Gson.

### Session 06: Containerization (Docker)
**Objective**: The unit of deployment in cloud computing.
*   **Theory**:
    *   Processes vs. Containers vs. VMs (Tanenbaum Ch. 3).
    *   Isolation (Namespaces & Cgroups).
*   **Exercise**: `Dockerfile` integration
    *   Containerizing the Java apps.

## Part II: Cloud Infrastructure (Chapters 6-9)

### Session 07-09: Cloud Compute & Storage (AWS)
**Objective**: Moving from local clusters to hyperscale public cloud.
*   **Theory**:
    *   Virtualization (EC2) vs. Serverless (Lambda).
    *   Object Storage (S3) implementation of consistency.
*   **Exercise**: `aws/`
    *   `lab-1`, `lab-2`: VPC and EC2 provisioning.
    *   `storage`: S3 interaction via CLI and Python.
    *   `compute`: Lambda function deployment.

### Session 10-12: Advanced Cloud Networking & Security
**Objective**: Connecting distributed components securely.
*   **Theory**:
    *   Software Defined Networking (VPC).
    *   Security: Authentication vs. Authorization (IAM).
*   **Exercise**: `aws/networking`
    *   API Gateway integration.

### Session 13: Full Stack Distribution (Final Project)
**Objective**: Integrating frontend and backend in a distributed manner.
*   **Theory**:
    *   Three-tier architecture.
    *   Event-driven architecture.
*   **Exercise**: `apps/amplifyapp`
    *   A React-based frontend connected to AWS Amplify.

---
**Note to Student**: This lesson plan bridges the gap between the theoretical "van Steen & Tanenbaum" concepts and the practical "AWS/Java/Docker" implementations found in this repository.

