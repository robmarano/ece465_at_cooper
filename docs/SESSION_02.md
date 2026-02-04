# ECE 465 Spring 2026 Weekly Course Notes

## Session 02

### Multi-processing & Network Programming — Part 1

#### I. The Linux Process Model
**Definition and Context**
To understand distributed systems, we must first understand the fundamental unit of execution: the process. From an operating system perspective, a process is defined simply as a "program in execution".

**Process Context**
When an operating system (like Linux) executes a program, it creates a "virtual processor" for it. To manage this, the OS maintains a **process context**, which is stored in a process table. This context includes:
*   **CPU Register Values:** Including the program counter and stack pointer.
*   **Memory Maps:** The definition of the address space allocated to the process.
*   **Open Files:** Pointers to resources the process is currently using.
*   **Accounting Information & Privileges:** User IDs and usage stats.

**OS Protection and Concurrency**
The OS ensures **concurrency transparency**, meaning multiple processes share the same CPU and hardware resources without corrupting each other. This isolation comes at a performance price: creating a process requires initializing a completely independent address space (copying program text, zeroing data segments, setting up a stack). Switching between processes requires saving registers, modifying Memory Management Unit (MMU) registers, and invalidating address translation caches like the Translation Lookaside Buffer (TLB).

---

#### II. Multi-Processing and IPC (Inter-Process Communication)
Distributed applications are often constructed as collections of cooperating programs, each executing as a separate process. On a single Linux machine, we can start multiple processes that run concurrently. Since they have separate address spaces, they require specific mechanisms to exchange data, known as **Inter-Process Communication (IPC)**.

**Options for IPC**
1.  **Files with Locks:** Processes read/write to a shared file. To maintain consistency, they must use locking mechanisms (e.g., `flock`) to prevent concurrent access corruption.
2.  **Pipes:** A unidirectional data channel that connects the standard output of one process to the standard input of another.
3.  **Message Queues:** A linked list of messages stored within the kernel.
4.  **Shared Memory:** A segment of memory accessible by multiple processes (requires synchronization).

**Context Switching Overhead**
IPC often requires extensive context switching. For example, sending data via IPC might require switching from user mode to kernel mode, switching the process context within the kernel, and switching back to user mode for the receiver.

##### **Python Example: Multi-processing with Pipes**
Python’s `multiprocessing` library allows the creation of processes that bypass the Global Interpreter Lock (GIL) by using subprocesses.

```python
from multiprocessing import Process, Pipe
import os

def sender(conn):
    msg = "Hello from Process " + str(os.getpid())
    conn.send(msg)
    for i in range(10):
        print(f"Process {os.getpid()} sending: {i}")
    conn.close()

def receiver(conn):
    msg = conn.recv()
    print(f"Process {os.getpid()} received: {msg}")
    for i in range(10):
        print(f"Process {os.getpid()} received: {i}")
    conn.close()

if __name__ == '__main__':
    # Create a pipe for communication
    parent_conn, child_conn = Pipe()
    
    # Create two separate processes
    p1 = Process(target=sender, args=(child_conn,))
    p2 = Process(target=receiver, args=(parent_conn,))
    
    # Start processes (OS creates independent address spaces)
    p1.start()
    p2.start()
    
    # Wait for completion
    p1.join()
    print(f"Process {p1.name} finished.")
    p2.join()
    print(f"Process {p2.name} finished.")
```
*(Ref: Based on logic described in regarding `multiprocessing.Process`)*

##### **Java Example: Processes with File Communication**
In Java, `ProcessBuilder` starts operating system processes. Here, two JVMs communicate via a shared file with locking.

```java
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileIPC {
    public static void main(String[] args) {
        File file = new File("shared.txt");
        
        // Simulating Process 1: Writer
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel channel = raf.getChannel()) {
            
            // Acquire an exclusive lock on the file
            FileLock lock = channel.lock();
            raf.writeBytes("Data from Process 1");
            lock.release(); // Release lock for other processes
            
        } catch (IOException e) { e.printStackTrace(); }

        // Simulating Process 2: Reader (conceptually a separate process)
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {
            
            // In a real scenario, this would wait for the lock
            String line = raf.readLine();
            System.out.println("Process 2 read: " + line);
            
        } catch (IOException e) { e.printStackTrace(); }
    }
}
```

---

#### III. Collapsing to Multi-Threading
While processes provide strong isolation, the granularity is often too coarse for high performance. We can "collapse" the logic of multiple communicating processes into a single process containing multiple **threads**.

**The Thread Model**
*   **Definition:** A thread behaves like a process (executes its own code independently) but operates within the *same* address space as other threads in that process.
*   **Thread Context:** Contains the minimal information needed for execution (CPU registers, stack pointer) but ignores memory maps and open files, which are shared with the parent process.
*   **Performance:** Switching threads is cheaper than switching processes because the MMU map does not need to change, and the TLB does not need flushing.
*   **Risks:** Because threads share data segments, the OS does not protect them from each other. The developer must manage synchronization (e.g., Mutexes).

**Why switch to Threads?**
1.  **Blocking Calls:** In a single-threaded process, a blocking I/O call stops the entire process. In a multi-threaded process, one thread can block (wait for I/O) while others continue execution.
2.  **Shared Data:** Threads can communicate via shared variables in memory without the overhead of kernel-mediated IPC (pipes/sockets).

##### **Python Example: Multi-threading with Shared Memory**
Unlike the `multiprocessing` example, these threads share the global variable `shared_x`.

```python
from threading import Thread
import time

# Variable shared by all threads in this process
shared_x = 0

def worker(name):
    global shared_x
    local_copy = shared_x
    time.sleep(0.1) # Simulate work
    shared_x = local_copy + 1
    print(f"{name} updated x to {shared_x}")

if __name__ == "__main__":
    thread_list = []
    # Create threads
    for i in range(3):
        t = Thread(target=worker, args=(f"Thread-{i}",))
        thread_list.append(t)
        t.start()

    # Wait for threads to finish
    for t in thread_list:
        t.join()
        
    print(f"Final value of shared_x: {shared_x}")
```
*(Ref: Adapted from showing threading vs. multiprocessing semantics)*

##### **Java Example: Multi-threading**
Java natively supports threading. This example demonstrates multiple threads running within one JVM process. For network-based threading (sockets), refer to the course GitHub repository.

```java
public class ThreadedExample {
    // Shared resource
    private static int sharedCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            // Critical section (should be synchronized in production)
            int temp = sharedCounter;
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            sharedCounter = temp + 1;
            System.out.println(name + " updated counter.");
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        
        System.out.println("Final Counter: " + sharedCounter);
    }
}
```