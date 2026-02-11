import threading
import time
from queue import Queue

# Shared memory Naming Registry (standard Python dict)
naming_registry = {}
registry_lock = threading.Lock()
task_queue = Queue()

def worker():
    """Worker thread pulling tasks from a queue."""
    thread_name = threading.current_thread().name
    print(f"[{thread_name}] Starting...")
    
    while True:
        # Get task from queue
        item = task_queue.get()
        if item is None:
            # Poison pill: gracefully exit
            task_queue.task_done()
            print(f"[{thread_name}] Received exit signal. Stopping.")
            break
            
        image_id, file_path = item
        
        # Coordination: Check if already processed
        with registry_lock:
            status = naming_registry.get(image_id, {}).get('status', 'unknown')
            if status in ['processing', 'done']:
                print(f"[{thread_name}] Skipping {image_id}: Already {status}")
                task_queue.task_done()
                continue
            
            # Update status
            info = naming_registry.setdefault(image_id, {})
            info['status'] = 'processing'
            naming_registry[image_id] = info
            
        print(f"[{thread_name}] Processing {image_id} (Simulating I/O)")
        
        # Simulate I/O or processing (sleep)
        time.sleep(1)
        thumb_path = f"{file_path}_thumb.jpg"
        
        # Create the thumbnail file on disk
        try:
            with open(thumb_path, 'w') as f:
                f.write("This is a generated thumbnail.")
        except Exception as e:
            print(f"[{thread_name}] Error creating file: {e}")
        
        # Update Naming Registry
        with registry_lock:
            info = naming_registry[image_id]
            info['status'] = 'done'
            info['thumb_location'] = thumb_path
            naming_registry[image_id] = info
            
        print(f"[{thread_name}] Finished {image_id}")
        task_queue.task_done()

if __name__ == '__main__':
    print("--- 2. Multi-Threading CDN Example (Local) ---")

    # Start Worker Threads
    threads = []
    num_workers = 3
    for i in range(num_workers):
        t = threading.Thread(target=worker, name=f"Worker-{i}")
        t.start()
        threads.append(t)

    # Simulate incoming requests
    # Image 1 (duplicated request)
    image_1 = "img_456"
    path_1 = "/tmp/node_a/img_456.jpg"
    naming_registry[image_1] = {'original_location': path_1, 'status': 'uploaded'}
    task_queue.put((image_1, path_1))
    task_queue.put((image_1, path_1)) 

    # Image 2
    image_2 = "img_789"
    path_2 = "/tmp/node_a/img_789.jpg"
    naming_registry[image_2] = {'original_location': path_2, 'status': 'uploaded'}
    task_queue.put((image_2, path_2))

    print("Requests submitted. Waiting for completion...")
    
    # Wait for work to finish
    task_queue.join() 
    print("All tasks completed.")

    # Stop threads (send poison pills)
    for _ in range(num_workers): 
        task_queue.put(None)
        
    for t in threads: 
        t.join()

    print(f"Final Registry: {naming_registry}")
