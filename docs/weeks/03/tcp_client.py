import socket
import sys

def start_client(server_ip='127.0.0.1', port=65432):
    """
    Connects to the TCP server and sends data for processing.
    """
    print(f"[Client] Connecting to {server_ip}:{port}...")
    
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((server_ip, port))
            print("[Client] Connected.")
            
            while True:
                user_input = input("Enter a number to square (or 'EXIT' to quit): ")
                
                s.sendall(user_input.encode('utf-8'))
                
                if user_input.strip().upper() == 'EXIT':
                    break
                
                data = s.recv(1024)
                print(f"[Client] Server response: {data.decode('utf-8')}")
                
    except ConnectionRefusedError:
        print("[Client] Could not connect to server. Is it running?")
    except Exception as e:
        print(f"[Client] Error: {e}")

if __name__ == "__main__":
    # Allow user to specify IP via command line
    target_ip = sys.argv[1] if len(sys.argv) > 1 else '127.0.0.1'
    start_client(target_ip)
