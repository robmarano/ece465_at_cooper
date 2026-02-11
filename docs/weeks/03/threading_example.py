import threading
import queue
import time
import random

# Shared Queue for distributing tasks
task_queue = queue.Queue()
# Shared List for results (representing storage)
results_storage = []
# Lock for synchronizing access to storage
storage_lock = threading.Lock()

def producer(count):
    """Generates tasks and puts them in the queue."""
    thread_name = threading.current_thread().name
    print(f"[{thread_name}] Starting production...")
    for i in range(count):
        item = random.randint(1, 100)
        print(f"[{thread_name}] Produced item: {item}")
        task_queue.put(item)
        time.sleep(0.1)
    print(f"[{thread_name}] Finished production.")

def consumer():
    """Takes tasks from queue, processes them, and stores results."""
    thread_name = threading.current_thread().name
    print(f"[{thread_name}] Ready to consume...")
    
    while True:
        try:
            # Get item with timeout to allow thread to exit if empty for a while
            # In a real app, we might use a sentinel value like None
            item = task_queue.get(timeout=2)
        except queue.Empty:
            print(f"[{thread_name}] Queue empty, exiting.")
            return

        # Simulate processing (e.g., doubling the number)
        processed_item = item * 2
        print(f"[{thread_name}] Processed {item} -> {processed_item}")

        # Store result safely using Lock
        with storage_lock:
            results_storage.append(processed_item)
            print(f"[{thread_name}] Stored result. Total stored: {len(results_storage)}")
        
        task_queue.task_done()
        time.sleep(0.2) # Simulate processing time

if __name__ == "__main__":
    print("--- Multi-Threaded Distributed Processing Example ---")

    # Create threads
    prod_thread = threading.Thread(target=producer, args=(1000,), name="Producer")
    
    # Multiple consumers to demonstrate parallelism (concurrency in Python due to GIL)
    cons_thread1 = threading.Thread(target=consumer, name="Consumer-1")
    cons_thread2 = threading.Thread(target=consumer, name="Consumer-2")
    cons_thread3 = threading.Thread(target=consumer, name="Consumer-3")

    # Start threads
    prod_thread.start()
    cons_thread1.start()
    cons_thread2.start()
    cons_thread3.start()

    # Wait for producer to finish
    prod_thread.join()
    
    # Wait for consumers to finish (they exit on timeout)
    cons_thread1.join()
    cons_thread2.join()
    cons_thread3.join()

    print("\n--- Final Results ---")
    print(f"Storage content: {results_storage}")
    print(f"Total items processed: {len(results_storage)}")
