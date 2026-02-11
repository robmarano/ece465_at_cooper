import socket

def start_udp_receiver(host='0.0.0.0', port=5005):
    """
    Listens for UDP messages.
    """
    print(f"[UDP Receiver] Listening on {host}:{port}")
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((host, port))
    
    try:
        while True:
            data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
            print(f"[UDP Receiver] Received message: '{data.decode('utf-8')}' from {addr}")
    except KeyboardInterrupt:
        print("\n[UDP Receiver] Stopping...")
    finally:
        sock.close()

if __name__ == "__main__":
    start_udp_receiver()
