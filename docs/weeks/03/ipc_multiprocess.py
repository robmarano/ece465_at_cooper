import multiprocessing
import os
import time

def generator(conn, n):
    """
    Generates numbers from 0 to n-1 and sends them to the worker.
    """
    print(f"[Generator PID {os.getpid()}] Starting to generate {n} numbers...")
    for i in range(n):
        print(f"[Generator] Sending {i}")
        conn.send(i)
        time.sleep(0.1) # Simulate some delay
    
    conn.send(None) # Sentinel to signal end
    conn.close()
    print(f"[Generator] Finished.")

def worker(in_conn, out_conn):
    """
    Receives numbers, squares them (processing), and sends to storage.
    """
    print(f"[Worker PID {os.getpid()}] Ready to process...")
    while True:
        data = in_conn.recv()
        if data is None:
            break
        
        result = data * data
        print(f"[Worker] Received {data}, processed to {result}")
        out_conn.send(result)
    
    out_conn.send(None) # Signal storage we are done
    in_conn.close()
    out_conn.close()
    print(f"[Worker] Finished.")

def storage(conn):
    """
    Receives results and stores them (simulated by a list and printing).
    """
    print(f"[Storage PID {os.getpid()}] Ready to store...")
    stored_data = []
    while True:
        data = conn.recv()
        if data is None:
            break
        
        stored_data.append(data)
        print(f"[Storage] Stored: {data}")
    
    print(f"[Storage] Final stored data: {stored_data}")
    print(f"[Storage] Total sum: {sum(stored_data)}")
    conn.close()

if __name__ == "__main__":
    print("--- Multi-Process Distributed Processing Example (Pipes) ---")
    workers = 1000

    # Create pipes
    # Pipe returns two connection objects representing the two ends of the pipe.
    gen_worker_recv, gen_worker_send = multiprocessing.Pipe(duplex=False)
    worker_storage_recv, worker_storage_send = multiprocessing.Pipe(duplex=False)

    # Create processes
    # Process 1: Generator (sends to worker)
    p_gen = multiprocessing.Process(target=generator, args=(gen_worker_send, workers))
    
    # Process 2: Worker (receives from generator, sends to storage)
    p_work = multiprocessing.Process(target=worker, args=(gen_worker_recv, worker_storage_send))
    
    # Process 3: Storage (receives from worker)
    p_store = multiprocessing.Process(target=storage, args=(worker_storage_recv,))

    # Start processes
    p_store.start()
    p_work.start()
    p_gen.start()

    # Wait for completion
    p_gen.join()
    p_work.join()
    p_store.join()
    
    print("--- Processing Complete ---")
