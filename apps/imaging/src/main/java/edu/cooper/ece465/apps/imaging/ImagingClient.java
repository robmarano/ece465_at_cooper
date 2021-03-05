package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class ImagingClient {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingClient.class);


    public static void main(String[] args) {
         
        if (args.length != 2) {
            System.err.println(
                "Usage: java ImagingClient <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        InetAddress clientAddress = null;
        int clientPort = 0;
 
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
            DataInputStream dataInputStream = new DataInputStream(kkSocket.getInputStream());
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            clientAddress = kkSocket.getInetAddress();
            clientPort = kkSocket.getPort();
 
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye.")) {
                    try {
                        kkSocket.close();
                    } catch (IOException e) {
                        String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
                        Utils.handleException(LOG, e, errorMessage);
                    }
                    break;
                } else if (fromServer.startsWith("RECEIVE")) {
                    String fileName = fromServer.substring(9);
                    System.out.println(fileName);
                    fileName = "foo.sh";
                    Utils.receiveFile(fileName, dataInputStream);
                }
                 
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            String errorMessage = String.format("Don't know about host ",hostName);
            Utils.handleException(LOG, e, errorMessage);
        } catch (IOException e) {
            String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
            Utils.handleException(LOG, e, errorMessage);
        } catch (Exception e) {
            System.err.println("General exception caught" + e);
        }
    }
}