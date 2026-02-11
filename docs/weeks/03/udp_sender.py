import socket
import time
import sys

def start_udp_sender(target_ip='127.0.0.1', port=5005):
    """
    Sends UDP broadcast/messages.
    """
    print(f"[UDP Sender] Sending to {target_ip}:{port}")
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    count = 0
    try:
        while True:
            message = f"Heartbeat {count} from sender"
            print(f"[UDP Sender] Sending: '{message}'")
            sock.sendto(message.encode('utf-8'), (target_ip, port))
            count += 1
            time.sleep(2)
    except KeyboardInterrupt:
        print("\n[UDP Sender] Stopping...")
    finally:
        sock.close()

if __name__ == "__main__":
    # If running on different nodes, specify the target IP (or broadcast address)
    target = sys.argv[1] if len(sys.argv) > 1 else '127.0.0.1'
    start_udp_sender(target)
