# Distributed ImageCDN Example

This project demonstrates three different architectural approaches to building a distributed system for image processing (a simplified Content Delivery Network).

The goal is to process "uploaded" images (create thumbnails), track their location, and coordinate work to avoid duplication.

## 1. Quick Start

### Prerequisites
- Python 3.x
- A terminal environment (Linux/macOS recommended)

### Setup Environment
First, run the setup script. This creates dummy directories (`/tmp/node_a`, `/tmp/node_b`) and populates them with dummy `.jpg` files. This allows the examples to simulate real file processing.

```bash
python3 setup_env.py
```

---

## 2. Running the Examples

### A. Multi-Processing (Local Distribution)
*Best for: CPU-intensive tasks on a single machine (bypassing Python's GIL).*

This script spawns multiple operating system processes. Each process works on a subset of images independently.

**Run:**
```bash
python3 cdn_multiprocessing.py
```

**Verify:**
Check that thumbnail files were created:
```bash
ls -l /tmp/node_a/
# You should see files like 'img_100.jpg_thumb.jpg'
```

### B. Multi-Threading (Local Concurrency)
*Best for: I/O-bound tasks (e.g., downloading files) on a single machine.*

This script uses threads within a single process. They share memory directly but are limited by the Global Interpreter Lock (GIL) for CPU tasks.

**Run:**
```bash
python3 cdn_multithreading.py
```

**Verify:**
Check that thumbnail files were created (if you cleaned up previous runs):
```bash
ls -l /tmp/node_a/
```

### C. Distributed System (Networked Nodes)
*Best for: Large-scale systems spanning multiple servers.*

This example uses a Client-Server architecture with a **Master Node** (coordinator) and **Worker Nodes** (processors). They communicate over TCP sockets.

**Step 1: Start the Master Node**
The Master holds the "Naming Registry" and assigns tasks.
```bash
python3 cdn_node_master.py
```
*(Leave this running in Terminal 1)*

**Step 2: Register Images (Client)**
Use the client script to tell the Master about new images that need processing.
```bash
# Open Terminal 2
# Syntax: python3 cdn_client.py register [ID] [PATH]

python3 cdn_client.py register img_dist_1 /tmp/node_a/img_100.jpg
python3 cdn_client.py register img_dist_2 /tmp/node_a/img_200.jpg
```

**Step 3: Start a Worker Node**
The Worker connects to the Master, asks for work, processes it, and reports back.
```bash
# Open Terminal 3 (or use Terminal 2)
python3 cdn_node_worker.py
```
*You will see the worker receive the task, "process" it (sleep + create file), and update the Master.*

**Step 4: Query Status**
Check the status of an image using the client.
```bash
# In Terminal 2/3
python3 cdn_client.py query img_dist_1
```
*Response should show `"status": "done"` and the location of the thumbnail.*

---

## 3. How it Works (Under the Hood)

1.  **Storage:** We simulate storage using local folders `/tmp/node_a` and `/tmp/node_b`. In a real system, these would be S3 buckets or distinct storage servers.
2.  **Processing:** The "thumbnail generation" is simulated by sleeping for 2 seconds and then creating a small text file ending in `_thumb.jpg`.
3.  **Coordination:**
    *   **MP/MT:** Uses Python's `multiprocessing.Lock` or `threading.Lock` to safely update a shared dictionary.
    *   **Distributed:** Uses the Master node as the source of truth. Workers must report to the Master to get tasks and update status, preventing race conditions.
