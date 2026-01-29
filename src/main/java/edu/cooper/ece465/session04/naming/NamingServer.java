package edu.cooper.ece465.session04.naming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session 04: Naming
 * Topic: Service Discovery & Naming (Tanenbaum Ch. 5)
 *
 * This class implements a "Flat Naming" service.
 * It acts as a centralized registry where distributed entities (like
 * ImagingServer)
 * can register their Access Point (IP:Port) under a Name ("ImagingService").
 * This solves the problem of how entities find each other without hardcoding
 * IPs.
 */
public class NamingServer {
    private static final Logger LOG = LoggerFactory.getLogger(NamingServer.class);
    private static final int PORT = 1099;
    private static final ConcurrentHashMap<String, String> registry = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        LOG.info("Starting NamingServer on port {}", PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            LOG.error("NamingServer failed", e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    LOG.debug("Received: {}", inputLine);
                    String[] parts = inputLine.split(" ");
                    String command = parts[0];

                    if ("REGISTER".equalsIgnoreCase(command) && parts.length == 4) {
                        // REGISTER <SERVICE> <IP> <PORT>
                        String service = parts[1];
                        String address = parts[2] + ":" + parts[3];
                        registry.put(service, address);
                        LOG.info("Registered {} at {}", service, address);
                        out.println("OK");
                    } else if ("LOOKUP".equalsIgnoreCase(command) && parts.length == 2) {
                        // LOOKUP <SERVICE>
                        String service = parts[1];
                        String address = registry.get(service);
                        if (address != null) {
                            out.println(address);
                        } else {
                            out.println("404 NOT FOUND");
                        }
                    } else {
                        out.println("ERROR INVALID_COMMAND");
                    }
                }
            } catch (IOException e) {
                LOG.error("Error handling client", e);
            }
        }
    }
}
