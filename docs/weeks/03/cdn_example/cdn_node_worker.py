import socket
import json
import time
import sys

# --- Configuration ---
MASTER_HOST = '127.0.0.1'
MASTER_PORT = 65432
WORKER_ID = f"worker_{int(time.time())}"

def connect_to_master():
    """Establishes connection to the master node."""
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((MASTER_HOST, MASTER_PORT))
        print(f"[Worker] Connected to Master at {MASTER_HOST}:{MASTER_PORT}")
        return sock
    except ConnectionRefusedError:
        print(f"[Worker] Could not connect to Master. Is it running?")
        sys.exit(1)

def run_worker():
    """Main worker loop."""
    print(f"[Worker {WORKER_ID}] Starting up...")
    
    sock = connect_to_master()
    
    try:
        while True:
            # 1. Ask for work
            request = {"action": "get_work"}
            sock.sendall(json.dumps(request).encode('utf-8'))
            
            response_data = sock.recv(4096)
            if not response_data:
                break
                
            response = json.loads(response_data.decode('utf-8'))
            
            if response.get("status") == "ok":
                task = response.get("task")
                image_id = task.get("image_id")
                location = task.get("location")
                
                print(f"[Worker] Received task: Process {image_id} from {location}")
                
                # 2. Process Image (Simulated)
                print(f"[Worker] Processing {image_id}...")
                time.sleep(2) # Simulate heavy CPU work
                thumb_path = f"{location}_thumb.jpg"
                
                # Create the thumbnail file on disk
                try:
                    with open(thumb_path, 'w') as f:
                        f.write("This is a generated thumbnail.")
                    print(f"[Worker] Created file: {thumb_path}")
                except Exception as e:
                    print(f"[Worker] Error creating file: {e}")
                
                # 3. Report Completion
                update_request = {
                    "action": "update_status",
                    "image_id": image_id,
                    "data": {
                        "status": "done",
                        "processed_by": WORKER_ID,
                        "thumb_location": thumb_path
                    }
                }
                sock.sendall(json.dumps(update_request).encode('utf-8'))
                
                # Wait for ack
                ack_data = sock.recv(4096)
                ack = json.loads(ack_data.decode('utf-8'))
                if ack.get("status") == "ok":
                     print(f"[Worker] Successfully reported completion for {image_id}")
                else:
                     print(f"[Worker] Error reporting completion: {ack}")

            elif response.get("status") == "empty":
                print("[Worker] No work available. Sleeping...")
                time.sleep(3) 
            
            else:
                print(f"[Worker] Unexpected response: {response}")
                time.sleep(3)

    except KeyboardInterrupt:
        print("\n[Worker] Shutting down.")
    except Exception as e:
        print(f"[Worker] Error: {e}")
    finally:
        sock.close()

if __name__ == "__main__":
    if len(sys.argv) > 1:
        MASTER_HOST = sys.argv[1]
    run_worker()
