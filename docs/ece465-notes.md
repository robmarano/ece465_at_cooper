# ECE 465 Spring 2026 Weekly Course Notes

[<- back to syllabus](../README.md)

---

Class 01:

# Chapter 1: Introduction to Distributed Systems

### 1.1 From Networked to Distributed Systems
A distributed system is defined as a collection of autonomous computing elements that appears to its users as a single coherent system. This definition highlights two key features:
1.  **Independent Nodes:** The system consists of autonomous devices (nodes) that act independently.
2.  **Single System Image:** To users and applications, the system behaves as a single entity, hiding the underlying network complexity.

**Distributed vs. Decentralized Systems**
The distinction between these systems lies in how and why computers are connected:
*   **Decentralized Systems (Integrative View):** These arise from the need to connect existing, independent systems across administrative boundaries. Spreading resources is **necessary** due to administrative policies or lack of trust.
    *   *Examples:* Blockchain (distributed ledgers) due to lack of trust between parties; Federated Learning where data must remain within an organization's perimeter.
*   **Distributed Systems (Expansive View):** These involve extending a system with more computers to improve performance or reliability. Spreading resources is **sufficient** to meet system goals but not strictly dictated by administrative boundaries.
    *   *Example:* Cloud computing and Content Delivery Networks (CDNs) like Akamai, where data is replicated for performance.

**The Centralization Myth**
A common misconception is that centralized solutions are inherently unscalable or vulnerable. However, a distinction must be made between **logical** and **physical** centralization.
*   *Logical Centralization:* The system acts as a single entity (e.g., the DNS root).
*   *Physical Distribution:* The implementation is spread across many replicated servers to ensure scalability and fault tolerance.

### 1.2 Design Goals
Building distributed systems is complex and justified only when specific goals are met:

#### 1. Resource Sharing
The primary goal is to make resources (storage, computing power, data, networks) easily accessible and shareable among users. This allows for economic efficiency and collaboration.

#### 2. Distribution Transparency
The system should hide the fact that its resources are physically distributed. This is typically achieved through a **middleware** layer.

| Transparency Type | Description |
| :--- | :--- |
| **Access** | Hides differences in data representation and how resources are accessed. |
| **Location** | Hides where a resource is physically located. |
| **Relocation** | Hides that a resource may move to another location while in use. |
| **Migration** | Hides that a resource may move to another location. |
| **Replication** | Hides that a resource is replicated (copied) across multiple nodes. |
| **Concurrency** | Hides that a resource may be shared by several competitive users. |
| **Failure** | Hides the failure and recovery of a resource. |

*Note on Transparency:* Full transparency is often impossible or undesirable (e.g., hiding network latency in a real-time system is physically impossible).

#### 3. Openness
An open system offers components that can be easily used by or integrated into other systems.
*   **Interoperability:** Implementations from different manufacturers can co-exist and work together.
*   **Portability:** An application developed for one distributed system can execute on another.
*   **Mechanism vs. Policy:** To achieve flexibility, systems should separate the *mechanism* (how something is done) from the *policy* (how it is used).

#### 4. Scalability
Scalability is measured along three dimensions:
*   **Size Scalability:** Adding more users and resources without noticeable performance loss. Problems usually arise from centralized services, data, or algorithms.
*   **Geographical Scalability:** Maintaining performance when users and resources are far apart. Latency is the primary challenge here.
*   **Administrative Scalability:** Managing the system easily even as it spans many independent administrative organizations.

**Scaling Techniques:**
*   **Hiding Latency:** Moving computation to the client (e.g., JavaScript forms) or using asynchronous communication.
*   **Partitioning/Distribution:** Splitting a component into smaller parts (e.g., the DNS namespace or the Web).
*   **Replication:** Copying data to increase availability and balance load, though this introduces consistency challenges.

### 1.3 Types of Distributed Systems

#### High-Performance Distributed Computing
*   **Cluster Computing:** Homogeneous hardware (same OS/network) connected via high-speed LAN, managed by a single node.
*   **Grid Computing:** Heterogeneous federation of resources (different HW/OS/Domains) often creating a "virtual organization" for collaboration.

#### Distributed Information Systems
Focuses on integrating separate applications into an enterprise-wide system.
*   **Transaction Processing:** Operations adhere to **ACID** properties (Atomic, Consistent, Isolated, Durable).
*   **Enterprise Application Integration (EAI):** Uses middleware (RPC, RMI, Message-Oriented Middleware) to allow applications to communicate directly.

#### Pervasive Systems
Systems that blend into the environment, characterized by small, battery-powered, mobile devices.
*   **Ubiquitous Computing:** The system is continuously present; devices are networked, distributed, and unobtrusive.
*   **Mobile Computing:** Devices inherently change location. Distinction is made between Mobile Cloud Computing (offloading to the cloud) and Mobile Edge Computing (processing at nearby edge servers for low latency).
*   **Sensor Networks:** Nodes collaborate to process sensed data efficiently, often using in-network data processing to reduce bandwidth.

### 1.4 Pitfalls
Developers often commit errors by accepting the following **false assumptions** about the underlying network:
1.  The network is reliable.
2.  The network is secure.
3.  The network is homogeneous.
4.  The topology does not change.
5.  Latency is zero.
6.  Bandwidth is infinite.
7.  Transport cost is zero.
8.  There is one administrator.

---

### Python Coding Examples

The textbook emphasizes that distributed systems rely on message passing and hiding complexity (transparency). Below are Python examples illustrating **Access Transparency** (hiding data representation using serialization) and **Basic Connectivity** (the foundation of distributed systems).

#### Example 1: Access Transparency via Serialization
In distributed systems, machines may represent data differently. To achieve access transparency, data must be marshaled (serialized) into a standard format before transmission.

```python
import pickle

# A complex data object (list of dictionaries)
# In a real scenario, this could be a database record or object state.
local_data = [
    {"id": 1, "action": "update", "value": 42},
    {"id": 2, "action": "delete"}
]

print(f"Original Data Type: {type(local_data)}")

# Marshaling (Serialization)
# This simulates preparing data to be sent over the network.
# It hides the internal memory representation of the Python list.
network_message = pickle.dumps(local_data)

print(f"Marshaled (Network) Data: {network_message}")

# --- Network Transmission Simulation ---

# Unmarshaling (Deserialization)
# The receiving node reconstructs the object without knowing
# the sender's internal memory layout.
received_data = pickle.loads(network_message)

print(f"Reconstructed Data: {received_data}")
print(f"Is data identical? {local_data == received_data}")
```
*Ref: Concepts based on Section 1.2.2 and Python pickle usage in Note 4.4.*

#### Example 2: Basic Client-Server Communication
This example demonstrates the "Networked" aspect of distributed systems using sockets. This is the low-level mechanism upon which higher-level distributed abstractions (like RPC) are built.

**The Server (Run this first):**
```python
from socket import *

def start_server():
    # Create a TCP/IP socket
    server_socket = socket(AF_INET, SOCK_STREAM)
    
    # Bind the socket to the address and port
    server_socket.bind(('localhost', 8080))
    
    # Listen for incoming connections (queue up to 1 request)
    server_socket.listen(1)
    print("Server is listening on port 8080...")
    
    while True:
        # Accept a connection
        connection, client_address = server_socket.accept()
        try:
            print(f"Connection from {client_address}")
            
            # Receive data in small chunks
            data = connection.recv(1024)
            if data:
                print(f"Received: {data.decode()}")
                
                # Send data back to the client (Echo)
                response = "Acknowledged: " + data.decode()
                connection.sendall(response.encode())
        finally:
            # Clean up the connection
            connection.close()

if __name__ == "__main__":
    start_server()
```

**The Client:**
```python
from socket import *

def start_client():
    # Create a TCP/IP socket
    client_socket = socket(AF_INET, SOCK_STREAM)
    
    # Connect the socket to the server's port
    client_socket.connect(('localhost', 8080))
    
    try:
        # Send data
        message = "Hello Distributed World"
        print(f"Sending: {message}")
        client_socket.sendall(message.encode())
        
        # Look for the response
        response = client_socket.recv(1024)
        print(f"Received: {response.decode()}")
        
    finally:
        print("Closing socket")
        client_socket.close()

if __name__ == "__main__":
    start_client()
```
*Ref: Adapted from Note 2.1 illustrating basic connectivity principles discussed in Chapter 1.*