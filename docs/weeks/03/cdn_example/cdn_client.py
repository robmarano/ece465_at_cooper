import socket
import json
import sys

MASTER_HOST = '127.0.0.1'
MASTER_PORT = 65432

def send_request(action, **kwargs):
    """Sends a JSON request to the master."""
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((MASTER_HOST, MASTER_PORT))
            payload = {"action": action}
            payload.update(kwargs)
            s.sendall(json.dumps(payload).encode('utf-8'))
            response = s.recv(4096)
            return json.loads(response.decode('utf-8'))
    except ConnectionRefusedError:
        return {"status": "error", "message": "Could not connect to Master"}

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 cdn_client.py [register|query] [image_id] [location]")
        sys.exit(1)

    command = sys.argv[1]
    image_id = sys.argv[2] if len(sys.argv) > 2 else "img_test"
    
    if command == "register":
        location = sys.argv[3] if len(sys.argv) > 3 else "/tmp/uploads/img_test.jpg"
        print(f"Registering {image_id} at {location}...")
        res = send_request("register_image", image_id=image_id, location=location)
        print(f"Response: {res}")
        
    elif command == "query":
        print(f"Querying {image_id}...")
        res = send_request("query_image", image_id=image_id)
        print(f"Response: {res}")
        
    else:
        print(f"Unknown command: {command}")
