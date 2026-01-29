# ECE 465: Distributed Systems Engineering - Lesson Plan

**Textbook Reference**: *Distributed Systems* by Maarten van Steen and Andrew S. Tanenbaum (v4.0.3x)

This lesson plan integrates the theoretical foundations of distributed systems with the practical engineering exercises assimilated from the course archives.

---

## Part I: Foundations (Chapters 1-3)

### Session 01: The Distributed Transition
**Objective**: Understand the shift from centralized uniprocessor systems to distributed architectures. The "Hello World" of distribution.
*   **Textbook Theory (Ch. 1 & 3)**:
    *   **Definition**: A collection of independent computers that appears to its users as a single coherent system.
    *   **Motivation**: Resource sharing (printers, data), communication, and scalability.
    *   **Threads (Ch. 3.2)**: The basic unit of CPU utilization. In distributed systems, multithreading is crucial for servers to handle multiple blocking requests (like I/O) simultaneously without stalling the entire process.
*   **Exercise**: `edu.cooper.ece465.session01`
    *   **Goal**: Demonstrate the difference between *doing one thing at a time* (Single-threaded) and *doing many things at once* (Multi-threaded).
    *   **Code**: `Producer`, `Consumer`, `CubbyHole`. A classic synchronization problem illustrating shared state.

### Session 02: Architectures & Processes
**Objective**: Exploring how software components are organized and executed.
*   **Textbook Theory (Ch. 2 & 3)**:
    *   **Architectures (Ch. 2)**: How components are organized.
        *   *Layered*: Request flows down/Response flows up (e.g., OSI model).
        *   *Object-based*: Objects communicate via method invocations (RPC/RMI).
        *   *Resource-centered (REST)*: Operations on resources (GET/PUT/POST).
    *   **Clients & Servers (Ch. 3)**:
        *   *Clients*: Invoke operations.
        *   *Servers*: Iterate, wait for request, process, reply.
        *   *Stateful vs. Stateless*: Does the server remember previous requests? (Stateless is easier to scale).
*   **Exercise**: `edu.cooper.ece465.session02`
    *   **Goal**: Build a robust, multithreaded server that listens on a port and spawns workers.
    *   **Code**: `DistributedImagingServer` (The Server) & `DistributedImagingClient` (The Client).
    *   **Pattern**: A "Dispatcher" thread accepts connections and hands them off to "Worker" threads (`DistributedImagingThread`).

### Session 03: Communication
**Objective**: Moving beyond raw sockets to structured communication.
*   **Textbook Theory (Ch. 4)**:
    *   **OSI Model**: Transport Layer (TCP/UDP) vs Application Layer.
    *   **Semantics**:
        *   *Persistent vs. Transient*: Is the message stored by middleware?
        *   *Synchronous vs. Asynchronous*: Does the sender block?
    *   **Sockets**: The low-level interface to the Transport Layer.
*   **Exercise**: `edu.cooper.ece465.session03`
    *   **Goal**: Implement a mechanism to transfer files (stream of bytes) between nodes.
    *   **Code**: `filetransfer.Server` & `filetransfer.Client`.
    *   **Concept**: Handling InputStreams and OutputStreams over network sockets.

### Session 04: Naming & Service Discovery
**Objective**: Demystifying how distributed entities find each other.
*   **Textbook Theory (Ch. 5)**:
    *   **Entities**: Resources, processes, users, etc.
    *   **Access Point**: The address of an entity (e.g., `127.0.0.1:8080`).
    *   **Name**: A human-friendly string (e.g., "ImagingService") used to resolve an Access Point.
    *   **Name Resolution**: The process of looking up a Name to find its Access Point (like a Phonebook or DNS).
    *   **Flat Naming**: Simple lookups (e.g., DHTs or Broadcasting).
    *   **Structured Naming**: Hierarchical lookups (e.g., DNS, Filesystems).
*   **Exercise**: `edu.cooper.ece465.session04.naming`
    *   **Goal**: Implement a "DNS-Lite" to replace hardcoded IP addresses.
    *   **Code**: `NamingServer` (The Registry) & `NamingClient` (The Resolver).
    *   **Integration**: `DistributedImagingServer` registers itself; `DistributedImagingClient` asks the `NamingServer` where to find it.

### Session 05: Data Formats & Heterogeneity
**Objective**: Handling data between machines with different architectures (Endianness, OS, Languages).
*   **Textbook Theory (Ch. 4.2)**:
    *   **The Problem**: A C++ `struct` on Windows != a Java `Object` on Linux.
    *   **Marshaling/Serialization**: Converting an in-memory data structure into a platform-independent format (wire format) for transmission.
    *   **Canonical Forms**:
        *   *JSON/XML*: Text-based, readable, slower.
        *   *Protobuf/MsgPack*: Binary, compact, faster.
*   **Exercise**: `edu.cooper.ece465.session05`
    *   **Goal**: Demystify "Magic" libraries like Gson/Jackson.
    *   **Code**: `MiniJsonSerializer` (Reflection-based) vs. `EmployeeGsonExample` (Library-based).
    *   **Concept**: Using Java Reflection (`obj.getClass().getDeclaredFields()`) to inspect objects at runtime and build a JSON string manually.

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
---

## Curriculum Gap Analysis (Textbook vs. Repo)
*Assessment of coverage against Tanenbaum's "Distributed Systems" 4th Ed.*

### ✅ Covered
*   **Ch 1 (Intro)**: Covered in Session 01.
*   **Ch 2 (Architectures)**: Covered in Session 02 (Client-Server).
*   **Ch 3 (Processes/Threads)**: Covered in Session 01 & 02.
*   **Ch 4 (Communication)**: Covered in Session 03 (Sockets) & Session 05 (Serialization/RPC-prep).
*   **Ch 5 (Naming)**: Covered in Session 04 (DNS-Lite).

### 🚧 To Be Implemented (Future Sessions)
*   **Ch 6 (Coordination)**:
    *   *Missing*: Logical Clocks (Lamport/Vector), Mutual Exclusion, Leader Election (Bully/Ring Algo).
    *   *Plan*: Add to Session 04 extension or new Session 06.
*   **Ch 7 (Consistency & Replication)**:
    *   *Missing*: Quorums, Consistency Models (Strong, Eventual, Causal).
    *   *Plan*: Session 10 (Cloud Storage).
*   **Ch 8 (Fault Tolerance)**:
    *   *Missing*: Consensus (Paxos/Raft), Checkpointing, Reliability.
    *   *Plan*: Session 11.
*   **Ch 9 (Security)**:
    *   *Missing*: Authentication, Authorization, Encryption channels.
    *   *Plan*: Session 12 (AWS IAM & Networking).
### Session 13: Full Stack Distribution (Final Project)
**Objective**: Integrating frontend and backend in a distributed manner.
*   **Theory**:
    *   Three-tier architecture.
    *   Event-driven architecture.
*   **Exercise**: `apps/amplifyapp`
    *   A React-based frontend connected to AWS Amplify.

---
**Note to Student**: This lesson plan bridges the gap between the theoretical "van Steen & Tanenbaum" concepts and the practical "AWS/Java/Docker" implementations found in this repository.

