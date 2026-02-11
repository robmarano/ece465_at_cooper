# PR Summary: Week 03 Distributed Systems Examples

## Overview
This PR adds code examples and documentation for Week 03 of the Distributed Systems course, focusing on different paradigms for distributed processing and storage.

## Changes
1.  **Core Distributed Concepts (`weeks/03/`)**:
    *   `ipc_multiprocess.py`: Demonstrates Inter-Process Communication (IPC) using `multiprocessing.Pipe`.
    *   `threading_example.py`: Demonstrates concurrency using `threading` and shared memory (`queue.Queue`).
    *   `tcp_server.py` & `tcp_client.py`: Implements a basic TCP client-server model.
    *   `udp_sender.py` & `udp_receiver.py`: Implements a basic UDP broadcast model.
    *   `README.md`: Instructions for running the core examples.

2.  **CDN Example Project (`weeks/03/cdn_example/`)**:
    *   A simulated Content Delivery Network (CDN) implementing three architectures:
        *   **Multi-Processing:** `cdn_multiprocessing.py` (CPU-bound optimization).
        *   **Multi-Threading:** `cdn_multithreading.py` (I/O-bound optimization).
        *   **Distributed System:** `cdn_node_master.py` (Coordinator) & `cdn_node_worker.py` (Worker) using TCP sockets.
    *   `cdn_client.py`: A CLI client to interact with the distributed system (register images, query status).
    *   `setup_env.py`: Script to generate dummy image files and directories for testing.
    *   `README.md`: Comprehensive guide to running the CDN examples.

## Verification
- Validated all scripts locally.
- Confirmed `setup_env.py` creates necessary directories and dummy files.
- Confirmed `cdn_client.py` successfully communicates with `cdn_node_master.py`.
- Verified file creation (thumbnails) in `cdn_multiprocessing.py` and `cdn_multithreading.py`.

## Next Steps
- Review this summary.
- Commit changes.
- Push to remote and create PR.
