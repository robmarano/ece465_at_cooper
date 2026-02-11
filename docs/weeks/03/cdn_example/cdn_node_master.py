import socket
import threading
import json
import time

# --- Configuration ---
HOST = '0.0.0.0'
PORT = 65432
NAMING_REGISTRY = {
    # image_id: { 'status': 'uploaded' | 'processing' | 'done', 'node': node_addr, ... }
}
REGISTRY_LOCK = threading.Lock()

def handle_client(conn, addr):
    """
    Handles incoming connections from workers or clients.
    """
    print(f"[Master] Connection from {addr}")
    with conn:
        while True:
            try:
                data = conn.recv(4096)
                if not data:
                    break
                
                request = json.loads(data.decode('utf-8'))
                action = request.get('action')
                image_id = request.get('image_id')
                response = {"status": "error", "message": "Unknown action"}

                if action == "register_image":
                    # Client uploads metadata about a new image
                    with REGISTRY_LOCK:
                        if image_id not in NAMING_REGISTRY:
                            NAMING_REGISTRY[image_id] = {
                                "status": "uploaded",
                                "original_location": request.get("location")
                            }
                            print(f"[Master] Registered new image: {image_id}")
                            response = {"status": "ok", "message": "Image registered"}
                        else:
                             response = {"status": "ok", "message": "Image already exists"}

                elif action == "get_work":
                    # Worker asking for tasks
                    task = None
                    with REGISTRY_LOCK:
                        for img, info in NAMING_REGISTRY.items():
                            if info['status'] == 'uploaded':
                                info['status'] = 'processing'
                                task = {"image_id": img, "location": info['original_location']}
                                break
                    
                    if task:
                        response = {"status": "ok", "task": task}
                        print(f"[Master] Assigned task {task['image_id']} to {addr}")
                    else:
                        response = {"status": "empty", "message": "No work available"}

                elif action == "update_status":
                    # Worker updating status (e.g., done)
                    with REGISTRY_LOCK:
                        if image_id in NAMING_REGISTRY:
                            NAMING_REGISTRY[image_id].update(request.get('data', {}))
                            print(f"[Master] Updated status for {image_id}: {request.get('data')}")
                            response = {"status": "ok"}
                        else:
                            response = {"status": "error", "message": "Image not found"}

                elif action == "query_image":
                    # Client asking where an image is
                    with REGISTRY_LOCK:
                        info = NAMING_REGISTRY.get(image_id)
                        response = {"status": "ok", "data": info}

                conn.sendall(json.dumps(response).encode('utf-8'))
            
            except json.JSONDecodeError:
                print(f"[Master] Invalid JSON received from {addr}")
                break
            except Exception as e:
                print(f"[Master] Error handling {addr}: {e}")
                break
    print(f"[Master] Connection closed: {addr}")

def start_master():
    """Starts the TCP Server."""
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    try:
        server.bind((HOST, PORT))
        server.listen()
        print(f"[Master] Naming/Coordination Service running on {HOST}:{PORT}")
        
        while True:
            conn, addr = server.accept()
            t = threading.Thread(target=handle_client, args=(conn, addr))
            t.start()
    except KeyboardInterrupt:
        print("\n[Master] Shutting down.")
    finally:
        server.close()

if __name__ == "__main__":
    start_master()
