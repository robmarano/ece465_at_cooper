import socket
import threading

def handle_client(client_socket, address):
    """
    Handles a single client connection.
    receives a number, squares it, and sends it back.
    """
    print(f"[Server] Accepted connection from {address}")
    
    with client_socket:
        while True:
            try:
                data = client_socket.recv(1024)
                if not data:
                    break
                
                message = data.decode('utf-8')
                print(f"[Server] Received from {address}: {message}")
                
                if message == "EXIT":
                    print(f"[Server] Client {address} requested exit.")
                    break
                
                try:
                    # Processing: Square the number
                    number = int(message)
                    result = number * number
                    response = f"Result: {result}"
                except ValueError:
                    response = "Error: Please send an integer."
                
                client_socket.sendall(response.encode('utf-8'))
                
            except ConnectionResetError:
                break
    
    print(f"[Server] Connection with {address} closed.")

def start_server(host='0.0.0.0', port=65432):
    """
    Starts the TCP server.
    """
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # Allow address reuse to avoid "Address already in use" errors during testing
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    
    try:
        server_socket.bind((host, port))
        server_socket.listen()
        print(f"[Server] Listening on {host}:{port}")
        
        while True:
            conn, addr = server_socket.accept()
            # Handle each client in a separate thread to allow multiple nodes
            client_thread = threading.Thread(target=handle_client, args=(conn, addr))
            client_thread.start()
            
    except KeyboardInterrupt:
        print("\n[Server] Stopping server...")
    finally:
        server_socket.close()

if __name__ == "__main__":
    start_server()
