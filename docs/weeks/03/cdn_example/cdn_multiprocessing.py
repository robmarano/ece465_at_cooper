import multiprocessing
import os
import time

# Simulated storage directories
STORAGE_NODES = ['/tmp/node_a', '/tmp/node_b']

def process_image(image_id, file_path, naming_registry, lock):
    """CPU-bound task: generate thumbnail."""
    # Coordination: Check if already processed or processing
    with lock:
        # Accessing shared memory dictionary
        info = naming_registry.get(image_id, {})
        if info.get('status') in ['processing', 'done']:
            print(f"[Process {multiprocessing.current_process().name}] Skipping {image_id}: Already {info.get('status')}")
            return
        
        # Update status
        info['status'] = 'processing'
        naming_registry[image_id] = info
        
    print(f"[Process {multiprocessing.current_process().name}] Generating thumbnail for {image_id}")
    
    # Simulate thumbnail generation (CPU intensive work)
    time.sleep(2) 
    thumb_path = f"{file_path}_thumb.jpg"
    
    # Create the thumbnail file on disk
    try:
        with open(thumb_path, 'w') as f:
            f.write("This is a generated thumbnail.")
    except Exception as e:
        print(f"[Process {multiprocessing.current_process().name}] Error creating file: {e}")
    
    # Update Naming Registry
    with lock:
        info = naming_registry[image_id]
        info['status'] = 'done'
        info['thumb_location'] = thumb_path
        naming_registry[image_id] = info
    
    print(f"[Process {multiprocessing.current_process().name}] Finished {image_id}")

if __name__ == '__main__':
    print("--- 1. Multi-Processing CDN Example (Local) ---")
    
    # Setup shared state for Naming and Coordination
    # Manager handles synchronization across processes
    manager = multiprocessing.Manager()
    naming_registry = manager.dict()
    registry_lock = manager.Lock()
    
    # Simulate uploads
    images_to_process = []
    for i in range(1, 4):
        img_id = f"img_{i*100}"
        node = STORAGE_NODES[0]
        file_path = f"{node}/{img_id}.jpg"
        naming_registry[img_id] = {'original_location': file_path, 'status': 'uploaded'}
        # Create duplicate requests to demonstrate coordination
        images_to_process.append((img_id, file_path, naming_registry, registry_lock))
        images_to_process.append((img_id, file_path, naming_registry, registry_lock))

    print(f"Initial Registry: {naming_registry}")

    # Distribute computing using a Pool
    # We use 'starmap' to unpack the arguments tuple
    with multiprocessing.Pool(processes=2) as pool:
        print("Starting worker pool...")
        pool.starmap(process_image, images_to_process)
        
    print(f"Final Registry: {dict(naming_registry)}")
