# Distributed Systems - Week 03 Examples

This folder contains examples of distributed processing and storage concepts using Python.

## Prerequisites

- Python 3.x
- A terminal (or multiple terminals)

## 1. Multi-Process (Single Computer)

This example demonstrates distributing a task across multiple processes on a single machine using Inter-Process Communication (IPC) via Pipes.

**Files:** `ipc_multiprocess.py`

**Architecture:**
- **Generator Process:** Produces numbers.
- **Worker Process:** Calculates the square of the number.
- **Storage Process:** Aggregates results.

**Run:**
```bash
python3 ipc_multiprocess.py
```

## 2. Multi-Thread (Single Computer)

This example demonstrates concurrency using threads within a single process, sharing memory space (Queue and List).

**Files:** `threading_example.py`

**Architecture:**
- **Producer Thread:** Adds tasks to a Queue.
- **Consumer Threads:** Pick tasks, process them, and store in a shared list.

**Run:**
```bash
python3 threading_example.py
```

## 3. TCP Networking (2-3 Nodes)

This example demonstrates reliable communication between nodes using TCP. You can run these on the same machine (localhost) or different computers on the same network.

**Files:** `tcp_server.py`, `tcp_client.py`

**Architecture:**
- **Server:** Listens for connections and processes requests (calculates squares).
- **Client(s):** Connect to server and request work.

**Run (Single Machine):**
1.  Terminal 1 (Server): `python3 tcp_server.py`
2.  Terminal 2 (Client 1): `python3 tcp_client.py`
3.  Terminal 3 (Client 2): `python3 tcp_client.py`

**Run (Multi-Node):**
1.  **Node A (Server):** Find your IP address (e.g., `ifconfig` or `ip addr`). Let's say it is `192.168.1.5`.
    ```bash
    python3 tcp_server.py
    ```
2.  **Node B (Client):** Connect to Node A's IP.
    ```bash
    python3 tcp_client.py 192.168.1.5
    ```

## 4. UDP Networking (2-3 Nodes)

This example demonstrates connectionless communication (fire-and-forget) using UDP.

**Files:** `udp_receiver.py`, `udp_sender.py`

**Architecture:**
- **Receiver:** Listens for incoming packets.
- **Sender:** Broadcasts heartbeat messages.

**Run (Multi-Node):**
1.  **Node A (Receiver):**
    ```bash
    python3 udp_receiver.py
    ```
2.  **Node B (Sender):** Send to Node A's IP.
    ```bash
    python3 udp_sender.py <Node_A_IP>
    ```
