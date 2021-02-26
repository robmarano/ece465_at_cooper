package edu.cooper.ece465.apps.imaging;

import java.net.*;
import java.io.*;

import org.apache.log4j.Logger;

public class ImagingService {
    private static final Logger LOG = Logger.getLogger(ImagingService.class);

    public static void main(String[] args) throws IOException {
	 	System.out.println("Starting ImagingService...");

	    if (args.length != 1) {
	        System.err.println("Usage: java ImagingService <port number>");
	        System.exit(1);
	    }
	    else {
	        int portNumber = Integer.parseInt(args[0]);
	        boolean listening = true;
	         
	        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	            while (listening) {
	                new ImagingThread(serverSocket.accept()).start();
	            }
	        } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
	        }
	    }

		System.out.println("Finishing ImagingService.");
	}
}