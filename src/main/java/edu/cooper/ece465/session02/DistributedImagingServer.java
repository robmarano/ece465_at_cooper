package edu.cooper.ece465.session02;

import java.net.*;
import java.io.*;

/**
 * Session 02: Architectures
 * Topic: Client-Server & Multithreaded Servers (Tanenbaum Ch. 2.2 & 3.4)
 *
 * This Server implements the "Dispatcher-Worker" pattern.
 * The Main thread accepts connections (Dispatcher) and hands them off
 * to worker threads (DistributedImagingThread) to ensure we don't block.
 */
public class DistributedImagingServer {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java DistributedImagingServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        // [SESSION 04] Register with Naming Service
        try {
            edu.cooper.ece465.session04.naming.NamingClient naming = new edu.cooper.ece465.session04.naming.NamingClient();
            naming.register("ImagingService", "127.0.0.1", portNumber);
            System.out.println("Registered with Naming Service.");
        } catch (Exception e) {
            System.err.println("Warning: Could not register with Naming Service: " + e.getMessage());
        }

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new DistributedImagingThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}