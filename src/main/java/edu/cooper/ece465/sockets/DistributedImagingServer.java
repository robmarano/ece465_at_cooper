package edu.cooper.ece465.sockets;

import java.net.*;
import java.io.*;
 
public class DistributedImagingServer {
    public static void main(String[] args) throws IOException {
 
    if (args.length != 1) {
        System.err.println("Usage: java DistributedImagingServer <port number>");
        System.exit(1);
    }
 
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
         
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