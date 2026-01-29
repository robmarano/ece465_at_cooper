package edu.cooper.ece465.distImage;

import java.net.*;
import java.io.*;
 
public class DistributedImagingThread extends Thread {
    private Socket socket = null;
 
    public DistributedImagingThread(Socket socket) {
        super("DistributedImagingThread");
        this.socket = socket;
    }
     
    public void run() {
 
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            DistributedImagingProtocol dip = new DistributedImagingProtocol();
            outputLine = dip.processInput(null);
            out.println(outputLine);
 
            while ((inputLine = in.readLine()) != null) {
                outputLine = dip.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}