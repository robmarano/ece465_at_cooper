package edu.cooper.ece465.session04.naming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamingClient {
    private static final Logger LOG = LoggerFactory.getLogger(NamingClient.class);
    private static final String NAMING_SERVER_HOST = "localhost";
    private static final int NAMING_SERVER_PORT = 1099;

    public void register(String serviceName, String ip, int port) {
        String response = sendCommand("REGISTER " + serviceName + " " + ip + " " + port);
        if ("OK".equals(response)) {
            LOG.info("Successfully registered {}", serviceName);
        } else {
            LOG.error("Failed to register {}: {}", serviceName, response);
        }
    }

    public String lookup(String serviceName) {
        String response = sendCommand("LOOKUP " + serviceName);
        if (response != null && !response.startsWith("404") && !response.startsWith("ERROR")) {
            LOG.info("Lookup {} -> {}", serviceName, response);
            return response;
        } else {
            LOG.warn("Lookup failed for {}: {}", serviceName, response);
            return null;
        }
    }

    private String sendCommand(String command) {
        try (Socket socket = new Socket(NAMING_SERVER_HOST, NAMING_SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            return in.readLine();
        } catch (IOException e) {
            LOG.error("Error connecting to NamingServer", e);
            return null;
        }
    }

    // Simple test main
    public static void main(String[] args) {
        NamingClient client = new NamingClient();
        client.register("ImagingService", "127.0.0.1", 8080);
        String addr = client.lookup("ImagingService");
        System.out.println("Resolved Address: " + addr);
    }
}
