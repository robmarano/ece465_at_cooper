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

### Session 06: Coordination
**Objective**: Agreeing on events in a system without a global clock.
*   **Textbook Theory (Ch. 6.2)**:
    *   **The Problem**: "Time" is relative in distributed systems. `Time(A) < Time(B)` is meaningless if clocks aren't synced.
    *   **Logical Clocks (Lamport)**: We don't care *when* exactly something happened, only *what caused what*.
    *   **Happens-Before Relation (->)**: If event A causes B, then `C(A) < C(B)`.
*   **Exercise**: `edu.cooper.ece465.session06.coordination`
    *   **Goal**: Implement Lamport Timestamps.
    *   **Code**: `LamportClock`, `LamportProcess`, `LamportDemo`.
    *   **Concept**: Simulating a distributed system where processes exchange messages and update their local logical clocks to maintain causal ordering.

### Session 07: Consistency & Replication
**Objective**: Understanding the trade-off between Performance/Availability and Data Accuracy.
*   **Textbook Theory (Ch. 7)**:
    *   **The CAP Theorem**: Consistency, Availability, Partition Tolerance (Pick 2).
    *   **Strong Consistency (Sequential)**: Updates are seen in the same order by everyone. Reads return the latest write. Slow (requires coordination).
    *   **Eventual Consistency**: Updates propagate lazily. Reads may return stale data, but systems are fast and available.
*   **Exercise**: `edu.cooper.ece465.session07.consistency`
    *   **Goal**: Simulate a Replicated Key-Value Store.
    *   **Code**: `Coordinator`, `ReplicaNode`.
    *   **Experiment**: consistency modes.
        *   *Strong*: Write doesn't return until all replicas ACK.
        *   *Eventual*: Write returns immediately; replicas update in background.
    *   **Observe**: Stale reads in Eventual mode.

### Session 08: Fault Tolerance
**Objective**: Building reliable systems from unreliable components.
*   **Textbook Theory (Ch. 8)**:
    *   **Failure Models**: Crash, Omission, Timing, Byzantine (Malicious).
    *   **Redundancy**: The key to fault tolerance.
    *   **Failure Detection**: How do we know a node failed? (Heartbeats/Ping).
    *   **Recovery**: Primary-Backup replication.
*   **Exercise**: `edu.cooper.ece465.session08.faulttolerance`
    *   **Goal**: Implement a "Warm Standby" Failover system.
    *   **Code**: `ServiceNode` (Can be Primary or Backup), `HeartbeatManager`.
    *   **Experiment**:
        1.  Start Primary and Backup.
        2.  Primary handles requests & sends heartbeats.
        3.  **Kill Primary**.
        4.  Backup detects missing heartbeat (Timeout).
        5.  Backup promotes self to Primary and resumes handling requests.

### Session 09: Security
**Objective**: Guaranteeing Confidentiality, Integrity, and Authenticity.
*   **Textbook Theory (Ch. 9)**:
    *   **Cryptography**: Symmetric (Shared Key) vs. Asymmetric (Public/Private Key).
    *   **Authentication**: Proving you are who you say you are.
    *   **Digital Signatures**: Proving a message came from a specific sender and wasn't tampered with.
*   **Exercise**: `edu.cooper.ece465.session09.security`
    *   **Goal**: Implement a Secure Messaging System using RSA Signatures.
    *   **Code**: `KeyManager`, `SecureMessenger`.
    *   **Experiment**:
        1.  Sender signs a message ("Transfer $100").
        2.  Receiver verifies it with Sender's Public Key -> **Valid**.
        3.  Man-In-The-Middle tampers with message ("Transfer $900").
        4.  Receiver verifies -> **Invalid** (Integrity Check).

### Conclusion
We have built a Distributed Systems Playground covering the full semester curriculum!



