from multiprocessing import Process, Pipe
import os

def sender(conn):
    msg = "Hello from Process " + str(os.getpid())
    conn.send(msg)
    for i in range(10):
        print(f"Process {os.getpid()} sending: {i}")
    conn.close()

def receiver(conn):
    msg = conn.recv()
    print(f"Process {os.getpid()} received: {msg}")
    for i in range(10):
        print(f"Process {os.getpid()} received: {i}")
    conn.close()

if __name__ == '__main__':
    # Create a pipe for communication
    parent_conn, child_conn = Pipe()
    
    # Create two separate processes
    p1 = Process(target=sender, args=(child_conn,))
    p2 = Process(target=receiver, args=(parent_conn,))
    
    # Start processes (OS creates independent address spaces)
    p1.start()
    p2.start()
    
    # Wait for completion
    p1.join()
    print(f"Process {p1.name} finished.")
    p2.join()
    print(f"Process {p2.name} finished.")
