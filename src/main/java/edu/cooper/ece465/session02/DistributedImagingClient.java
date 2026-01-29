package edu.cooper.ece465.session02;

import java.io.*;
import java.net.*;

public class DistributedImagingClient {
    public static void main(String[] args) throws IOException {

        String hostName = null;
        int portNumber = 0;

        if (args.length == 2) {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        } else {
            System.out.println("No args provided. Attempting to lookup 'ImagingService' via Naming Service...");
            edu.cooper.ece465.session04.naming.NamingClient naming = new edu.cooper.ece465.session04.naming.NamingClient();
            String address = naming.lookup("ImagingService");

            if (address != null) {
                String[] parts = address.split(":");
                hostName = parts[0];
                portNumber = Integer.parseInt(parts[1]);
                System.out.println("Found Service at: " + hostName + ":" + portNumber);
            } else {
                System.err.println(
                        "Could not find 'ImagingService'. Usage: java DistributedImagingClient <host name> <port number>");
                System.exit(1);
            }
        }

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}